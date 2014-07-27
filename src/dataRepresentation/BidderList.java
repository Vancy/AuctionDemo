package dataRepresentation;

import java.util.ArrayList;

public class BidderList {
	private ArrayList<Bidder> list;
	
	public BidderList() {
		this.list = new ArrayList<Bidder>();
	}
	
	public void addBidder(Bidder b) {
		this.list.add(b);
	}
	
	public ArrayList<Bidder> getList() {
		return this.list;
	}
	
	public int size() {
		return list.size();
	}
	
	public Bidder getBidder(String name, String ip) {
		for (Bidder b: this.list) {
			if ((b.getName().equals(name)) && (b.getIP().equals(ip))) {
				return b;
			}
		}
		return null;
	}

}
