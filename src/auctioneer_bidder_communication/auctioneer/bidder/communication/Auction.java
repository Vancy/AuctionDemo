package auctioneer.bidder.communication;

import java.util.ArrayList;
import java.util.List;

public class Auction {
	
	protected static int bidderIDCounter = 1;
	protected static int itemIDCounter = 1;
	
	protected static int roundNumber = 1;
	
	protected static double minimumIncrement = 1.0;
	
	protected static Auctioneer auctioneer;
	
	private List<Bidder> bidders;

	private List<Item> items;
	
	protected Auction(int numberOfHumanBidders, int numberOfAgentBidders, int numberOfItems) {
		bidders = new ArrayList<Bidder>();
		items = new ArrayList<Item>();
		
		for (int i = 0; i < numberOfItems; i++) {
			items.add(new Item());
		}
		for (int i = 0; i < numberOfHumanBidders; i++) {
			bidders.add(new Bidder(items));
		}
		for (int i = 0; i < numberOfAgentBidders; i++) {
			bidders.add(new Agent(items));
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
