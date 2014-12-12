package dataRepresentation;

import java.util.ArrayList;
import java.util.List;

public class CCABiddingPackage {
	private Bidder bidder;
	private double price;
	private ArrayList<AuctionItem> itemList;
	
	public CCABiddingPackage(Bidder bidder, double price, ArrayList<AuctionItem> list) {
		this.bidder = bidder;
		this.price = price;
		this.itemList = list;
	}
	
	public double getPrice() {
		return this.price;
	}
	
	public List<AuctionItem> getItemList() {
		return this.itemList;
	}
	
	public Bidder getBidder() {
		return this.bidder;
	}
}
