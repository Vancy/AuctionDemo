package dataRepresentation;

import java.util.ArrayList;



public class Auctioneer {
	
	ArrayList<Bid> requestedBids;
	AuctionEnvironment environment;
	
	ArrayList<AuctionContext> auctionLog = new ArrayList<AuctionContext>();
	
	boolean nextRoundNotReady = true;
	
	public Auctioneer(AuctionEnvironment e) {
		this.environment = e;
		this.requestedBids = new ArrayList<Bid>();
	}
	
	public void getBid(Bid bid) {
		this.nextRoundNotReady = true;
		requestedBids.add(bid);
		
		//Xing change: accumulate bids process 
		System.err.println("current request:"+requestedBids.size());
		if (requestedBids.size() == this.environment.bidderList.size()) {
			this.processBids();
		}
	}
	
	public boolean nextRoundNotReady() {
		return this.nextRoundNotReady;
	}
	
	public void setNextRoundReady() {
		this.nextRoundNotReady = false;
	}
	
	public AuctionContext nextRound() {

		//this.environment.context.incrementRound();
		
		return this.environment.context;
	}
	
	private void processBids() {
		
		//Xing at 2014.7.31: Process current bids, update auction context;
		//Xing change at 2014.8.2: Process all bids at onece.
		
		System.err.println("request bid num:"+this.requestedBids.size());
		for (Bid bid: this.requestedBids) {
			for (AuctionItem bidderItem: bid.getItemList()) {
				double originalPrice = fetchItemPrice(bidderItem.getID());
				System.out.println("original price:"+originalPrice+"for bidder"+bid.getBidder().getName());
				if (originalPrice < bidderItem.getPrice()) {
					putItemPrice(bid.getBidder(), bidderItem.getID(), bidderItem.getPrice());
				}
			}
		}
		requestedBids.clear();
		//record current round log 
		this.recordLog();
		this.environment.context.bidsProcessingFinished = true;
		
		while (false == this.environment.context.bidsProcessingFinished); //wait till GUI update table
		this.environment.context.incrementRound();
		this.setNextRoundReady();
		System.err.println("nest round not ready"+this.nextRoundNotReady);
	}
	
	private void recordLog() {
		
		this.auctionLog.add(new AuctionContext(this.environment.context));
	}
	
	public ArrayList<AuctionContext> getLog() {
		return this.auctionLog;
	}
	
	private double fetchItemPrice(int itemID) {
		for (AuctionItem item: this.environment.context.getItemList()) {
			if (itemID == item.getID()) {
				return item.getPrice();
			}
		}
		return Double.MAX_VALUE;
	}
	
	private void putItemPrice(Bidder bidder, int itemID, double price) {
		for (AuctionItem item: this.environment.context.getItemList()) {
			if (itemID == item.getID()) {
				item.setPrice(price);
				item.setOwner(bidder);
			}
		}
	}
	

}
