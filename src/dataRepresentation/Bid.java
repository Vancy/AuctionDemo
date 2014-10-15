package dataRepresentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Bid{
	
	private Bidder bidder;
	private List<AuctionItem> itemList;

	public Bid(Bidder bidder, List<AuctionItem> list) {
		this.bidder = bidder;
		this.itemList = list;
	}
		
	public Bidder getBidder() {
		return bidder;
	}

	public void setBidder(Bidder bidder) {
		this.bidder = bidder;
	}
	
	public List<AuctionItem> getItemList() {
		synchronized (this) {
			Collections.sort(itemList);
		}
		return this.itemList;
	}

	public void setItemList(ArrayList<AuctionItem> list) {
		this.itemList = list;
	}
	
}
