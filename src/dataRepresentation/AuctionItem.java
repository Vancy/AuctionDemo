package dataRepresentation;

public class AuctionItem {
	private int ID;
	private String name;
	private double startingPrice;
	private double price;
	private String owner;
	
	static int number_of_items = 0;
	
	public AuctionItem() {
		this.ID = ++number_of_items - 1;
		this.name = "";
		this.startingPrice = 0;
	}
	public AuctionItem(String n, double sp) {
		this.ID = ++number_of_items - 1;
		this.name = n;
		this.startingPrice = sp;
		this.price = sp;
		this.owner = null;
	}
	
	protected void setStartingPrice(double startingPrice) {
		this.startingPrice = startingPrice;
	}
	
	protected double getStartingPrice() {
		return this.startingPrice ;
	}
	
	public int compareTo(AuctionItem i) {
		if (this.ID == i.ID) {
			return 0;
		} else if (this.ID < i.ID) {
			return -1;
		} else {
			return 1;
		}
	}
	public String getName() {
		return this.name;
	}
	public void setPrice(double newPrice) {
		this.price = newPrice;
	}
	public double getPrice() {
		return this.price;
	}
	public String getOwner() {
		return this.owner;
	}
	public int getID() {
		return this.ID;
	}
}
