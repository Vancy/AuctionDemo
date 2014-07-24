package auctioneer.bidder.communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Auctioneer {
	
	List<Bidder> bidders;
	List<Item> items;
	
	Map<Item, List<Bid>> scoreboard;
	Map<Item, List<Bid>> allBids;
	
	//Map<Integer, List<Bid>> tiedBids;
	
	List<Bid> requestedBids;
	
	protected Auctioneer(List<Bidder> bidders, List<Item> items) {
		this.bidders = new ArrayList<Bidder>(bidders);
		this.items = new ArrayList<Item>(items);
		this.requestedBids = new ArrayList<Bid>();
		
		//this.tiedBids = new HashMap<Integer, List<Bid>>();
		this.allBids = new TreeMap<Item, List<Bid>>();
		for (Item i : items) {
			allBids.put(i, new ArrayList<Bid>());
		}
		this.scoreboard = new TreeMap<Item, List<Bid>>();
		for (Item i : items) {
			scoreboard.put(i, new ArrayList<Bid>());
		}
	}
	
	protected boolean performRound(int roundNumber) {
		System.out.println("====< ROUND " + roundNumber + " >====");
		collectBids();
		processBids();
		System.out.println("====< END OF ROUND " + roundNumber + " RESULTS >====");
		/*for (Item item : items) {
			System.out.print("Item " + item.getID() + "\t\t\t");
		}
		System.out.println();
		for (List<Bid> b : scoreboard.values()) {
			for (Bid bid : b) {
				System.out.print(bid.toString());
			}
			System.out.print("\t");
		}
		System.out.println();*/
		for (Item item : scoreboard.keySet()) {
			System.out.print("Item " + item.getID() + ": ");
			for (Bid b : scoreboard.get(item)) {
				System.out.print(b.toString());
				System.out.print(" | ");
			}
			System.out.println();
		}
		return false;
	}
	
	private void collectBids() {
		Item currentItem;
		for (Bidder b : bidders) {
			for (int i = 0; i < items.size(); i++) {
				currentItem = items.get(i);
				
				System.out.print("Enter bidder " + b.getID() + "'s bid for item number " + currentItem.getID() + ": ");
				double value = Double.parseDouble(ScannerSingleton.getInstance().nextLine());
				
				if (value == 0) {
					if (b.getItemsBidOn().containsKey(currentItem) && 
						b.getItemsBidOn().get(currentItem).size() != 0) {
						continue;
					} else {
						requestedBids.add(b.placeBid(currentItem, 0, Auction.roundNumber));
						continue;
					}
				}
				
				if (value < currentItem.getStartingPrice()) {
					System.out.println("Cannot bid below item " + currentItem.getID() + "'s $" + currentItem.getStartingPrice() + " reserve!");
					i--;
					continue;
				}
				
				if (Auction.roundNumber == 1 && value < Auction.minimumIncrement) {
					System.out.println("Cannot bid below the minimum increment of " + Auction.minimumIncrement + "!");
					i--;
					continue;
				}
				
				if (scoreboard.get(items.get(i)).size() != 0) {
					if (value < scoreboard.get(items.get(i)).get(scoreboard.get(items.get(i)).size() - 1).getValue()) {
						System.out.println("Cannot bid below item " + currentItem.getID() + "'s current highest bid of $" + scoreboard.get(items.get(i)).get(scoreboard.get(items.get(i)).size() - 1).getValue() + "!");
						i--;
						continue;
					}
					if (Math.abs(scoreboard.get(items.get(i)).get(scoreboard.get(items.get(i)).size() - 1).getValue() - value) < Auction.minimumIncrement) {
						System.out.println("Cannot bid below the minimum increment of " + Auction.minimumIncrement + "!");
						i--;
						continue;
					}
				}
				
				requestedBids.add(b.placeBid(currentItem, value, Auction.roundNumber));
			}
		}
	}
	
	private void processBids() {
		List<Bid> temp;
		
		for (Bid b : requestedBids) {
			temp = new ArrayList<Bid>(allBids.get(b.getItem()));
			temp.add(b);
			allBids.put(b.getItem(), temp);
		}
		
		for (Item i : allBids.keySet()) {
			temp = new ArrayList<Bid>(allBids.get(i));
			Collections.sort(temp);
			
			double maxValue = temp.get(0).getValue();
			
			Bid bidToPlace = null; boolean tie = false;
			
			for (Bid b : temp) {
				if (b.getValue() == maxValue) {
					if (!tie) {
						bidToPlace = b;
						tie = true;
					} else {
						if (bidToPlace.getBidder().getID() != b.getBidder().getID())
						bidToPlace.addTiedBid(b);
					}
				}
			}
			if (tie) {
				List<Bid> bids = new ArrayList<Bid>();
				bids.add(bidToPlace);
				for (Bid b : bidToPlace.getTiedBids()) {
					bids.add(b);
				}
				//tiedBids.put(Auction.roundNumber, bids);
			}
			List<Bid> currentBids = new ArrayList<Bid>(scoreboard.get(bidToPlace.getItem()));
			currentBids.add(bidToPlace);
			scoreboard.put(bidToPlace.getItem(), currentBids);
		}
		
		requestedBids.clear();
		
	}
	
}
