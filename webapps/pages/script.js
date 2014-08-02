/**
 *
 * Date: Fri 1st Aug 9:09 PM (+1 Zurich OTC)
 * Author: youyix
 * Title: Fix bugs and logic. SAA is partly fine.
 *
 *
**/

// A common BID object 
var BID = {

    setTimer: setTimer,
    lockScreen: lockScreen,
    unlockScreen: unlockScreen,
    loading: loading,
    submitAuction: submitAuction,
    isTimeUp: isTimeUp
}

// A SAA-BID object, 'subclass' of BID
var SAA = {
    update: saaUpdate,
    validateInput: saaValidateInput,
    validateAllInput: saaValidateAllInput,
    setAll2Valid: saaSetAll2Valid,
    collectData: saaCollectData
}

// A CCA-BID object, 'subclass' of BID
var CCA = {
    update: ccaUpdate,
    validateInput: ccaValidateInput,
    collectData: ccaCollectData,
    validateAllInput: ccaValidateAllInput,
    setAll2Valid: ccaSetAll2Valid
}

function init() {
    SAA = $.extend(true, SAA, BID);
    CCA = $.extend(true, CCA, BID);
    $("#bid_table").data("type", "BID");
    $("#bid_table").data("object", BID);
}

$(document).ready(function(){
    
    init();

	$("#button").click(function(){
        BID.lockScreen();
		$name = $("#name").val();

		if ( ! checkUsername($name) ) {
			$("#login_error_tips").show();
			return;
		}
		$("#login_error_tips").hide();
 	    $.ajax({
   	    	type: "GET",
            url: "/WEB-INF/login.xml?name={0}&ip={1}".f(getName(), getIp()),
           	dataType: "xml",
          	success: switchTo,
          	error: loginError
        });
        console.log("Name:{0}\nIP:{1}".f(getName(), getIp()));
  	});

	// #
	$("#submit_auction").click(function() {
        console.log("CLICK", getBid());
		getBid().submitAuction();
	});

	$("#name").keypress(function( event ) {
		if ( event.which == 13 ) {
			event.preventDefault();
			$("#button").click();
		}
	});
});



function switchTo(data) {
    BID.unlockScreen();
    console.log("switchTo");
    var $context = ($(data)).find("auction_context");
    var $type = $context.children("type").attr("value");
    console.log("Type", $type);
    // Xing: get $type's string value, if directly compare $type to a string, it always returns unequal..
    var typeString = $type; //so store the value into typeString variable, which is string type.

    if (  typeString === "SAA" ) {
        console.log("Yes! this is SAA!");
        $("#bid_table").data("type", "SAA");
        $("#bid_table").data("object", SAA);
        SAA.update(data);


    } else if ( typeString === "CCA" )  {
        console.log("Yes! this is CCA!");
        $("#bid_table").data("type", "CCA");
        $("#bid_table").data("object", CCA);
        CCA.update(data);


    } else if ( $type == undefined){
        console.log("ELSE");
    }


}



