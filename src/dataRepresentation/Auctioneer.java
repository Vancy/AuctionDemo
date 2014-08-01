package dataRepresentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Auctioneer {
	
	ArrayList<Bid> requestedBids;
	AuctionContext context;
	
	public Auctioneer(AuctionContext c) {
		this.context = c;
		this.requestedBids = new ArrayList<Bid>();
	}
	
	public void getBid(Bid bid) {
		requestedBids.add(bid);
		
		//Xing at 2014.7.31: Process current bid, update auction context;
		for (AuctionItem bidderItem: bid.getItemList()) {
			double originalPrice = fetchItemPrice(bidderItem.getID());
			if (originalPrice < bidderItem.getPrice()) {
				putItemPrice(bid.getBidder().getID(), bidderItem.getID(), bidderItem.getPrice());
			}
		}
		
	}
	
	public int numberOfRequestedBids() {
		return requestedBids.size();
	}

	public AuctionContext nextRound(AuctionEnvironment ae) {
		
		System.out.println("num Bidderlist:"+ae.bidderList.size());
		System.out.println("num requestBids:"+this.requestedBids.size());
		while (true) {
			// all bidders have finished placing their bids
			if (requestedBids.size() == ae.bidderList.size()) {
				break;
			}
		}
		
		context.incrementRound();
		requestedBids.clear();
		return context;
	}
	
	private double fetchItemPrice(int itemID) {
		for (AuctionItem item: this.context.getItemList()) {
			if (itemID == item.getID()) {
				return item.getPrice();
			}
		}
		return Double.MAX_VALUE;
	}
	
	private int fetchItemOwner(int itemID) {
		for (AuctionItem item: this.context.getItemList()) {
			if (itemID == item.getID()) {
				return item.getOwner();
			}
		}
		return -1;
	}
	
	private void putItemPrice(int bidderID, int itemID, double price) {
		for (AuctionItem item: this.context.getItemList()) {
			if (itemID == item.getID()) {
				item.setPrice(price);
				item.setOwner(bidderID);
			}
		}
	}

}
