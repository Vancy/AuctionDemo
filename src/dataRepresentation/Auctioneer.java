package dataRepresentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import wdp.AnsParser;
import wdp.DatGenerator;
import wdp.ModGenerator;


public class Auctioneer extends Thread{
	
	/*
	 * Auctioneer is start by GUI Bidder list Start Button
	 * The process of each round:
	 * 1> Generate all agents' bids, by invoking agent's auctionResponse(), store in request list
	 * 2> Wait until round duration time up.
	 * 3> Collect next round variable from GUI if any
	 * 4> update information for next round
	 * 5> set flag, bidServlet checks this flag, send response.
	 */
	
	ConcurrentHashMap<Integer, Bid> requestedBids;
	ConcurrentHashMap<Integer, ArrayList<CCABiddingPackage>> CCASupplementaryBids;
	ConcurrentHashMap<Integer, ArrayList<LuaBid>> LuaBids;
	
	AuctionEnvironment environment;
	ArrayList<AuctionContext> auctionLog = new ArrayList<AuctionContext>();
	Timer roundTimer = new Timer();

	SAALogger saaLogger;
	
	public volatile boolean nextRoundNotReady = true;
	
	public Auctioneer(AuctionEnvironment e) {
		this.environment = e;
		this.requestedBids = new ConcurrentHashMap<Integer, Bid>();
		this.CCASupplementaryBids = new ConcurrentHashMap<Integer, ArrayList<CCABiddingPackage>>();
		this.LuaBids= new ConcurrentHashMap<Integer, ArrayList<LuaBid>>();
		saaLogger = new SAALogger();
	}
	
	public void getBid(Bid bid) {
		synchronized(this.requestedBids) {
			int bidderID = bid.getBidder().getID();
			String bidderName = bid.getBidder().getName();
			this.requestedBids.put(bidderID, bid);
			System.err.println("get bidder " + bidderName + "'s new bid");
		}
	}
	
	public void getSupplementaryRoundBid(Bidder bidder, ArrayList<CCABiddingPackage> bids) {
			this.CCASupplementaryBids.put(bidder.getID(), bids);
			if (this.CCASupplementaryBids.size() == this.environment.bidderList.size()) {
				processCcaSupplementaryRoundBids();
			}
	}
	
	public void getLuaBidPackage(Bidder bidder, ArrayList<LuaBid> bidPackage) {
		this.LuaBids.put(bidder.getID(), bidPackage);
		if (this.LuaBids.size() == this.environment.bidderList.size()) {
			processLuaBids();
		}
	}
	
	private boolean removeBids() {
			this.requestedBids.clear();
			System.out.println("current request list size after remove all:"+requestedBids.size());
			return true;
	}
	
	@Override
	public void run() {
		while (true) {
			/*
			 * If this auction is LUA auction, we run one-off bid collection, so there is no need to do the while loop.
			 * Collect enough bids, then end the auction.
			 */
			if (this.environment.context.getType() == AuctionContext.AuctionType.LUA) {
				System.err.println("LUA Auction start");
				break; // break from while loop, terminate auctioneer
			}
			/**************Next Round Start**************/
			System.err.println("******Round " + this.environment.context.getRound() + " Start*****Min increment " + this.environment.context.getMinIncrement() + "*****");
			
			//Collect Agent's bid
			collectAgentBid();
			
			while(this.environment.context.roundTimeRemain > 0) {
				// Wait until current round time up
				deliberateDelay(0.2);
			}

			// Wait one more seconds, to wait all defaults bids
			deliberateDelay(1);
			
			System.out.println("Processing Bids...");
			processBids();
			System.out.println("next round starting...");
			removeBids();
			deliberateDelay(1);
			updateNextRoundContext();
			/***************Next Round Will Start**********************/
			if (this.environment.context.isFinalRound()) {
				if (this.environment.context.getType() == AuctionContext.AuctionType.SAA) {
					System.err.println("SAA Auction End");
					break; // break from while loop, terminate auctioneer
				}	else if (this.environment.context.getType() == AuctionContext.AuctionType.CCA) {
					//process CCA supplementary round
					
				}
			}
		}
	}
	
	private void setNextRoundReady() {
		this.nextRoundNotReady = false;
	}
	
	private void setNextRoundNotReady() {
		this.nextRoundNotReady = true;
	}
		
