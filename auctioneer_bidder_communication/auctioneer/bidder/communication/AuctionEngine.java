package auctioneer.bidder.communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuctionEngine {
	
	private static int numberOfBidders = 0;
	private static int numberOfItems = 0;
	
	public static void main(String[] args) {
		
		System.out.print("Enter the number of participants: ");
		numberOfBidders = Integer.parseInt(ScannerSingleton.getInstance().nextLine());
		
		System.out.print("Enter the number of items up for auction: ");
		numberOfItems = Integer.parseInt(ScannerSingleton.getInstance().nextLine());
		
		Auction a = new Auction(numberOfBidders, numberOfItems);
		boolean a_completionStatus = a.startAuction();
	}
}
