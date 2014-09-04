package dataRepresentation;

import java.awt.Color;
import java.util.Random;



public class Bidder {
	
	static int BidderNumber = 0;
    
	private int ID;
	private String name = "";
	private String ipAdress = "127.0.0.1";
	private Color colorRecognition;
	
	public Bidder() {
		this.name = "Unknown";
		this.ID = -1;
		this.ipAdress = "Invalid";
	}
	
	//copy constructor
	public Bidder(Bidder bidder) {
		this.ID = bidder.ID;
		this.name = bidder.name;
		this.ipAdress = bidder.ipAdress;
		this.colorRecognition = bidder.colorRecognition;
	}
	
	public Bidder(String name, String ip) {
		this.ID = ++BidderNumber;
		this.name = name;
		this.ipAdress  = ip;
		this.colorRecognition = autoGenerateColor();
	}
	
	public int getID() {
		return ID;
	}
	public String getIP() {
		return this.ipAdress;
	}
	
	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return this.colorRecognition;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void placeBid(AuctionEnvironment environment, Bid bid) {
		environment.auctioneer.getBid(bid);
	}
	
	static private Color autoGenerateColor() {
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		// Java 'Color' class takes 3 floats, from 0 to 1.
		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();
		Color randomColor = new Color(r, g, b);
		return randomColor;
	}
	
}