	private void processBids() {
		
		//Xing at 2014.7.31: Process current bids, update auction context;
		//Xing change at 2014.8.2: Process all bids at once.
		if  (this.environment.context.getType() == AuctionContext.AuctionType.SAA) {
			processSAABids();
		} else if (this.environment.context.getType() == AuctionContext.AuctionType.CCA) {
			processCCABids();
		}
		
		synchronized(this.requestedBids) {
			requestedBids.clear();
		}
		
		//record current round log 
		this.recordLog();
	}
	
	private void processSAABids() {
		
		/*
		 * Generate dummy bids for silent bidders
		 */
		createSAADummyBids();
		
		/*
		 * newBids is true when there is at least one new bid, then auction will keep going,
		 * otherwise, newBids is false, means nobody bids, then next round is the final. 
		 */
		boolean newBids = false;
		//bidderPrices: 

		HashMap<Bidder, ArrayList<AuctionItem>> biddersPrices = new HashMap <Bidder, ArrayList<AuctionItem>>();
		//process each bid in requested bid list
		for (Bid bid: this.requestedBids.values()) {
			//currBidder: Who put this bid
			Bidder currBidder = bid.getBidder();
			int numberOfItemsBidOn = 0;
			int numberOfItemsLeading = 0;
			for (AuctionItem bidderItem: bid.getItemList()) {
				//item's original price
				double itemOriginalPrice = fetchItemPrice(bidderItem.getID());
				//if the temporary owner of this item is currBidder, increase currBidder's leading num
				if (currBidder.getID() == fetchItemOwnerID(bidderItem.getID())) {
					numberOfItemsLeading++;
				}
				/*if the bidder's price is higher than original price, means that this bidder bid on
				 * this item. Then record this to biddersPrices
				 */
				if (bidderItem.getPrice() > itemOriginalPrice) {
					ArrayList<AuctionItem> items;
					if (biddersPrices.get(currBidder) == null) {
						items = new ArrayList<AuctionItem>();
					} else {
						items = biddersPrices.get(currBidder);
					}
					items.add(bidderItem);
					biddersPrices.put(currBidder, items);
					if (!newBids) {
						newBids = true;
					}
					if (fetchItemOwnerID(bidderItem.getID()) != currBidder.getID()) {
						numberOfItemsBidOn++;
					}
				} else {
					/*bidder aborted bid for this item, for one of the following reasons:
					 * 1> this bidder leads the price in last round
					 * 2> this bidder gives up on this item this round
					 */
				}
			}
			if (this.environment.context.getRound() == 1 ) {
				currBidder.setActivity(numberOfItemsBidOn);
				currBidder.setEligibility(numberOfItemsBidOn);
			} else if (this.environment.context.getRound() == this.environment.context.getActivityRuleStartRound() - 1) {
				currBidder.setActivity(numberOfItemsBidOn + numberOfItemsLeading);
				currBidder.setEligibility(this.environment.context.getItemList().size());
			} else {
				// set eligibility to previous activity and calculate current activity
				// this will determine whether the bidder has become inactive
				currBidder.setEligibility(currBidder.getActivity());
				currBidder.setActivity(numberOfItemsBidOn + numberOfItemsLeading);
			}
			if (this.environment.context.getRound() >= this.environment.context.getActivityRuleStartRound()) {
				currBidder.auctionRuleVerify();
			}
		}
		
		// apply the item updates
		for (Bidder bidder : biddersPrices.keySet()) {
			for (AuctionItem item : biddersPrices.get(bidder)) {
				double bidderPrice = item.getPrice();
				if (bidderPrice > fetchItemPrice(item.getID())) {
					putItemPrice(bidder, item.getID(), bidderPrice);				
				} else if (Math.abs(bidderPrice - fetchItemPrice(item.getID())) <= 0.001 && flipCoinWin()) {
					putItemPrice(bidder, item.getID(), bidderPrice);
				}
			}
		}
		
		for (Bidder bidder : this.environment.bidderList.getList()) {
			bidder.leadingItemsMessage = "";
			for (AuctionItem item: this.environment.context.getItemList()) {
				if (fetchItemOwnerID(item.getID()) == bidder.getID()) {
					bidder.leadingItemsMessage += item.getName() + " ";
				}
			}
			if (!bidder.leadingItemsMessage.equals("")) {
				bidder.leadingItemsMessage = "You are currently leading the following items: " + bidder.leadingItemsMessage;
			}
		}
		
		saaLogger.addToExcelLog(this.requestedBids.values());
		
		if (!newBids) {
			this.environment.context.setFinalRound();
			saaLogger.createExcelLogSheet(this.environment.context.getItemList(), this.environment.bidderList.getList(), "SAA Auction Results");
		}
	}
	
