package auctioneer.bidder.communication;

public class Item implements Comparable<Item> {
	
	private int ID;
	private String name = "";
	
	private double startingPrice = 0.0;
	
	public Item() {
		this.ID = Auction.itemIDCounter++;
	}
	public Item(String name) {
		this.ID = Auction.itemIDCounter++;
		this.name = name;
	}
	public Item(double startingPrice) {
		this.ID = Auction.itemIDCounter++;
		this.startingPrice = startingPrice;
	}
	public Item(String name, double startingPrice) {
		this.ID = Auction.itemIDCounter++;
		this.name = name;
		this.startingPrice = startingPrice;
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
	
	protected double getStartingPrice() {
		return startingPrice;
	}
	protected void setStartingPrice(double startingPrice) {
		this.startingPrice = startingPrice;
	}
	
	@Override
	public int compareTo(Item i) {
		if (this.ID == i.ID) {
			return 0;
		} else if (this.ID < i.ID) {
			return -1;
		} else {
			return 1;
		}
	}
	
}
