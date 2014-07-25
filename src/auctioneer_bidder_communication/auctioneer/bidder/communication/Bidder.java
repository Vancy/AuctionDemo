package auctioneer.bidder.communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Bidder {
	
	protected int ID;
	protected String name = "";
	protected Map<Item, List<Bid>> itemsBidOn;
	
	public Bidder(List<Item> items) {
		this.ID = Auction.bidderIDCounter++;
		this.itemsBidOn = new TreeMap<Item, List<Bid>>();
		for (Item i : items) {
			itemsBidOn.put(i, new ArrayList<Bid>());
		}
	}
	public Bidder(String name, List<Item> items) {
		this.ID = Auction.bidderIDCounter++;
		this.name = name;
		this.itemsBidOn = new TreeMap<Item, List<Bid>>();
		for (Item i : items) {
			itemsBidOn.put(i, new ArrayList<Bid>());
		}
	}
	
	protected Bid placeBid(Item item, double value, int roundNumber) {
		Bid b = new Bid(this, item, value, roundNumber);
		List<Bid> temp = new ArrayList<Bid>(itemsBidOn.get(item));
		temp.add(b);
		Collections.sort(temp);
		itemsBidOn.put(item, temp);
		return b;
	}
	
	protected int getID() {
		return ID;
	}
	protected void setID(int ID) {
		this.ID = ID;
	}
	
	protected String getName() {
		return name;
	}
	protected void setName(String name) {
		this.name = name;
	}
	protected Map<Item, List<Bid>> getItemsBidOn() {
		return itemsBidOn;
	}
	protected void setItemsBidOn(Map<Item, List<Bid>> itemsBidOn) {
		this.itemsBidOn = itemsBidOn;
	}
	
}
