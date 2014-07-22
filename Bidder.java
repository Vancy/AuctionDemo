
public class Bidder {
	
	private int[] bids;
	private int idNumber;
	
	public Bidder(int numberOfObjects, int idNumber) {
		bids = new int[numberOfObjects];
		this.idNumber = idNumber;
	}
	
	public int getId() {
		return idNumber;
	}
	
	/**
	 * 
	 * @param objectNumber - the object you're bidding for
	 * @param bid - the monetary value of your bid
	 */
	public void placeBid(Object o, int bid) {
		bids[o.getId() - 1] = bid;
		o.addBid(idNumber, bid);
	}

}
