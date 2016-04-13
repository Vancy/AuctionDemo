Polymer('auction-lua', {
  localIP: "127.0.0.1",
  username: "",
  lua: {},
 
  items: [],
  auctionTableStarted: false,
  isFinal: false,
  data: "",

  updateUrl: "",
  timer: undefined,

  domReady: function() {
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
    for (var i = 0; i < bidderList.length; i++) {
	if (bidderList[i].ipAddress === this.localIP &&	bidderList[i].name === this.username) {
	  return "My winning result:<br/>ID Name WinType WinPrice<br/>" + bidderList[i].luaWinningMessage;
	} else {
	  continue;
	}
    }
    return "";
  },

  getMyValuationMsg: function() {
    var bidderList = this.lua.bidderList.list;
    for (var i = 0; i < bidderList.length; i++) {
	if (bidderList[i].ipAddress === this.localIP &&	bidderList[i].name === this.username) {
	  this.reflectZeroValuationToDisableBidButton(bidderList[i].luaValuationsMessage);
	  return "Item valuations:<br/>" + bidderList[i].luaValuationsMessage;
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
     //console.log(floats);
     for (var i=0; i<floats.length; i++) {
	this.$.table.updateBidButton(i,floats[i]);
     }
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
