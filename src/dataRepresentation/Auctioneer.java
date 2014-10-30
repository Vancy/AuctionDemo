package dataRepresentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;


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
	
	HashMap<Integer, Bid> requestedBids;
	AuctionEnvironment environment;
	ArrayList<AuctionContext> auctionLog = new ArrayList<AuctionContext>();
	Timer roundTimer = new Timer();
	
	SAALogger saaLogger;
	
	public volatile boolean nextRoundNotReady = true;
	
	public Auctioneer(AuctionEnvironment e) {
		this.environment = e;
		this.requestedBids = new HashMap<Integer, Bid>();
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
	
	private boolean removeBids() {
			this.requestedBids.clear();
			System.out.println("current request list size after remove all:"+requestedBids.size());
			return true;
	}
	
	@Override
	public void run() {
		while (true) {
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
				System.err.println("Auction End");
				break; // break from while loop, terminate auctioneer
			}
		}
	}
	
	private void setNextRoundReady() {
		this.nextRoundNotReady = false;
	}
	
	private void setNextRoundNotReady() {
		this.nextRoundNotReady = true;
	}
	
	public AuctionContext nextRound() {
		//this method is invoked by BidServlet, to get the latest auctionContext
		return this.environment.context;
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
		
		/*
		 * newBids is true when there is at least one new bid, then auction will keep going,
		 * otherwise, newBids is false, means nobody bids, then next round is the final. 
		 */
		boolean newBids = false;
		//bidderPrices: 
		//originalPrices:
		HashMap<Bidder, ArrayList<AuctionItem>> bidderPrices = new HashMap <Bidder, ArrayList<AuctionItem>>();
		HashMap<Integer, Double> originalPrices = new HashMap <Integer, Double>();
		for (Bid bid: this.requestedBids.values()) {
			Bidder currBidder = bid.getBidder();
			int numberOfItemsBidOn = 0;
			int numberOfItemsLeading = 0;
			for (AuctionItem bidderItem: bid.getItemList()) {
				
				double originalPrice = fetchItemPrice(bidderItem.getID());
				originalPrices.put(bidderItem.getID(), originalPrice);
				if (currBidder.getID() == fetchItemOwnerID(bidderItem.getID())) {
					numberOfItemsLeading++;
				} 
				if (bidderItem.getPrice() > originalPrice) {
					ArrayList<AuctionItem> items;
					if (bidderPrices.get(currBidder) == null) {
						items = new ArrayList<AuctionItem>();
					} else {
						items = bidderPrices.get(currBidder);
					}
					items.add(bidderItem);
					bidderPrices.put(currBidder, items);
					if (!newBids) {
						newBids = true;
					}
					numberOfItemsBidOn++;
				} else {
					// bidder aborted bid for this item
				}
			}
			if (this.environment.context.getRound() == 1) {
				currBidder.setActivity(numberOfItemsBidOn);
				currBidder.setEligibility(numberOfItemsBidOn);
			} else {
				// set eligibility to previous activity and calculate current activity
				// this will determine whether the bidder has become inactive
				currBidder.setEligibility(bid.getBidder().getActivity());
				currBidder.setActivity(numberOfItemsBidOn + numberOfItemsLeading);
				if (currBidder.getActivity() > currBidder.getEligibility()) {
					currBidder.decrementActivityCounter();
					// WARN THE BIDDER THAT THEY WERE INACTIVE AND LOST A WAIVER HERE.
					if (currBidder.getActivityCounter() <= 0) {
						// ALERT THE BIDDER THAT THEY GOT KICKED HERE.
					}
				}
			}
		}
		
		// apply the item updates
		for (Bidder bidder : bidderPrices.keySet()) {
			for (AuctionItem item : bidderPrices.get(bidder)) {
				double bidderPrice = item.getPrice();
				if (bidderPrice > fetchItemPrice(item.getID())) {
					putItemPrice(bidder, item.getID(), bidderPrice);				
				} else if (Math.abs(bidderPrice - fetchItemPrice(item.getID())) <= 0.001 && flipCoinWin()) {
					putItemPrice(bidder, item.getID(), bidderPrice);
				}
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
				Bid agentBid = ((Agent)bidder).auctionResponse(this.environment.context);
				requestedBids.put(agentBid.getBidder().getID(), agentBid);
				System.err.println("Agent:"+bidder.getName()+" place a bid:");
				for (AuctionItem item: agentBid.getItemList()) {
					System.out.println(bidder.getName()+" demands:"+item.getRequiredQuantity()+"for item:"+item.getName());
				}
			} else if (bidder instanceof Agent) {
				Bid agentBid = ((Agent)bidder).auctionResponse(this.environment.context);
				requestedBids.put(agentBid.getBidder().getID(), agentBid);
				System.err.println("Agent:"+bidder.getName()+" place a bid:");
				for (AuctionItem item: agentBid.getItemList()) {
					System.out.println(bidder.getName()+"'s price:"+item.getPrice()+"for item:"+item.getName());
				}
			} 	
		}
	}
	
	private void recordLog() {	
		this.auctionLog.add(new AuctionContext(this.environment.context));
	}
	
	public ArrayList<AuctionContext> getLog() {
		return this.auctionLog;
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
				System.err.println(item.getOwner().getName());
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