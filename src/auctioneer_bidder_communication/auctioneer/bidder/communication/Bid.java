package auctioneer.bidder.communication;

import java.util.ArrayList;
import java.util.List;

public class Bid implements Comparable<Bid> {
	
	private double value;
	private Bidder bidder;
	private Item item;

	private int roundNumber;
	
	private List<Bid> tiedBids;
	private boolean isTied = false;
	
	public Bid(Bidder bidder, Item item, double value, int roundNumber) {
		this.bidder = bidder;
		this.item = item;
		this.value = value;
		this.setRoundNumber(roundNumber);
		tiedBids = new ArrayList<Bid>();
	}
	
	@Override
	public String toString() {
		if (isTied) {
			String s = "Bidders " + this.bidder.getID() + "";
			for (Bid b : tiedBids) {
				if (!s.contains(b.getBidder().getID()+"")) {
					s += "&";
					s += b.getBidder().getID();
				}
			}
			s += " - $" + value;
			return s;
		} else {
			if (bidder instanceof Agent) {
				return "Agent " + bidder.getID() + " - $" + value;
			} else {
				return "Bidder " + bidder.getID() + " - $" + value;
			}
		}
	}
	
	/*protected Bid clone() {
		Bid b = new Bid(bidder, item, value, roundNumber);
		b.setTiedBids(this.tiedBids);
		b.setTied(this.isTied);
		return b;
	}*/
	
	public void addTiedBid(Bid b) {
		tiedBids.add(b);
		setTied(true);
	}
	
	public List<Bid> getTiedBids() {
		return tiedBids;
	}
	
	public void setTiedBids(List<Bid> b) {
		this.tiedBids = new ArrayList<Bid>(b);
	}
	
	public Bidder getBidder() {
		return bidder;
	}

	public void setBidder(Bidder bidder) {
		this.bidder = bidder;
	}
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getRoundNumber() {
		return roundNumber;
	}

	public void setRoundNumber(int roundNumber) {
		this.roundNumber = roundNumber;
	}

	public boolean isTied() {
		return isTied;
	}

	public void setTied(boolean isTied) {
		this.isTied = isTied;
	}

	@Override
	public int compareTo(Bid b) {
		if (this.value == b.value) {
			return 0;
		} else if (this.value < b.value) {
			return 1;
		} else {
			return -1;
		}
	}
	
}
