package dataRepresentation;

import java.awt.Color;
import java.util.Random;



public class Bidder {
	
	static int BidderNumber = 0;
    
	private int ID;
	private String name = "";
	private String ipAddress = "127.0.0.1";
	private Color colorRecognition;
	
	private int eligibility;
	private int activity;
	private int activityCounter = AuctionContext.numberOfActivityRuleWaivers;
	
	/*this field store the warning message auctioneer gives to this bidder, if any.
	 * this message will be sent to client side, then human bidder can read this message.
	 */
	private String warningMessage = "";
	
	public int getEligibility() {
		return eligibility;
	}

	public void setEligibility(int eligibility) {
		this.eligibility = eligibility;
		System.out.println(this.name + " eligibility is: " + eligibility);
	}

	public int getActivity() {
		return activity;
	}

	public void setActivity(int activity) {
		this.activity = activity;
		System.out.println(this.name + " actvity is: " + activity);
	}
	
	public void decrementActivityCounter() {
		System.err.println(this.name + " actvity counter decremented!");
		activityCounter--;
		this.warningMessage = "WARNING! You were inactive last round and have lost a waiver.";
		if (activityCounter < 0) {
			System.err.println(this.name + " kicked from auction!");
			this.warningMessage = "ATTENTION! Your inactivity has exceeded this auction's limit and the auctioneer has kicked you out.";
		}
	}
	
	public void auctionRuleVerify() {
		if (this.getActivity() > this.getEligibility()) {
			this.decrementActivityCounter();
		} else {
			//clear warning message
			this.warningMessage = "";
		}
	}
	
	public int getActivityCounter() {
		return activityCounter;
	}
	
	public Bidder() {
		this.name = "Unknown";
		this.ID = -1;
		this.ipAddress = "Invalid";
	}
	
	//copy constructor
	public Bidder(Bidder bidder) {
		this.ID = bidder.ID;
		this.name = bidder.name;
		this.ipAddress = bidder.ipAddress;
		this.colorRecognition = bidder.colorRecognition;
	}
	
	public Bidder(String name, String ip) {
		this.ID = ++BidderNumber;
		this.name = name;
		this.ipAddress  = ip;
		this.colorRecognition = autoGenerateColor();
	}
	
	public int getID() {
		return ID;
	}
	public String getIP() {
		return this.ipAddress;
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
