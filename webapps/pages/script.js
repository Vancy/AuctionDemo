/**
 *
 * Date: Wed Jul 30 5:25 PM
 * Author: youyix
 * Title: Refactory code. Add CCA.
 *
 *
**/

var BID = {

    setTimer: setTimer,
    lockScreen: lockScreen,
    unlockScreen: unlockScreen,
    loading: loading,
    submitAuction: submitAuction,
    isTimeUp: isTimeUp
}

var SAA = {
    update: saaUpdate,
    validateInput: saaValidateInput,
    validateAllInput: saaValidateAllInput,
    setAll2Valid: saaSetAll2Valid,
    collectData: saaCollectData
}

var CCA = {
    update: ccaUpdate,
    validateInput: ccaValidateInput,
    collectData: ccaCollectData,
    validateAllInput: ccaValidateAllInput,
    setAll2Valid: ccaSetAll2Valid
}



$(document).ready(function(){
    SAA = $.extend(true, SAA, BID);
    CCA = $.extend(true, CCA, BID);

	$("#button").click(function(){
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

	console.log(data);

	var $context = ($(data)).find("auction_context");
	var $roundNumber = $context.children("round").attr("value");
	var $isFinal = $context.children("round").attr("final");
	var $minIncreament = $context.children("minimum_increament").attr("value");
	$("#bid_table").data("minIncreament", $minIncreament);



	console.log("Round {0}  Final? {1}".f($roundNumber, $isFinal));

	$("#round_infomation").html("Round {0}  <b>{1}</b>  <i>Min Increament {2}</i>"
		.f($roundNumber, $isFinal==="yes" ? "<b class='alert'>Final!</b>" : "", $minIncreament));

	SAA.setTimer($context.children("duration").attr("value"));

	$("#bid_table").find("tbody").find("tr").remove();

	$context.find("item").each(function(i) {
		console.log(this);
		var $itemId = i;
		var $itemName = $(this).attr("name");
		var $price = $(this).attr("price");
		var $owner = $(this).attr("owner");


		console.log($itemId, $itemName, $price, $owner);

		var $_id = $("<th class='invisible'></th>").text($itemId);
		var $_name = $("<th></th>").text($itemName);
		var $_price = $("<th></th>").text($price);

        // opt 1
        var $_owner = $("<th></th>").text($owner);
        // opt 2
		var $_yprice = $("<th></th>").append($("<input type='text' id='price{0}' class='input_price' value='{1}'></input>"
			.f($itemId, S2N($minIncreament)+S2N($price))));
		$_yprice.append($("<p id=price{0}_tips class='input_price_tips'></p>".f($itemId)));


        console.log("IIIITTTEEEMM", $isFinal);
        var $item;

        if ( $isFinal == "yes") {
            $item = $("<tr></tr>").append($_id, $_name, $_price, $_owner);
        } else {
            console.log("NOT FIN");
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

        $items = $items + "<item name=\"{0}\" price=\"{1}\" owner=\"{2}\"></item>".f($name, $yprice, $owner);
    });

    $bid = "<bid><bidder name='{0}' ip='{1}' /><item_list>".f($name, $ip) + $items + "</item_list></bid>";

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
        // input.addClass("invalid_input");
        $("#price{0}_tips".f($id)).text("Please enter a positive number.");
        $("#price{0}_tips".f($id)).show();
        $valid = false;
    } else {
        // input.removeClass("invalid_input");
        // input.addClass("invalid_price");
        $("#price{0}_tips".f($id)).hide();

        if ( S2N($yprice) < S2N($price) + $minIncreament ) {
            // input.addClass("invalid_price");

            $("#price{0}_tips".f($id)).html("Your are aborting this bid.");
            $("#price{0}_tips".f($id)).show();
            $valid = false;
        } 
    }
    return $valid;

    function hasInvalidCharacters(str) {
        return str.search(/[^0-9]/) != -1 ;

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
        if ( ! SAA.validateInput($(this)) ) {
            $(this).val(0);
        }
    });
}
/** END OF SAA --------------------------------------------------------*/




/** CCA ++++++++++++++++++++++++++++++++++++++++++++++*/

function ccaUpdate(data) {
    $("#login_box").hide();
    console.log("SAAupdate()");

    console.log(data);

    var $context = ($(data)).find("auction_context");
    var $roundNumber = $context.children("round").attr("value");
    var $isFinal = $context.children("round").attr("final");   
    var $minIncreament = $context.children("minimum_increament").attr("value"); 

    console.log("Round {0}  Final? {1}".f($roundNumber, $isFinal));

    $("#round_infomation").html("Round {0}  <b>{1}</b>  <i>Min Increament {2}</i>"
        .f($roundNumber, $isFinal==="yes" ? "<b class='alert'>Final!</b>" : "", $minIncreament));
    
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
        var $itemId = i;
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

        $items = $items + "<item name='{0}' price='{1}' quantity_require='{2}'></item>".f($name, $yprice, $owner);
    });
    
    $bid = "<bid><bidder name='{0}' ip='{1}'><item_list>".f($name, $ip) + $items + "</item_list></bid>";
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
        if ( ! CCA.validateInput($(this)) ) {
            $(this).val(0);
        }
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

    // $(".input_price").removeClass("invalid_input");
    // $(".input_price").removeClass("invalid_price");
    

    $(".input_price").next("p").hide();
    $("input_amount").next("p").hide();

    $("#timer").text("Your bid is being submitting...");
    // collect data
    var $xmlData = getBid().collectData();
    
    clearInterval($timer);

    console.log($xmlData);

    console.log("Before lockScreen", getType());
    getBid().lockScreen();

    // other ....

    $.ajax({
        url: '/WEB-INF/bid.xml', 
        processData: false,
        async: true,
        contentType: "text/xml",
        cache: false,
        dataType: "xml",
        type: "POST",  // type should be POST
        data: $xmlData, // send the string directly
        success: function(response) {
            console.log("success");
            getBid().unlockScreen();
            update(response);
        },
        error: function(response) {
            //################### ######
            console.log("FAIL TO GET RESPONESE FROM SERVER");
            getBid().unlockScreen();
            updateError(response);
        },
        complete: function(response) {
            // ...
        }
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
    console.log($(window).height(), $(window).width());
    console.log(screen.availHeight, screen.availWidth);
    console.log($('#lock_screen').height(), $('#lock_screen').width());
    console.log(top, left);
    $('#lock_screen')
        .css({
            'top': top-200,
            'left': left
    })
.fadeIn();

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
    $("#timer").text("FAIL TO GET RESPONESE FROM SERVER");
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
    // other ....
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