/**  SAA ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
function saaUpdate(data) {
	$("#login_box").hide();
	console.log("SAAupdate()");

	console.log('type: ' + $.type(data));
    if ( $.type(data) === "string" ) {
        data = $.parseXML(data);
    }

	var $context = ($(data)).find("auction_context");
	var $roundNumber = $context.children("round").attr("value");
	var $isFinal = $context.children("round").attr("final");
    $isFinal = ( $isFinal === "yes" ) ? true : false; 
    // $isFinal = true;
	var $minIncreament = $context.children("minimum_increament").attr("value");
	$("#bid_table").data("minIncreament", $minIncreament);

	console.log("Round {0}  Final? {1}".f($roundNumber, $isFinal));

	$("#round_infomation").html("Round {0}  <b>{1}</b>  <i>Min Increament {2}</i>"
		.f($roundNumber, $isFinal===true ? "<b class='alert'>Final!</b>" : "", $minIncreament));

	SAA.setTimer($context.children("duration").attr("value"));

	$("#bid_table").find("tbody").find("tr").remove();
    $("#bid_table").find("thead").find("tr").remove();

    if ( ! $isFinal ) {
        $("#bid_table").find("thead").append("<tr><th>Item</th><th>Price</th><th>Your price</th></tr>");
    } else {
        $("#bid_table").find("thead").append("<tr><th>Item</th><th>Price</th><th>Owner</th></tr>");
    }  

	$context.find("item").each(function(i) {
		console.log(this);
		var $itemId = $(this).attr("id");
		var $itemName = $(this).attr("name");
		var $price = $(this).attr("price");
		var $owner = $(this).attr("owner");
        // $owner = "123";

		console.log($itemId, $itemName, $price, $owner);

		var $_id = $("<th class='invisible'></th>").text($itemId);
		var $_name = $("<th></th>").text($itemName);
		var $_price = $("<th></th>").text($price);

        // opt 1
        var $_owner = $("<th></th>").text($owner);
        // opt 2
		var $_yprice = $("<th></th>").append($("<input type='text' id='price{0}' class='input_price' value='{1}'></input>"
			.f($itemId, 0+S2N($price))));
		$_yprice.append($("<p id=price{0}_tips class='input_price_tips'></p>".f($itemId)));

        var $item;

        if ( $isFinal ) {
            $item = $("<tr></tr>").append($_id, $_name, $_price, $_owner);
        } else {
            $item = $("<tr></tr>").append($_id, $_name, $_price, $_yprice);
        }
		$("#bid_table").find("tbody").append($item);
	});

	$('#bid_table tbody tr').hover(function() {
        $(this).addClass('zhover');
    }, function() {
        $(this).removeClass('zhover');
    });

    if ( ! $isFinal ) {
        $(".input_price").change(function() {
            SAA.validateInput($(this));
        });

        $(".input_price").keypress(function( event ) {
            if ( event.which == 13 ) {
                event.preventDefault();
                $("#submit_auction").click();
            }
        });
    }

	console.log("Finish paint");
	$("#bid").show();
}

function saaCollectData() {
    console.log("saaCollectData");
    var $bid = "";
    var $items = "";
    $("#bid_table").find("tbody").find("tr").each(function(i) {
        var $id = $(this).children("th:eq(0)").text();
        var $name = $(this).children("th:eq(1)").text();
        var $yprice = $("#price{0}".f($id)).val();
        var $owner = " ";
        $items = $items + "<item id='{0}' name='{1}' price='{2}' owner='{3}'></item>".f($id, $name, $yprice, $owner);
    });
    $bid = "<?xml version='1.0' encoding='utf-8'?><bid><bidder name='{0}' ip='{1}' /><item_list>".f($name, $ip) + $items + "</item_list></bid>";

    return $bid;
}

// Validate the user input
function saaValidateInput(input) {
    var $valid = true;
    var $item = input.parents("tr");

    var $id = $item.children("th:eq(0)").text();
    var $name = $item.children("th:eq(1)").text();
    var $price = $item.children("th:eq(2)").text();
    var $yprice = $("#price{0}".f($id)).val();
    var $minIncreament = S2N($("#bid_table").data("minIncreament"));
    console.log("validateInput", $id, $name, $price, $yprice);

    if ( hasInvalidCharacters($yprice) ) {
        $("#price{0}_tips".f($id)).text("Please enter a positive number.");
        $("#price{0}_tips".f($id)).show();
        $valid = false;
    } else {
        $("#price{0}_tips".f($id)).hide();
        if ( S2N($yprice) < S2N($price) + $minIncreament ) {
            $("#price{0}_tips".f($id)).html("Your are aborting this bid.");
            $("#price{0}_tips".f($id)).show();
            $valid = false;
        } 
    }
    return $valid;

    function hasInvalidCharacters(str) {
        return str.search(/[^0-9]/) != -1;
    }
}

function saaValidateAllInput() {
    console.log("validateAllInput");
    var $valid = true;
    
    $(".input_price").each(function(i) {
        if ( SAA.validateInput($(this)) === false ) {
            $valid = false;
        }
    });

    console.log("All", $valid, $count);
    return $valid;
}

function saaSetAll2Valid() {
    console.log("setAll2Valid");
    
    $(".input_price").each(function(i) {
        $(this).val(0);
    });
}
/** END OF SAA --------------------------------------------------------*/




