package dataRepresentation;

import java.util.ArrayList;



public class Auctioneer {
	
	ArrayList<Bid> requestedBids;
	AuctionEnvironment environment;
	
	boolean nextRoundNotReady = true;
	
	public Auctioneer(AuctionEnvironment e) {
		this.environment = e;
		this.requestedBids = new ArrayList<Bid>();
	}
	
	public void getBid(Bid bid) {
		requestedBids.add(bid);
		
		//Xing change: accumulate bids process 
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
				double originalPrice = fetchItemPrice(bidderItem.getName());
				System.out.println("original price:"+originalPrice+"for bidder"+bid.getBidder().getName());
				if (originalPrice < bidderItem.getPrice()) {
					putItemPrice(bid.getBidder(), bidderItem.getName(), bidderItem.getPrice());
				}
			}
		}
		requestedBids.clear();
		this.environment.context.incrementRound();
		this.setNextRoundReady();
	}
	
	//Xing: currently message from client browser can only recognize item by name, so please keep this.
	private double fetchItemPrice(String name) {
		for (AuctionItem item: this.environment.context.getItemList()) {
			if (name.equals(item.getName())) {
				return item.getPrice();
			}
		}
		return Double.MAX_VALUE;
	}
	
	//Xing: currently message from client browser can only recognize item by name, so please keep this.
	private void putItemPrice(Bidder bidder, String itemname, double price) {
		for (AuctionItem item: this.environment.context.getItemList()) {
			if (itemname.equals(item.getName())) {
				item.setPrice(price);
				item.setOwner(bidder);
			}
		}
	}
	/*
	 * Keep following two methods, maybe in the furture we can substitute, by using item id, not item name.
	 */
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
