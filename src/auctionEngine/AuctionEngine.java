package auctionEngine;

import webServer.WebServer;
import dataRepresentation.AuctionEnvironment;


public class AuctionEngine {

	public static void main(String[] args) {
		
		AuctionEnvironment environment = new AuctionEnvironment();
		
		WebServer server = new WebServer(environment);
		server.start();
		
		AuctionMainWindow window = new AuctionMainWindow(environment);
		window.frame.setVisible(true);


	}

}