	private void processCCABids() {
		
		//firstly clear last round temporary owners
		for(AuctionItem item: this.environment.context.getItemList()) {
			if (!item.biddingFinised) {
				item.clearOwners();
			}
		}
		
		//create a temporary array to story the total number all bidders require each round
		int itemNumber = this.environment.context.getItemList().size();
		int[] thisRoundRequirment = new int[itemNumber];
		for (int i=0; i<itemNumber; i++) {
			thisRoundRequirment[i] = 0;
		}
		
		//collect requirement of each item
		for (Bid bid: this.requestedBids.values()) {
			for (AuctionItem bidderItem: bid.getItemList()) {
				thisRoundRequirment[bidderItem.getID()] += bidderItem.getRequiredQuantity();
				placeItemOwner(bidderItem.getID(), bid.getBidder().getName(), bidderItem.getRequiredQuantity());
				System.out.println("Bidder "+ bid.getBidder().getName()+ " wants itemID:" + bidderItem.getID() + " require " + bidderItem.getRequiredQuantity());
			}
		}
		for (AuctionItem item: this.environment.context.getItemList()) {
			item.setRequiredQuantity(thisRoundRequirment[item.getID()]);
			if (item.getRequiredQuantity() <= item.getQuantity()) {
				//set this item is finish bidding
				item.biddingFinised = true; 
			}
		}
		
		if (this.environment.context.allCCAItemBddingFinished()) {
			// if all cca items are finished, set context finishTag as true
			this.environment.context.setFinalRound();
		}
	}
	
	private void recordLog() {	
		this.auctionLog.add(new AuctionContext(this.environment.context));
	}
	
	private void processCcaSupplementaryRoundBids() {
		System.err.println("Processing CCA supplementary Round Bids (AMPL)...");
		// collect all bids to an array
		List<CCABiddingPackage> totalBiddingPakages = new ArrayList<CCABiddingPackage>();
		for (int i: this.CCASupplementaryBids.keySet()) {
			totalBiddingPakages.addAll(this.CCASupplementaryBids.get(i));
		}
		
		/*
		 * Generating .mod file for AMPL
		 */
		System.err.println("(AMPL) Gnerating .mod file...");
		ModGenerator modGenerator = new ModGenerator(this.environment.context.getItemList());
		modGenerator.generateFile();
		
		/*
		 * Generating .dat file for AMPL
		 */
		System.err.println("(AMPL) Generating .dat file...");
		DatGenerator datGenerator = new DatGenerator(this.environment.bidderList.getList(), totalBiddingPakages, this.environment.context.getItemList());
		datGenerator.generateFile();
		
		/*
		 * Processing and get answer
		 */
		System.err.println("(AMPL) Processing and get answer...");
		AnsParser ap = new AnsParser();
		ap.printResults();
		System.err.println("(AMPL) Done...");
		
	}
	
	private void processLuaBids() {
		System.out.println("We now process LUA bids");
		//TODO
		this.environment.context.LuaBids = this.LuaBids;
		this.environment.context.setFinalRound();
		this.environment.context.bidsProcessingFinished = true;

	}
	
	public ArrayList<AuctionContext> getLog() {
		return this.auctionLog;
	}
	
	private void updateNextRoundContext() {
		
		/*
		 * reset parameter via GUI, e.g. minimun_increment
		 */
		this.environment.context.bidsProcessingFinished = true; //set flag true: GUI can update info
		while (false == this.environment.context.bidsProcessingFinished); //wait till GUI finish update
		
		this.environment.context.incrementRound();
		this.environment.context.roundTimeRemain = this.environment.context.getDurationTime();
		
		//some update for CCA Auction
		if (this.environment.context.getType() == AuctionContext.AuctionType.CCA) 
			updateNextRoundPriceForCCA();
		
		//set flag true, bidservlet can send response
		this.setNextRoundReady();
		
		//make sure every one receive their responses
		while (this.requestedBids.size() > 0) {
			deliberateDelay(0.5);
			//waiting to confirm every html response sent
		}
		
		//set flag false, next round bidservlet wait till ready.
		this.requestedBids.clear();
		this.setNextRoundNotReady();
	}
	
