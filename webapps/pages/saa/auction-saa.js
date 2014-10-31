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

  // Explaination: http://stackoverflow.com/questions/195951/change-an-elements-css-class-with-javascript
  enableSubmittion: function() {
    this.$.bid.disabled = false;
    this.$.bid.className = this.$.bid.className.replace( /(?:^|\s)btn-disabled(?!\S)/g , '' );
    this.$.bid.className += " btn";
  },

  disableSubmittion: function() {
    this.$.bid.disabled = true;
    this.$.bid.className = this.$.bid.className.replace( /(?:^|\s)btn(?!\S)/g , '' );
    this.$.bid.className += " btn-disabled";
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
    //console.log("SAA ", this.saa);

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
        this.$.time.innerHTML = "<b class='error'>Error. Cannot connect to the server.</b>";
        this.disableSubmittion();
        return;
    }
    var json = JSON.parse(e.detail.xhr.response); 
    this.setData(json);
  },


  updateInfomation: function() {
    var tmp = this.saa.itemList;
    /**
    * If the last round(`this.round`) is smaller than current round, then enable submit button.
    */
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
      this.$.time.innerHTML = "Please wait, auction hasn't started yet.";
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
    /*Xing added following code to display activity rules:
     * 2014.10.31
     */
    var bidderListInfo = this.saa.bidderList.list;
    this.displayAuctionRuleInfo(bidderListInfo);
  },

 displayAuctionRuleInfo: function(bidderListInfo) {
     for ( var i = 0; i <bidderListInfo.length; i++ ) {
	if (bidderListInfo[i].name == this.username && bidderListInfo[i].ipAddress == this.localIP) {
	  var warnMsg = bidderListInfo[i].warningMessage;
	  var activityCounter = parseInt(bidderListInfo[i].activityCounter);
	  if ("" != warnMsg) {
	     console.log("You got a warn:"+ warnMsg);
	  }
	  this.$.warning.innerHTML = '<font color="red">'+warnMsg+'</font>';
	  if (activityCounter <= 0) {
	     console.log("You break enough times of rules, kick you off");
             this.disableSubmittion();
	  }	
	}
     }
  },	

  isAuctionStarted: function() {
    if ( this.round == 0 ) {
      return false;
    }
    return true;
  },

  submit: function() {
  //Xing: always enable bidders to bid multiple times, so needn't disableSubmittion
    //this.disableSubmittion();
    this.data = this.collectData();
    this.$.submit.go();
  },

  collectData: function() {
    var data = {};
    data.bid = {};
    data.bid.bidder = {name: this.username, ip: this.localIP};
    data.bid.itemList = this.$.table.getItems();
    return JSON.stringify(data);
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
