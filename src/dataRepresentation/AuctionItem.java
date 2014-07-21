package dataRepresentation;

public class AuctionItem {
	private String name;
	private float basePrice;
	private float price;
	private String owner;
	
	public AuctionItem(String n, float bp) {
		this.name = n;
		this.basePrice = bp;
		this.price = bp;
		this.owner = null;
	}
	public String getName() {
		return this.name;
	}
	public void setPrice(float newPrice) {
		this.price = newPrice;
	}
	public float getPrice() {
		return this.price;
	}
	public String getOwner() {
		return this.owner;
	}
}
