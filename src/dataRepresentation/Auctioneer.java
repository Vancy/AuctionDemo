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
	}
	
	public int numberOfRequestedBids() {
		return requestedBids.size();
	}

	public AuctionContext nextRound() {
		ArrayList<AuctionItem> itemList = new ArrayList<AuctionItem>();
		Map<AuctionItem, Double> map = new HashMap<AuctionItem, Double>();
		
		for (Bid b : requestedBids) {
			for (AuctionItem i : b.getItemList()) {
				map.put(i, i.getPrice());
			}
		}
		
		for (AuctionItem i : map.keySet()) {
			itemList.add(i);
		}
		
		context.setItemList(itemList);
		context.incrementRound();
		requestedBids.clear();
		return context;
	}

}
