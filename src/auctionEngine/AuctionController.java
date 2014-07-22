package auctionEngine;

import java.util.ArrayList;

import dataRepresentation.*;
import webServer.WebServer;

public class AuctionController {

	public static void main(String[] args) {
		AuctionContext context = createAuctionContextExample();
		System.out.println(context.generateXml());
		//AuctionContext.outputXml(context.generateXml(), "./Test.xml");

		WebServer server = new  WebServer(context);
		server.start();
		


	}
	
	public static AuctionContext createAuctionContextExample() {
		ArrayList<AuctionItem> list = new ArrayList<AuctionItem>();
		list.add(new AuctionItem("dogfood", 100));
		list.add(new AuctionItem("kennel", 250));
		AuctionContext ac = new AuctionContext(20, list);
		return ac;
	}

}
