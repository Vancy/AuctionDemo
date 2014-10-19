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

  setUp: function(username, localIP) {
    this.username = username;
    this.localIP = localIP;

    this.updateUrl = "/WEB-INF/update.xml?name=" + this.username + "&ip=" + this.localIP;
  },

  /**
  *  Entry to set all data.
  */
  setData: function(data) {
    console.log("SAA: ", data);

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
        return;
    }
    var xml = e.detail.xhr.response; 
    var x2js = new X2JS();
    var json = x2js.xml_str2json(xml);

    this.setData(json);
  },


  updateInfomation: function() {
    var tmp = this.saa.auction_context.item;
    this.round = this.saa.auction_context.round._value;
    this.timeRemain = this.saa.auction_context.duration._remain;
    this.minimumIncreament = this.saa.auction_context.minimum_increament._value;
    this.isFinal = this.saa.auction_context.round._final == "no" ? false : true;

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

    this.$.bid.disabled = false;
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
        this.$.bid.disabled = true;
      }
    }
  },

  isAuctionStarted: function() {
    if ( this.saa.auction_context.round._value == "0" ) {
      return false
    }
    return true;
  },

  submit: function() {
    // validate
    this.data = this.collectData();
    this.$.submit.go();
  },

  collectData: function() {
    var x2js = new X2JS();
    var data = {};
    data.bid = {};
    data.bid.bidder = {_name: this.username, _ip: this.localIP};
    data.bid.item_list = {}
    data.bid.item_list.item = this.$.table.getItems();
    
    return x2js.json2xml(data);
  }, 

  handleResponse: function(e) {
    // var xml = e.detail.xhr.response;
    console.log("ok", e);
  },

  fail: function(e) {
    clearInterval(this.timer);
    console.log("fail", e);
  }
});