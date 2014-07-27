package dataRepresentation;

import java.util.ArrayList;


public class Auctioneer {
	
	ArrayList<Bid> requestedBids;
	AuctionContext context;
	
	public Auctioneer(AuctionContext c) {
		this.context = c;
		this.requestedBids = new ArrayList<Bid>();
	}
	
	public void getBid(Bid bid) {
		this.requestedBids.add(bid);
	}
	

	public AuctionContext nextRound() {
		/*
		 * May use several delay mechanism,
		 * to ensure every bidder get the same AuctionContext of next round
		 */
		return context;
	}

}
