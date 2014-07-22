import java.util.Scanner;


public class Main {
	
	private static int numberOfBidders;
	private static int numberOfObjects;
	private static int reserve;
	public static Scanner reader;
	
	public static void main(String[] args) {
		// get the number of bidders and objects up for auction
		reader = new Scanner(System.in);
		System.out.print("Please enter the number of bidders: ");
		numberOfBidders = Integer.parseInt(reader.nextLine());
		System.out.print("Please enter the number of objects up for auction: ");
		numberOfObjects = Integer.parseInt(reader.nextLine());
		System.out.print("Please enter the reserve: ");
		reserve = Integer.parseInt(reader.nextLine());
		Bidder[] bidders = new Bidder[numberOfBidders];
		for (int i = 0; i < bidders.length; i++) {
			bidders[i] = new Bidder(numberOfObjects, i+1);
		}
		Auction auction = new Auction(numberOfObjects, reserve, bidders);
		
		int roundCounter = 1;
		while (true) {
			if (auction.startRound(roundCounter)) {
				System.out.println("====<NO BIDS WERE PLACED - AUCTION OVER >====");
				break;
			}
			roundCounter++;
		}
	}
	
}
