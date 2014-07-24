package auctioneer.bidder.communication;

import java.util.Scanner;

public class ScannerSingleton {
	
	private static Scanner reader = null;
	
	protected ScannerSingleton() {
	      // Exists only to defeat instantiation.
	}
	
	public static Scanner getInstance() {
		if (reader == null) {
			reader = new Scanner(System.in);
		}
		return reader;
	}

}