	private void collectAgentBid() {
		for (Bidder bidder: this.environment.bidderList.getList()) {
			if (bidder instanceof CCAAgent) {
				Bid agentBid = ((CCAAgent)bidder).auctionResponse(this.environment.context);
				requestedBids.put(agentBid.getBidder().getID(), agentBid);
				System.err.println("Agent:"+bidder.getName()+" place a bid:");
				for (AuctionItem item: agentBid.getItemList()) {
					System.out.println(bidder.getName()+" demands:"+item.getRequiredQuantity()+"for item:"+item.getName());
				}
			} else if (bidder instanceof SAAAgent) {
				Bid agentBid = ((SAAAgent)bidder).auctionResponse(this.environment.context);
				requestedBids.put(agentBid.getBidder().getID(), agentBid);
				System.err.println("Agent:"+bidder.getName()+" place a bid:");
				for (AuctionItem item: agentBid.getItemList()) {
					System.out.println(bidder.getName()+"'s price:"+item.getPrice()+"for item:"+item.getName());
				}
			} 	
		}
	}
	
	private void updateNextRoundPriceForCCA() {
		double priceTick = this.environment.context.getPriceTick() + this.environment.context.getMinIncrement();
		this.environment.context.setPriceTick(priceTick);
		ArrayList<AuctionItem> auctionItems = this.environment.context.getItemList();
		for (AuctionItem item: auctionItems) {
			if (!item.biddingFinised) {
				item.setPrice(priceTick);
			}
		}
	}
	
	/*
	 * three helper methods: fetchItemPrice, putItemPrice, placeItemOwner
	 */
	/*
	 * fetch the temporary price of an item
	 */
	private double fetchItemPrice(int itemID) {
		for (AuctionItem item: this.environment.context.getItemList()) {
			if (itemID == item.getID()) {
				return item.getPrice();
			}
		}
		return Double.MAX_VALUE;
	}
	
	/*
	 * return one item's temporary owner id
	 */
	private int fetchItemOwnerID(int itemID) {
		for (AuctionItem item: this.environment.context.getItemList()) { 
			if (itemID == item.getID()) {
				return item.getOwner().getID();
			}
		}
		return -1;
	}
	
	/*
	 * update the highest price of certain item and update the temporary owner
	 */
	private void putItemPrice(Bidder bidder, int itemID, double price) {
		for (AuctionItem item: this.environment.context.getItemList()) {
			if (itemID == item.getID()) {
				item.setPrice(price);
				item.setOwner(bidder);
			}
		}
	}
	
	private void placeItemOwner(int itemID, String name, int amount) {
		for (AuctionItem item: this.environment.context.getItemList()) {
			if (itemID == item.getID()) {
				item.placeOwner(name, amount);
			}
		}
	}
	
	private void createSAADummyBids() {
		for (Bidder b : this.environment.bidderList.getList()) {
			if (!this.requestedBids.containsKey(b.getID())) {
				// this bidder did not make a bid - give him a dummy bid
				System.err.println(b.getName() + " did not submit a bid! Assigning a dummy bid...");
				ArrayList<AuctionItem> dummyItems = new ArrayList<AuctionItem>();
				for (AuctionItem i : this.environment.context.getItemList()) {
					AuctionItem dummyItem = new AuctionItem(i);
					dummyItem.setOwner(b);
					dummyItem.setPrice(0);
					dummyItems.add(dummyItem);
				}
				Bid dummyAbortedBid = new Bid(b, dummyItems);
				this.requestedBids.put(b.getID(), dummyAbortedBid);
			}
		}
	}
	
	/*
	 * Two utility funcitons: deliberateDelay, flipCoinWin
	 */
	static private void deliberateDelay(double sec) {
		try {
			Thread.currentThread();
			Thread.sleep((int)(sec * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	static private boolean flipCoinWin() {
		Random generator = new Random(System.currentTimeMillis());
		int coin = generator.nextInt(100);
		return coin > 50 ? true : false;
	}

}