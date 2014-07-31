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
			double originalPrice = fetchItemPrice(bidderItem.getName());
			if (originalPrice < bidderItem.getPrice()) {
				putItemPrice(bidderItem.getName(), bidderItem.getPrice());
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
		
//		ArrayList<AuctionItem> itemList = new ArrayList<AuctionItem>();
//		Map<AuctionItem, Double> map = new HashMap<AuctionItem, Double>();
//		
//		for (Bid b : requestedBids) {
//			for (AuctionItem i : b.getItemList()) {
//				map.put(i, i.getPrice());
//			}
//		}
//		
//		for (AuctionItem i : map.keySet()) {
//			itemList.add(i);
//		}
//		
//		context.setItemList(itemList);
		context.incrementRound();
		requestedBids.clear();
		return context;
	}
	
	private double fetchItemPrice(String name) {
		for (AuctionItem item: this.context.getItemList()) {
			if (name.equals(item.getName())) {
				return item.getPrice();
			}
		}
		return Double.MAX_VALUE;
	}
	
	private void putItemPrice(String name, double price) {
		for (AuctionItem item: this.context.getItemList()) {
			if (name.equals(item.getName())) {
				item.setPrice(price);
			}
		}
	}

}
