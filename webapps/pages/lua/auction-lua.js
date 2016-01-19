Polymer('auction-lua', {
  localIP: "127.0.0.1",
  username: "",
  lua: {},
 
  items: [],
  auctionStarted: false,
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
    console.log("LUA-setup");
  },

  setData: function(data) {
    this.lua = data;
    this.updateInformation();

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
  
  updateInformation: function() {
    this.items = this.lua.itemList;
    console.log("update info");
    var currentRound = this.lua.round;
    if (currentRound <= 0) {
      this.auctionStarted = false;
    } else {
      //The timer finishes its job.
      clearTimeout(this.timer);
      this.auctionStarted = true;
      console.log("auction starts");
      this.$.table.startAuction(this.lua);
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
