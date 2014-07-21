import java.util.ArrayList;


public class Auction {
	
	private int reserve;
	private int numberOfObjects;
	private ArrayList<Bidder> bidders;
	private ArrayList<Object> objects;
	private String standings = "";
	
	public Auction(int numberOfObjects, int reserve, Bidder[] b) {
		this.reserve = reserve;
		this.numberOfObjects = numberOfObjects;
		bidders = new ArrayList<Bidder>();
		objects = new ArrayList<Object>();
		for (Bidder bidder : b) {
			bidders.add(bidder);
		}
		for (int i = 1; i <= numberOfObjects; i++) {
			objects.add(new Object(bidders.size(), i));
		}
	}
	
	/**
	 * 
	 * @param round - what round to begin
	 * @return whether or not there were no new bids in the round
	 */
	public boolean startRound(int round) {
		System.out.println("====< ROUND " + round + " >====");
		boolean everyonePassed = false;
		int passCounter = 0;
		for (Bidder b : bidders) {
			for (int i = 1; i <= numberOfObjects; i++) {
				if (objects.get(i-1).highestBidders[b.getId()-1] && onlyOneTrue(objects.get(i-1).highestBidders)) {
					System.out.println("Bidder " + b.getId() + " is currently leading the bid for Object " + i + ". Cannot bid again.");
					passCounter++;
					continue;
				}
				System.out.print("Please enter bidder " + b.getId() + "'s bid for object " + i + ": ");
				int bid = Integer.parseInt(Main.reader.nextLine());
				if (bid < reserve && bid != 0) {
					System.out.println("Cannot bid below the $" + reserve + " reserve! Try again.");
					i--; continue;
				}
				if (bid <= objects.get(i-1).getBids()[b.getId()-1] && bid != 0) {
					System.out.println("Cannot bid below than your current bid of $" + objects.get(i-1).getBids()[b.getId()-1] + " Try again.");
					i--; continue;
				}
				if (bid != 0) {
					b.placeBid(objects.get(i-1), bid);
				} else {
					passCounter++;
				}
			}
		}
		
		// print current standings
		System.out.println("END OF ROUND " + round + " STANDINGS");
		for (int i = 1; i <= numberOfObjects; i++) {
			System.out.print("|   " + i + "   ");
		}
		System.out.println("|");
		for (int i = 1; i <= numberOfObjects; i++) {
			int max = max(objects.get(i - 1).getBids());
			standings += "| " + max + "(" + highestBidders(objects.get(i - 1)) + ") ";
		}
		standings += ("|\n");
		System.out.println(standings);
		
		if (passCounter == bidders.size() * objects.size()) {
			everyonePassed = true;
		}
		return everyonePassed;
	}
	
	private int max(int[] numbers) {
		int max = 0;
		for (int i = 0; i < numbers.length; i++) {
			if (numbers[i] > max) {
				max = numbers[i];
			}
		}
		return max;
	}
	
	private boolean onlyOneTrue(boolean[] booleans) {
		int counter = 0;
		for (boolean b : booleans) {
			if (b) {
				counter++;
			}
		}
		return counter == 1 ? true : false;
	}
	
	private String highestBidders(Object o) {
		String s = "";
		boolean doneOne = false;
		for (int i = 0; i < o.highestBidders.length; i++) {
			if (o.highestBidders[i]) {
				if (doneOne) {
					s += "&";
				}
				s += (i+1)+"";
				doneOne = true;
			}
		}
		return s;
	}
	
}
