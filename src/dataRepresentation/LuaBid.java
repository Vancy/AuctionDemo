package dataRepresentation;

public class LuaBid {
	private AuctionItem item;
	private double licencedPrice;
	private double unlicencedPrice;
	private boolean licencedInterest = true;
	private boolean unlicencedInterest = true;
	
	public LuaBid(AuctionItem item, double licenced, double unlicenced) {
		this.item = item;
		this.licencedPrice = licenced;
		this.unlicencedPrice = unlicenced;
	}
	
	public LuaBid(AuctionItem item, double licenced, double unlicenced, boolean licenced_interest, boolean unlicenced_interest) {
		this.item = item;
		this.licencedPrice = licenced;
		this.unlicencedPrice = unlicenced;
		this.licencedInterest = licenced_interest;
		this.unlicencedInterest = unlicenced_interest;
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
	
	public boolean interestedInLicensed() {
		return this.licencedInterest;
	}
	
	public boolean interestedInUnlicensed() {
		return this.unlicencedInterest;
	}
}