/** CCA ++++++++++++++++++++++++++++++++++++++++++++++*/

function ccaUpdate(data) {
    $("#login_box").hide();
    console.log("SAAupdate()");

    console.log('type: ' + $.type(data));
    if ( $.type(data) === "string" ) {
        data = $.parseXML(data);
    }

    var $context = ($(data)).find("auction_context");
    var $roundNumber = $context.children("round").attr("value");
    var $isFinal = $context.children("round").attr("final");  
    $isFinal = ( $isFinal === "yes" ) ? true : false; 
    var $minIncreament = $context.children("minimum_increament").attr("value"); 

    console.log("Round {0}  Final? {1}".f($roundNumber, $isFinal));

    $("#round_infomation").html("Round {0}  <b>{1}</b>  <i>Min Increament {2}</i>"
        .f($roundNumber, $isFinal ? "<b class='alert'>Final!</b>" : "", $minIncreament));
    
    SAA.setTimer($context.children("duration").attr("value"));

    $("#bid_table").find("thead").find("tr").remove();

    if ( $isFinal ) {
        $("#bid_table").find("thead").append("<tr><th>Item</th><th>Price</th><th>Amout</th><th>Owner(s)</th></tr>");
    } else {
        $("#bid_table").find("thead").append("<tr><th>Item</th><th>Price</th><th>Amout</th><th>Your amout</th><th>Your price</th></tr>");
    }  

    $("#bid_table").find("tbody").find("tr").remove();

    $context.find("item").each(function(i) {
        console.log(this);
        var $itemId = $(this).attr("id");
        var $itemName = $(this).attr("name");
        var $price = $(this).attr("price");
        var $quantityAmount = $(this).attr("quantity_amount");

        console.log($itemId, $itemName, $price, $quantityAmount, $owner);

        var $_id = $("<th class='invisible'></th>").text($itemId);
        var $_name = $("<th></th>").text($itemName);
        var $_price = $("<th></th>").text($price);
        var $_amount = $("<th></th>").text($quantityAmount);
        var $_yprice = $("<th id='cca_price{0}'></th>".f($id)).text("");

        // opt 1
        
        var $str = "";
        $(this).find("owner").each(function(i) {
            str = str + "<p>" + $(this).attr("name") + "(" + $(this).attr("quantity") + ")" + "</p>";
        });
        var $_owners = $("<th></th>").html(str);
        // opt 2
        var $_yamount = $("<th></th>").append($("<input type='text' id='amount{0}' class='input_amount' value='0'></input>").f($itemId));
        $_yamount.append($("<p id=amount{0}_tips class='input_amount_tips'></p>".f($itemId)));


        var $item;
        if ( $isFinal ) {
            $item = $("<tr></tr>").append($_id, $_name, $_price, $_amount, $_owners);
        } else {
            $item = $("<tr></tr>").append($_id, $_name, $_price, $_amount, $_yamount, $_yprice);
        }
        

        $("#bid_table").find("tbody").append($item);
    
    }); 

    $('#bid_table tbody tr').hover(function() {

        $(this).addClass('zhover');
    }, function() {

        $(this).removeClass('zhover');
    });

    if ( ! $isFinal ) {
        $(".input_amount").change(function() {
            if ( CCA.validateInput($(this)) ) {
                var $item = $(this).parents("tr");
                var $id = $item.children("th:eq(0)").text();
                var $yamount = $("#yamount{0}".f($id)).val();
                var $price = $item.children("th:eq(2)").text();
                $("cca_price{0}".f($id)).val(S2N($yamount) * S2N($price));
            }
        });

        $(".input_amount").keypress(function( event ) {
            if ( event.which == 13 ) {
                event.preventDefault();
                $("#submit_auction").click();
            }
        });
    }
    

    $("#bid").show();
}

