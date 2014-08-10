package dataRepresentation;

public class AuctionItem implements Comparable<AuctionItem> {
	private int ID;
	private String name;
	private double startingPrice;
	private double price;
	
	//this parameter is used in CCA auction, SAA auction needn't use this value
	private int quantity;

	private Bidder owner;
	
	static int number_of_items = 0;
	
	public AuctionItem() {
		this.ID = ++number_of_items - 1;
		this.name = "";
		this.startingPrice = 0;
		this.quantity = 0;
		this.owner = null;
	}
	
	//copy constructor
	public AuctionItem(AuctionItem item) {
		this.ID = item.ID;
		this.name = item.name;
		this.startingPrice = item.startingPrice;
		this.price = item.price;
		
		if (null != item.owner) {
			this.owner = new Bidder(item.owner);
		}
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
	
	public AuctionItem(String n, double sp, int quantity) {
		this.ID = ++number_of_items - 1;
		this.name = n;
		this.startingPrice = sp;
		this.price = sp;
		this.quantity = quantity;
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
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
            return true;
        }
		
		if (o instanceof AuctionItem) {
			AuctionItem anotherItem = (AuctionItem)o;
			if (this.ID == anotherItem.ID) {
				return true;
			}
		}

		return false;
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
		if (null == this.owner) {
			return new Bidder();
		} else {
			return this.owner;
		}
	}
	public void setOwner(Bidder owner) {
		this.owner = owner;
	}
	
	public int getQuantity() {
		return this.quantity;
	}
	public int getID() {
		return this.ID;
	}
}
