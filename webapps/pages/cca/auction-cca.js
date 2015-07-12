Polymer('auction-cca', {
  localIP: "127.0.0.1",
  username: "",
  cca: {},

  round: -1,
  timeRemain: -1,
  minimumIncreament: -1,
  items: [],
  isFinal: false,
  data: "",
  isTimerStarted: false,

  updateUrl: "",
  timer: undefined,

  activity: 0,
  eligibility: 0,
  eligibilityReducingBids: {},
  tempRequirePkg: {},
  RPC: false,

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
    this.cca = data;
    this.updateInfomation();
    //console.log("CCA ", this.cca);


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

    var tmp = this.cca.itemList;
    /**
    * If the last round(`this.round`) is smaller than current round, then enable submit button.
    */
    if ( this.cca.round > this.round && this.cca.round > 0) {
      this.enableSubmittion();
    } 
    this.round = this.cca.round;
    this.timeRemain = this.cca.roundTimeRemain;
    this.minimumIncreament = this.cca.minIncreament;
    this.isFinal = this.cca.finalRound;

    /**
    * Convert format of onwers
    */
    
    for ( var i=0; i<tmp.length; i++ ) {
      var m_owners = [];
      var ks = Object.keys(tmp[i].owners);
      for ( var j=0; j<ks.length; j++ ) {
        var o = {};
        o.name = ks[j];
        o.quantity = tmp[i].owners[ks[j]];
        m_owners.push(o);
      }
      tmp[i].owners = m_owners;
    }


    /**
     *  1. When it is the first time or last time(final round) to get data, it's fine to refresh the UI by update 
     *     the `this.items`, which is **two-way** binded with the `item` inside the 'acution-cca-table'.
     *  2. While when it is a normal update, to avoid 'flash', only `price` information needs to be updated.
     *     So, *do not* modify `this.items` as well as the `items` inside the 'acution-cca-table'. Instead, 
     *     call a funtion of 'acution-cca-table', which will *ONLY* modifies the UI of `price`.
    **/
    if ( this.items.length == 0 || this.isFinal ) {
      console.log("Initial or Final", tmp);
      this.items = tmp;
      this.activity = this.$.table.calculateTotalEligibility();

    } else {
      this.$.table.update(tmp);
      this.$.table.validate();
    }

    /* Xing: if isFinal is true, means clock round is end, so move to supplymentary round.
     *       change auction-cca-table to auction-cca-phase2-table, and stop periodical update.
    **/ 
    if (this.isFinal) {
    	var stage1table = this.$.table;
    	var stage2table = this.$.supplymentarytable;

    	stage2table.hidden = false; // display phase2 table
    	//stage1table.hidden = true; // don't hide phase1 table (Xing at 2015.7.12)
        this.items = tmp;
    	clearInterval(this.timer);  // stop timer update
    	this.$.time.innerHTML = "<b>Supplementary Round</b>";
        this.$.title.innerHTML = "<p>CCA - Supplementary Round</p>"
    }

    // this.$.bid.disabled = false;
    if ( ! this.isAuctionStarted() ) {
      this.$.time.innerHTML = "Please wait, auction hasn't started yet.";
      // this.$.bid.disabled = true;
      // not stated yet
    } else {
     if ( ! this.isFinal  ) {
        this.$.time.innerHTML = "Time remain: " + this.timeRemain + "s";
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
    if (this.eligibility < this.activity) {
	//record eligibility decreasing round bids
	this.eligibilityReducingBids[this.round] = this.tempRequirePkg;
    }
    this.activity = this.eligibility;
    this.eligibility = 0;
    this.data = this.collectData();
    this.$.submit.go();
  },

  collectData: function() {
    var data = {};
    data.bid = {};
    data.bid.bidder = {name: this.username, ip: this.localIP};
    if (! this.isFinal) {
      data.bid.itemList = this.$.table.getItems();
    } else if (this.isFinal) {
      data.bid.packageList = this.$.supplymentarytable.getPackages();
    }	
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
  },


  //Xing: add these functions to support auction rules 2015.3.11
   displayEligibility: function(e) {
     //console.log("display points:", e.detail.points);
     //this.$.eligibilityPoints.innerHTML = 'Eligibility:' + '<font color="green">'+e.detail.points+'</font>';
     this.eligibility = e.detail.points;
     currentPkg = e.detail.require;
     this.tempRequirePkg = currentPkg;
     console.log("require package:", currentPkg);
     if (this.eligibility > this.activity) {
	//check if RPC is satisfied
	var RPCsatisfied = true;
	//1 calculate Nt (current requirement package total price with current round unit price
	var Nt = 0;
        for (var i in currentPkg) {
	  // [1] unit price [2] unit required
	  Nt += currentPkg[i][1] * currentPkg[i][2];
	}
	console.log("log", this.eligibilityReducingBids);
	for (var s in this.eligibilityReducingBids) {
	  var pkg = this.eligibilityReducingBids[s];
	  var Ns = 0;
	  var St = 0;
	  var Ss = 0;
	  for (var id in pkg) {
		//2 calculate Ns (current requirement package total price with s round unit price
		Ns += pkg[id][1] * currentPkg[id][2];
		//3 calculate St (round s requirement package total price with current round unit price
		St += currentPkg[id][1] * pkg[id][2];
		//4 calculate Ss (current requirement package total price with s round unit price
		Ss += pkg[id][1] * pkg[id][2];
	  }
	  if ((Nt - Ns) <= (St - Ss)) {
	    //RPC satisfied with round s
	  } else {
	    //PRC is not satisfied with round s
	    RPCsatisfied = false;
	    break;
	  }
	}
        //disable/able Bid buttion
        if ( RPCsatisfied == true) {
	  console.log("RPC is satisfied");
          this.RPC = true;
	  this.enableSubmittion();
	  return;
	} else if ( RPCsatisfied == false) {
	console.log("RPC is not satisfied");
          this.RPC = false;
	  this.disableSubmittion();
	  return;
	}
     }
     this.RPC = false;
     //else should enable submittion buttion
     this.enableSubmittion();
   }
});