function ccaCollectData() {
    console.log("ccaCollectData");
    var $bid = "";
    var $items = "";

    $("#bid_table").find("tbody").find("tr").each(function(i) {
        
        var $id = $(this).children("th:eq(0)").text();
        var $name = $(this).children("th:eq(1)").text();
        var $yamount = $("#yamount{0}".f($id)).val();

        $items = $items + "<item id={0} name='{1}' quantity_require='{2}'></item>".f($id, $name, $yamount);
    });
    
    $bid = "<bid><bidder name='{0}' ip='{1}' /><item_list>".f($name, $ip) + $items + "</item_list></bid>";
    return $bid;
}

function ccaValidateInput(input) {
    
    var $valid = true;
    var $item = input.parents("tr");

    var $id = $item.children("th:eq(0)").text();
    var $name = $item.children("th:eq(1)").text();
    var $price = $item.children("th:eq(2)").text();
    var $amount = $item.children("th:eq(3)").text();
    var $yamount = $("#yamount{0}".f($id)).val();

    console.log("validateInput", $id, $name, $price, $yamount);

    if ( hasInvalidCharacters($yamount) ) {

        $("#amount{0}_tips".f($id)).text("Please enter a positive integer number.");
        $("#amount{0}_tips".f($id)).show();
        $valid = false;
    } else {
  
        $("#amount{0}_tips".f($id)).hide();

        if ( S2N($yamount) > S2N($amount) ) {

            $("#amount{0}_tips".f($id)).html("You cannot have more than {0}.".f($amount));
            $("#amount{0}_tips".f($id)).show();
            $valid = false;
        } 
    }
    return $valid;

    function hasInvalidCharacters(str) {
        return str.search(/[^0-9]/) != -1 ;

    }  
}

function ccaValidateAllInput() {
    console.log("ccaValidateAllInput");
    var $valid = true;
    
    $(".input_price").each(function(i) {
        if ( CCA.validateInput($(this)) === false ) {
            $valid = false;
        }
    });

    console.log("All", $valid, $count);
    return $valid;
}

function ccaSetAll2Valid() {
    console.log("ccaSetAll2Valid");
    
    $(".input_price").each(function(i) {
        $(this).val(0);
    });
}


/** END OF CCA --------------------------------------------------------*/






/** BID ++++++++++++++++++++++++++++++++++++++++++++++*/
function setTimer(timeToCount) {
    console.log("setTimer");
	$count = timeToCount;
	$timer = setInterval(function() {
		$("#timer").text("Time out after {0} seconds.".f($count));
		$count = $count - 1;

		if ( $count == 0 ) {
            clearInterval($timer);
			getBid().submitAuction();
		}
	}, 1000);
}

