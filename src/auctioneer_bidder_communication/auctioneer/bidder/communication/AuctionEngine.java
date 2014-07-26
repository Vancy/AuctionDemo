package auctioneer.bidder.communication;

public class AuctionEngine {
	
	private static int numberOfHumanBidders = 0;
	private static int numberOfAgentBidders = 0;
	private static int numberOfItems = 0;
	
	public static void main(String[] args) {
		
		System.out.print("Enter the number of human bidders: ");
		numberOfHumanBidders = Integer.parseInt(ScannerSingleton.getInstance().nextLine());
		
		System.out.print("Enter the number of agent bidders: ");
		numberOfAgentBidders = Integer.parseInt(ScannerSingleton.getInstance().nextLine());
		
		System.out.print("Enter the number of items up for auction: ");
		numberOfItems = Integer.parseInt(ScannerSingleton.getInstance().nextLine());
		
		Auction a = new Auction(numberOfHumanBidders, numberOfAgentBidders, numberOfItems);
		boolean a_completionStatus = a.startAuction();
	}
}
