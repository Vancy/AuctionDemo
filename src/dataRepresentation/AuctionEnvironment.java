package dataRepresentation;

public class AuctionEnvironment {
	
	
	public boolean AuctionStarted = false;
	public volatile AuctionContext context;
	public BidderList bidderList;
	public Auctioneer auctioneer;
	
	
	public AuctionEnvironment() {
		bidderList = new BidderList();
		context = new AuctionContext(bidderList);
		auctioneer = new Auctioneer(this);
	}
	public AuctionEnvironment(BidderList list, AuctionContext c) {
		this.context = c;
		this.bidderList = list;
	}
	public void resetEnv() {
		//clear previous round bidder winning message, if any.
		this.bidderList.clearBiddersLuaWinningMsg();
		//set the context basic info to start mode.
		this.context.setRound(0);
		this.context.setFinalRound(false);
		this.context.clearAuctionItems();
	}
	
	public void startAuctioneer() {
		this.auctioneer = new Auctioneer(this);
		this.auctioneer.start();
	}
		
}
