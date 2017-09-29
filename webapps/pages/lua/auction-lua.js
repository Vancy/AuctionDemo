Polymer('auction-lua', {
  localIP: "127.0.0.1",
  username: "",
  lua: {},

  items: [],
  auctionTableStarted: false,
  isFinal: false,
  currentLuaValuationsMessage: "",
  valuationVector: [],
  data: "",

  updateUrl: "",
  timer: undefined,

  domReady: function() {
	this.$.submitInterests.disable = false;
  },


  setUp: function(username, localIP) {
    this.username = username;
    this.localIP = localIP;
    this.updateUrl = "/WEB-INF/update.xml?name=" + this.username + "&ip=" + this.localIP;
    this.$.table.setUp(username, localIP);
    console.log("LUA-setup");
  },

  setData: function(data) {
    this.lua = data;
    this.initialize();

    // Start the ajax timer when it is the first time.
    var self = this;
    this.timer = setInterval(function() {
        self.$.period.go()
    }, 1000);
  },

  update: function(e) {
    // console.log("update!!", e, e.detail, e.detail.xhr);

    if ( e.detail.xhr.status != 200 ) {
        clearInterval(this.timer);
        console.log("Unable to connect to server. Status code: ", e.detail.xhr.status);
        this.$.status.innerHTML = "<b class='error'>Error. Cannot connect to the server.</b>";
        return;
    }
    var json = JSON.parse(e.detail.xhr.response);
    this.lua = json;
    this.updateInformation();
    //update the status of the bids are submitted.
    if (this.$.table.myBidsSubmitted == true) {
      this.$.status.innerHTML = "<b>Your bids have been submitted!</b>";
    }
  },

  initialize: function() {
  //this function is only used by first prepare the web page.
    console.log("initialization");
    this.items = this.lua.itemList;
  },

  updateInformation: function() {
    console.log("update info");
    var currentRound = this.lua.round;
    this.isFinal = this.lua.finalRound;
    if (currentRound <= 0) {
      //we only update the item price when auction is not started yet.
      this.auctionTableStarted = false;
    } else {
      if (!this.auctionTableStarted) {
        this.$.table.startAuction(this.lua);
	this.auctionTableStarted = true;
	console.log("auction starts");
      }
    }
    if (this.isFinal) {
      this.$.results.innerHTML = this.getMyResultMsg();
    }
    this.$.valuations.innerHTML = this.getMyValuationMsg();
  },

  getMyResultMsg: function() {
    var bidderList = this.lua.bidderList.list;
    var results = "";
    for (var i = 0; i < bidderList.length; i++) {
	if (bidderList[i].ipAddress === this.localIP &&	bidderList[i].name === this.username) {
	  //We get the result message, not display immediately. Instead, we first calculate the payoff, then display.
	  var winningList = bidderList[i].luaWinningMessage.split("<br/>");
	  // the last one is an empty string, so ignore that.
          for (var j=0; j<winningList.length-1; j++) {
	    var infoList = winningList[j].split(" ");
	    var itemID = parseInt(infoList[0]);
            var itemName = infoList[1];
            var itemPrice = infoList[2];
            var winType = infoList[3];
	    var myWinPrice = parseFloat(infoList[4]);
	    myPayoff = calculatePayoff(this, itemID, winType, myWinPrice);
	    results += "<tr><td>" + itemName + "</td><td>" + itemPrice + "</td><td>" + winType + "</td><td>" + myWinPrice + "</td><td>" + myPayoff + "</td></tr>";
	  }
	  return "<b>My winning results:</b><br/><table><tr> <td>Item</td>  <td>ItemPrice</td> <td>Win   Type</td> <td>WinPrice</td>  <td>Payoff</td> </tr>" + results + "</table>";
	} else {
	  continue;
	}
    }
    return results;

    function calculatePayoff(self, itemID, winType, myPrice) {
        //console.log("vars:", itemID, winType, myPrice);
        var index;
	if (winType === "(LicencedWin)") {
	  index = itemID * 2;
        } else if (winType === "(UnlicencedWin)") {
	  index = itemID * 2 + 1;
        } else {
	console.log(winType);
        }
	if (self.valuationVector === "") {
	  return myPrice;
 	}
	myValuation = self.valuationVector[index];
	  return myValuation - myPrice;
    }
  },

  getMyValuationMsg: function() {
    var bidderList = this.lua.bidderList.list;
    for (var i = 0; i < bidderList.length; i++) {
	if (bidderList[i].ipAddress === this.localIP &&	bidderList[i].name === this.username) {
          if (this.currentLuaValuationsMessage == bidderList[i].luaValuationsMessage) {
	    //needn't update default disable bid button
	  } else {
	    this.reflectZeroValuationToDisableBidButton(bidderList[i].luaValuationsMessage);
	    this.currentLuaValuationsMessage = bidderList[i].luaValuationsMessage;
	  }
	  return (this.currentLuaValuationsMessage === "")? "" : "Item valuations:<br/>" + bidderList[i].luaValuationsMessage;
	} else {
	  continue;
	}
    }
    return "";
  },

  reflectZeroValuationToDisableBidButton: function(valuationMessage) {
     var floatRegex = /[+-]?\d+(\.\d+)?/g;
     if (valuationMessage == null) return;
     var floats = valuationMessage.match(floatRegex).map(function(v) { return parseFloat(v); });
     this.valuationVector = floats;
     //console.log(floats);
     for (var i=0; i<floats.length; i++) {
	this.$.table.updateBidButton(i,floats[i]);
     }
  },

  submitInterests: function() {
      if (this.$.table.auctionStarted) {
        this.$.submitInterests.innerHTML = "Auction has started";
        this.$.submitInterests.disabled = true;
	return;
      }
      console.log("submit interests");
      this.$.table.updateBidButtons();
      this.$.submitInterests.innerHTML = "Active Bids Submitted";
      this.$.submitInterests.disabled = true;
  },

  // successfully submit
  success: function(e) {
    // var xml = e.detail.xhr.response;
    console.log("ok", e);
  },

  // fail to submit
  fail: function(e) {
    clearInterval(this.timer);
    console.log("fail", e);
  },

});
