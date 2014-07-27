package dataRepresentation;

import java.util.ArrayList;


public class Bid{
	
	private Bidder bidder;
	private ArrayList<AuctionItem> itemList;


	
	public Bid(Bidder bidder, ArrayList<AuctionItem> list) {
		this.bidder = bidder;
		this.itemList = list;
	}
		
	public Bidder getBidder() {
		return bidder;
	}

	public void setBidder(Bidder bidder) {
		this.bidder = bidder;
	}
	
	public ArrayList<AuctionItem> getItemList() {
		return this.itemList;
	}

	public void setItemList(ArrayList<AuctionItem> list) {
		this.itemList = list;
	}
	
}
