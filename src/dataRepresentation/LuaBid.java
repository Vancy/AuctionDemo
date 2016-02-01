package dataRepresentation;

public class LuaBid {
	private AuctionItem item;
	private double licencedPrice;
	private double unlicencedPrice;
	
	public LuaBid(AuctionItem item, double licenced, double unlicenced) {
		this.item = item;
		this.licencedPrice = licenced;
		this.unlicencedPrice = unlicenced;
	}
	
	public double getLicencedBidPrice() {
		return this.licencedPrice;
	}
	
	public double getUnlicencedBidPrice() {
		return this.unlicencedPrice;
	}
	
	public int ItemID() {
		return this.item.getID();
	}
}
