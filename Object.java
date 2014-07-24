
public class Object {
	
	private int[] bids;
	private int idNumber;
	public int highestCurrentBid;
	public boolean[] highestBidders;
	
	public Object(int numberOfBidders, int idNumber) {
		this.idNumber = idNumber;
		bids = new int[numberOfBidders];
		highestBidders = new boolean[numberOfBidders];
	}
	
	public void addBid(int bidderId, int bid) {
		bids[bidderId - 1] = bid;
		if (bid > highestCurrentBid) {
			highestCurrentBid = bid;
			for (int i = 0; i < highestBidders.length; i++) {
				highestBidders[i] = false;
			}
			highestBidders[bidderId-1] = true;
		} else if (bid == highestCurrentBid) {
			highestBidders[bidderId-1] = true;
		}
	}
	
	public int[] getBids() {
		return bids;
	}
	
	public int getId() {
		return idNumber;
	}
	
	
	
}
