$(document).ready(function(){
	// # This funciton below is only to test the exist of jQuery.
	// $("h1").click(function(){
	// 	$(this).css("color", "red");
	// });

	// # When the 'login button clicked', script below is called.
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
          	success: update,
          	error: loginError
        });
        console.log("Name:{0}\nIP:{1}".f(getName(), getIp()));
  	});

	// # 
	$("#submit_auction").click(function() {
		submitAuction();
	});

	// $("#name").focus(function() {
	// 	if ( $(this).val() === "input your name" ) {
	// 		$(this).val("");
	// 	}
	// });

	// $("#name").focusout(function() {
	// 	if ( $.trim($("this").val()) === "" ) {
	// 		$(this).val("input your name");
	// 	}
	// });

	$("#name").keypress(function( event ) {
		if ( event.which == 13 ) {
			event.preventDefault();
			$("#button").click();
		}
	});


});

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

function update(data) {
	$("#login_box").hide();
	console.log("update()");

	console.log(data);

	var $context = ($(data)).find("auction_context");
	var $roundNumber = $context.children("round").attr("value");
	var $isFinal = $context.children("round").attr("final");
	var $minIncreament = $context.children("minimum_increament").attr("value");
	$("#bid_table").data("minIncreament", $minIncreament);

	// alert("this roundnumber is:" + $roundNumber);
	

	console.log("Round {0}  Final? {1}".f($roundNumber, $isFinal));

	$("#round_infomation").html("<p>Round {0}  {1}  Min_increament {2}</p>"
		.f($roundNumber, $isFinal==="yes" ? "<b class='alert'>Final!</b>" : "", $minIncreament));
	
	setTimer($context.children("duration").attr("value"));

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
		var $_yprice = $("<th></th>").append($("<input type='text' id='price{0}' class='input_price' value='{1}'></input>"
			.f($itemId, S2N($minIncreament)+S2N($price))));
		$_yprice.append($("<p id=price{0}_tips class='input_price_tips'></p>".f($itemId)));

		var $item = $("<tr></tr>").append($_id, $_name, $_price, $_yprice);

		$("#bid_table").find("tbody").append($item);

		
	});	

	$(".input_price").change(function() {
		validateInput($(this));
	});

	$('#bid_table tbody tr').hover(function() {

        $(this).addClass('zhover');
    }, function() {

        $(this).removeClass('zhover');
    });

    $(".input_price").keypress(function( event ) {
		if ( event.which == 13 ) {
			event.preventDefault();
			$("#submit_auction").click();
		}
	});

	$("#bid").show();
}


function setTimer(timeToCount) {
	$count = timeToCount;
	$timer = setInterval(function() {
		$("#timer").text("Time out after {0} seconds.".f($count));
		$count = $count - 1;

		if ( $count == 0 ) {
			submitAuction();
		}
	}, 1000);
}

function collectData() {
	var $bid = "";
	var $items = "";

	$("#bid_table").find("tbody").find("tr").each(function(i) {
		
		var $id = $(this).children("th:eq(0)").text();
		var $name = $(this).children("th:eq(1)").text();
		var $yprice = $("#price{0}".f($id)).val();
		var $owner = " ";

		$items = $items + "<item name={0} price={1} owner={2}></item>".f($name, $yprice, $owner);
	});
	
	$bid = "<bid><item_list>" + $items + "</item_list></bid>";
	return $bid;
}


function updateError() {

}


// Post xml to server
function submitAuction() {

	console.log("submitAuction");

	if ( ! validateAllInput() ) {
		if ( isTimeUp() ) {
			setAll2Valid();		
		} else {
			return;
		}
	}

	// $(".input_price").removeClass("invalid_input");
	// $(".input_price").removeClass("invalid_price");
	

	$(".input_price").next("p").hide();

	$("#timer").text("Your bid is being submitting...");
	// collect data
	var $xmlData = collectData();
	
	clearInterval($timer);

	console.log($xmlData);


	// other ....

	$.ajax({
		url: 'test.jsp', 
		processData: false,
		async: false,
		contentType: "text/xml",
		cache: false,
        dataType: "xml",
		type: "POST",  // type should be POST
		data: $xmlData, // send the string directly
		success: function(response) {
			update(response);
		},
		error: function(response) {
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

// Validate the user input
function validateInput(input) {
	
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

function validateAllInput() {
	console.log("validateAllInput");
	var $valid = true;
	
	$(".input_price").each(function(i) {
		if ( validateInput($(this)) === false ) {
			$valid = false;
		}
	});

	console.log("All", $valid, $count);
	return $valid;
}

function setAll2Valid() {
	console.log("setAll2Valid");
	
	$(".input_price").each(function(i) {
		if ( ! validateInput($(this)) ) {
			$(this).val(0);
		}
	});
}