// Post xml to server
function submitAuction() {

    console.log("SAAsubmitAuction");

    if ( ! getBid().validateAllInput() ) {
        if ( getBid().isTimeUp() ) {
            getBid().setAll2Valid();        
        } else {
            return;
        }
    }

    $(".input_price").next("p").hide();
    $(".input_amount").next("p").hide();

    $("#timer").text("Your bid is being submitting...");
    // collect data
    var $xmlData = getBid().collectData();
    
    clearInterval($timer);

    console.log("collected_data", $xmlData);

    console.log("Before lockScreen", getType());
    getBid().lockScreen();

    // other ....

    // $.ajax({
    //     url: '/WEB-INF/bid.xml', 
    //     // processData: false,
    //     // async: true,
    //     // contentType: "text/xml",
    //     dataType: "xml",
    //     type: "POST",  
    //     data: $xmlData, 
    //     success: function(response) {
    //         console.log("success");
    //         getBid().unlockScreen();
    //         getBid().update(response);
    //     },
    //     error: function(xhr, status, error) {
    //         console.log("FAIL TO GET RESPONESE FROM SERVER");
    //         console.log(status, xhr.responseText);
    //         console.log("xhr", xhr);
    //         console.log("error", error);
    //         getBid().unlockScreen();
    //         updateError(xhr);
    //     },
    //     complete: function(response, status) {
    //         console.log("ajax finished", status);
    //     }
    // });

    ///  
    $.post('/WEB-INF/bid.xml', $xmlData)
        .done(function(data, status, error) {
            console.log("OK", status);
            console.log("data", data);
            console.log(error);

            getBid().unlockScreen();
            getBid().update(data);
        })
        .fail(function(data, status, error) {
            console.log("NO", status);
            console.log(data);
            console.log(error);
    });

}


function isTimeUp(){
    console.log("count", $count);
    return $count <= 0 ? true : false;
}


// ++++
function lockScreen() {
    console.log("lock");
    $('body').css('overflow-y', 'hidden');
    $('<div id="overlay"></div>')
        .css('top', 0)
        .css('opacity', '0')
        .animate({'opacity': '0.8'}, 'slow')
        .appendTo('body');

    $('<div id="lock_screen"></div>')
        .hide()
        .appendTo('body');

    $('<img>')
        .attr('src', "./loader.gif")
        .load(function() {
            getBid().loading();
        })
        .click(function() {
            // unlockScreen();
        })
        .appendTo('#lock_screen');
    console.log("End of lockScreen");

}

function loading() {
    console.log("loading");
    var top = (screen.availHeight - $('#lock_screen').height()) / 2;
    var left = ($(window).width() - $('#lock_screen').width()) / 2;
    // console.log($(window).height(), $(window).width());
    // console.log(screen.availHeight, screen.availWidth);
    // console.log($('#lock_screen').height(), $('#lock_screen').width());
    // console.log(top, left);
    $('#lock_screen')
        .css({
            'top': top-200,
            'left': left
    }).fadeIn();

}

function unlockScreen() {
    $('#overlay, #lock_screen')
        .fadeOut('slow', function() {
            $(this).remove();
            $('body').css('overflow-y', 'auto'); 
         });     
}

/** END OF BID --------------------------------------------------------*/



/** PUBLIC ++++++++++++++++++++++++++++++++++++++++++++++*/
function updateError() {
    $("#timer").text("Update error.");
    $("#submit_auction").prop("disabled", true);
    
    $("#submit_auction").removeClass("submit_button");
    $("#submit_auction").addClass("disabled_button");
}


// Helper function for formatting strings
// Usage: "sasdsdad{0}das{1}".f("ads", 2);
String.prototype.format = String.prototype.f = function() {
    var s = this,
        i = arguments.length;

    while (i--) {
        s = s.replace(new RegExp('\\{' + i + '\\}', 'gm'), arguments[i]);
    }
    return s;
};

// Helper function: convert string to number
function S2N(str) {
    return parseInt(str);
}

function loginError() {
    $(".login_error").show();
    BID.unlockScreen();
}

function checkUsername(name) {
    if ( $.trim(name) == "" ) {
        $("#login_error_tips").html("<b class='alert'>Please enter a username.</b>");
        return false;
    }
    return true;
}

function getName() {
    return $name;
}

function getIp() {
    $ip = "127.0.0.1";
    $.ajax({
        type: "GET",
        url: "http://jsonip.com",
        dataType: "json",
        async: false,
        success: function(res) {
            $ip = res.ip;
        },
        error: function(res) {
        }
    });
    return $ip;
}

function getBid() {
    return $("#bid_table").data("object");
}

function getType() {
    return $("#bid_table").data("type");
}

/** END OF PUBLIC --------------------------------------------------------*/
