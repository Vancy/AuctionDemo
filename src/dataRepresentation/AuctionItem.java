package dataRepresentation;

public class AuctionItem {
	private String name;
	private float basePrice;
	private float price;
	
	public AuctionItem(String n, float p) {
		this.name = n;
		this.basePrice = p;
		this.price = p;
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
}
