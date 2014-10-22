Polymer('auction-saa', {
  localIP: "127.0.0.1",
  username: "",
  saa: {},

  round: -1,
  timeRemain: -1,
  minimumIncreament: -1,
  items: [],
  isFinal: false,
  data: "",
  isTimerStarted: false,

  updateUrl: "",
  timer: undefined,

   domReady: function() {
    this.disableSubmittion();
  },

  enableSubmittion: function() {
    this.$.bid.disabled = false;
  },

  disableSubmittion: function() {
    this.$.bid.disabled = true;
  },

  setUp: function(username, localIP) {
    this.username = username;
    this.localIP = localIP;
    this.updateUrl = "/WEB-INF/update.xml?name=" + this.username + "&ip=" + this.localIP;
  },

  /**
  *  Entry to set all data.
  */
  setData: function(data) {
    this.saa = data;
    this.updateInfomation();

    // Start the ajax timer when it is the first time.
    var self = this;
    if ( this.isTimerStarted == false ) {
      this.timer = setInterval(function() {
        self.$.period.go()
      }, 1000);
      this.isTimerStarted = true;
    }
  },

  /**
  *  To get updation information from server every 1s.
  */
  update: function(e) {
    // console.log("update!!", e, e.detail, e.detail.xhr);

    if ( e.detail.xhr.status != 200 ) {
        clearInterval(this.timer);
        console.log("Unable to connect to server. Status code: ", e.detail.xhr.status);
        this.$.time.innerHTML = "</b>There is a problem to connect to server.</b>";
        this.disableSubmittion();
        return;
    }
    var json = JSON.parse(e.detail.xhr.response); 
    // console.log("Timer ", json);
    

    this.setData(json);
  },


  updateInfomation: function() {
    var tmp = this.saa.itemList;
    if ( this.saa.round > this.round && this.saa.round > 0) {
      this.enableSubmittion();
    }
    this.round = this.saa.round;
    this.timeRemain = this.saa.roundTimeRemain;
    this.minimumIncreament = this.saa.minIncreament;
    this.isFinal = this.saa.finalRound;

    /**
     *  1. When it is the first time to get data or last time(final round), it's fine to refresh the UI by update 
     *     the `this.items`, which is **two-way** binded with the `item` inside the 'acution-saa-table'.
     *  2. While when it is a normal update, to avoid 'flash', only `price` information needs to be updated.
     *     So, *do not* modify `this.items` as well as the `items` inside the 'acution-saa-table'. Instead, 
     *     call a funtion of 'acution-saa-table', which will *ONLY* modifies the UI of `price`.
    **/
    if ( this.items.length == 0 || this.isFinal ) {
        this.items = tmp;
    } else {
        this.$.table.update(tmp);
    }

    // this.$.bid.disabled = false;
    if ( ! this.isAuctionStarted() ) {
      this.$.time.innerHTML = "Please wait, auction haven't started yet.";
      // this.$.bid.disabled = true;
      // not stated yet
    } else {
     if ( ! this.isFinal  ) {
        this.$.time.innerHTML = "Time remain: " + this.timeRemain + "s";
      } else {
        // final
        clearInterval(this.timer);
        this.$.time.innerHTML = "<b>Final</b>";
        this.disableSubmittion();
      }
    }
  },

  isAuctionStarted: function() {
    if ( this.round == 0 ) {
      return false
    }
    return true;
  },

  submit: function() {
    // validate
    this.disableSubmittion();
    this.data = this.collectData();
    this.$.submit.go();
    
  },

  collectData: function() {

    var data = {};
    data.bid = {};
    data.bid.bidder = {name: this.username, ip: this.localIP};
    data.bid.itemList = this.$.table.getItems();
    console.log("SUBMIT ", data.bid);
    console.log("  ", JSON.stringify(data));
    return JSON.stringify(data);
    // return '{"bid":{"bidder":{"name":"Zhenfei","ip":"195.176.178.184"},"itemList":[{"ID":0,"name":"ItemA","price":"1","owners":{}},{"ID":1,"name":"ItemB","price":"2","owners":{}},{"ID":2,"name":"ItemC","price":"3","owners":{}}]}} ';

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
  }
});