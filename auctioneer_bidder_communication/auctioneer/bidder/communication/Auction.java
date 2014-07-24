package auctioneer.bidder.communication;

import java.util.ArrayList;
import java.util.List;

public class Auction {
	
	protected static int bidderIDCounter = 1;
	protected static int itemIDCounter = 1;
	
	protected static int roundNumber = 1;
	
	protected static double minimumIncrement = 10.0;
	
	private Auctioneer auctioneer;
	private List<Bidder> bidders;
	private List<Item> items;
	
	protected Auction(int numberOfBidders, int numberOfItems) {
		bidders = new ArrayList<Bidder>();
		items = new ArrayList<Item>();
		
		for (int i = 0; i < numberOfItems; i++) {
			items.add(new Item());
		}
		for (int i = 0; i < numberOfBidders; i++) {
			bidders.add(new Bidder(items));
		}
		
		auctioneer = new Auctioneer(bidders, items);
	}
	
	protected boolean startAuction() {
		while (true) {
			if (auctioneer.performRound(roundNumber)) {
				break;
			}
			roundNumber++;
		}
		return true;
	}
	
	public double getMinimumIncrement() {
		return minimumIncrement;
	}

	public void setMinimumIncrement(double minimumIncrement) {
		Auction.minimumIncrement = minimumIncrement;
	}

}
