package dataRepresentation;

public class AuctionItem implements Comparable<AuctionItem> {
	private int ID;
	private String name;
	private double startingPrice;
	private double price;
	// ogen, 1st august changed owner from String to int.
	// the owner should be determined by ID instead of name
	// because there could exist bidders with same names.
	private Bidder owner;
	
	static int number_of_items = 0;
	
	public AuctionItem() {
		this.ID = ++number_of_items - 1;
		this.name = "";
		this.startingPrice = 0;
		this.owner = null;
	}
	public AuctionItem(String n, double sp) {
		this.ID = ++number_of_items - 1;
		this.name = n;
		this.startingPrice = sp;
		this.price = sp;
		this.owner = null;
	}
	
	public AuctionItem(int id, String n, double sp) {
		this.ID = id;
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
	
	@Override
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
	public Bidder getOwner() {
		return this.owner;
	}
	public void setOwner(Bidder owner) {
		this.owner = owner;
	}
	public int getID() {
		return this.ID;
	}
}
