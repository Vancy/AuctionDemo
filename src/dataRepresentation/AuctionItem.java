package dataRepresentation;

import java.util.HashMap;

public class AuctionItem implements Comparable<AuctionItem> {
	private int ID;
	private String name;
	private double startingPrice;
	private double price;
	private Bidder owner;
	
	//this five parameters are used in CCA auction, SAA auction needn't use this values
	final private int quantity;
	final private int eligibilityPoint;
	private int quantity_required;
	private HashMap<String, Integer> owners = new HashMap<String, Integer>();
	public boolean biddingFinised = false;
	
	//the parameter maxPrice is only used in LUA. SAA and CCA needn't use this value
	private double maxPrice;
	
	static int number_of_items = 0;
	
	//default constructor, initial item object with invalid values.
	public AuctionItem() {
		this.ID = ++number_of_items - 1;
		this.name = "";
		this.startingPrice = 0;
		this.quantity = 0;
		this.quantity_required = 0;
		this.owner = null;
		this.eligibilityPoint = 0;
		this.maxPrice = 0;
	}
	
	//copy constructor
	public AuctionItem(AuctionItem item) {
		this.ID = item.ID;
		this.name = item.name;
		this.startingPrice = item.startingPrice;
		this.price = item.price;
		
		if (null != item.owner) {
			this.owner = new Bidder(item.owner);
		}
		
		this.quantity = item.quantity;
		this.quantity_required = item.quantity_required;
		
		this.eligibilityPoint = item.eligibilityPoint;
		this.maxPrice = item.maxPrice;
	}
	
	//create item object by initializing name and start price.
	public AuctionItem(String n, double sp) {
		this.ID = ++number_of_items - 1;
		this.name = n;
		this.startingPrice = sp;
		this.price = sp;
		this.owner = null;
		
		this.quantity = 0;
		this.quantity_required = 0;
		this.eligibilityPoint = 0;
		this.maxPrice = 0;
	}
	
	//create item object by initializing id, name and start price.
	public AuctionItem(int id, String n, double sp) {
		this.ID = id;
		this.name = n;
		this.startingPrice = sp;
		this.price = sp;
		this.owner = null;
		
		this.quantity = 0;
		this.quantity_required = 0;
		this.eligibilityPoint = 0;
		this.maxPrice = 0;
	}
	
	//create item object by initializing name, start price and max price.
	public AuctionItem(String n, double sp, double mp) {
		this.name = n;
		this.startingPrice = sp;
		this.price = sp;
		this.owner = null;
		
		this.quantity = 0;
		this.quantity_required = 0;
		this.eligibilityPoint = 0;
		this.maxPrice = mp;
	}
	
	/*
	 * this constructor is used by bidders who attend CCA auction.
	 * require indicate how many items bidder wants
	 */
	public AuctionItem(int id, String n, int require) {
		this.ID = id;
		this.name = n;
		// This is invalid for bidders
		this.quantity = 0;
		this.quantity_required = require;
		this.owner = null;
		this.eligibilityPoint = 0;
		this.maxPrice = 0;
	}
	
	public AuctionItem(String n, double sp, int quantity, int eligibility) {
		this.ID = ++number_of_items - 1;
		this.name = n;
		this.startingPrice = sp;
		this.price = sp;
		this.quantity = quantity;
		this.owner = null;
		this.eligibilityPoint = eligibility;
		this.maxPrice = 0;
	}
	
	protected void setStartingPrice(double startingPrice) {
		this.startingPrice = startingPrice;
	}
		
	@Override
	public int compareTo(AuctionItem i) {
		if (this.ID == i.ID) {
			return 0;
		} else if (this.ID < i.ID) {
			return -1;
		} else {
			return 1;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
            return true;
        }
		
		if (o instanceof AuctionItem) {
			AuctionItem anotherItem = (AuctionItem)o;
			if (this.ID == anotherItem.ID) {
				return true;
			}
		}

		return false;
	}
	
	public int getID() {
		return this.ID;
	}
	public String getName() {
		return this.name;
	}
	
	protected double getStartingPrice() {
		return this.startingPrice;
	}
	
	protected double getMaxPrice() {
		return this.maxPrice;
	}
	
	public void setPrice(double newPrice) {
		this.price = newPrice;
	}
	public double getPrice() {
		return this.price;
	}
	public Bidder getOwner() {
		if (null == this.owner) {
			return new Bidder();
		} else {
			return this.owner;
		}
	}
	public void setOwner(Bidder owner) {
		this.owner = owner;
	}
	
	public int getQuantity() {
		return this.quantity;
	}
	
	public int getRequiredQuantity() {
		return this.quantity_required;
	}
	
	public int getEligibility() {
		return this.eligibilityPoint;
	}
		
	public void setRequiredQuantity(int quantity) {
		this.quantity_required = quantity;
	}
	
	//for CCA auction to use
	public void clearOwners() {
		this.owners.clear();
	}
	//for CCA auction to use
	public void placeOwner(String name, int quantity) {
		this.owners.put(name, quantity);
	}
	//for CCA auction to use
	public HashMap<String, Integer> getOwners() {
		return this.owners;
	}
}
