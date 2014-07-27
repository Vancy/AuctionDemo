package dataRepresentation;

public class AuctionEnvironment {
	
	public AuctionContext context;
	public BidderList bidderList;
	public Auctioneer auctioneer;
	
	
	public AuctionEnvironment() {
		bidderList = new BidderList();
		context = new AuctionContext();
		auctioneer = new Auctioneer(context);
	}
	public AuctionEnvironment(BidderList list, AuctionContext c) {
		this.context = c;
		this.bidderList = list;
	}
}