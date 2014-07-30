package dataRepresentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Agent extends Bidder {
	
	private double sunkAwarenessConstant = 1;
	
	List<AuctionItem> items;
	
	List<List<AuctionItem>> powerSet;
	
	Map<List<AuctionItem>, Integer> valuations = new HashMap<List<AuctionItem>, Integer>();
	
	public Agent(String name, String ip, List<AuctionItem> items) {
		super(name, ip);
		this.items = new ArrayList<AuctionItem>(items);
		powerSet = getPowerSet(items);
		assignValuationsAndSunkAwareness();
	}
	
	public Agent(String name, String ip, List<AuctionItem> items, double sunkAwarenessConstant) {
		super(name, ip);
		this.setSunkAwarenessConstant(sunkAwarenessConstant);
		this.items = new ArrayList<AuctionItem>(items);
		powerSet = getPowerSet(items);
		assignValuationsAndSunkAwareness();
	}

	public double getSunkAwarenessConstant() {
		return sunkAwarenessConstant;
	}

	public void setSunkAwarenessConstant(double sunkAwarenessConstant) {
		this.sunkAwarenessConstant = sunkAwarenessConstant;
	}
	
	private List<List<AuctionItem>> getPowerSet(Collection<AuctionItem> list) {
		List<List<AuctionItem>> ps = new ArrayList<List<AuctionItem>>();
		ps.add(new ArrayList<AuctionItem>()); // add the empty set

		// for every item in the original list
		for (AuctionItem item : list) {
			List<List<AuctionItem>> newPs = new ArrayList<List<AuctionItem>>();

			for (List<AuctionItem> subset : ps) {
				// copy all of the current powerset's subsets
				newPs.add(subset);

				// plus the subsets appended with the current item
				List<AuctionItem> newSubset = new ArrayList<AuctionItem>(subset);
				newSubset.add(item);
				newPs.add(newSubset);
			}

			// powerset is now powerset of list.subList(0, list.indexOf(item)+1)
			ps = newPs;
		}
		return ps;
	}
	
	private void assignValuationsAndSunkAwareness() {
		for (List<AuctionItem> powerSetList : powerSet) {
			if (powerSetList.size() == 1) {
				System.out.print("Enter agent " + this.ID + "'s value for item " + powerSetList.get(0).getID() + ": ");
				int value = Integer.parseInt(ScannerSingleton.getInstance().nextLine());
				valuations.put(powerSetList, value);
			} else {
				valuations.put(powerSetList, 0);
			}
		}
		System.out.print("Enter agent " + this.ID + "'s sunk-awareness constant: ");
		int value = Integer.parseInt(ScannerSingleton.getInstance().nextLine());
		setSunkAwarenessConstant(value);
	}
	
	private double calculateSurplus(List<AuctionItem> itemSet) {
		
		// ignore the empty set of items geenrated from the power set
		if (itemSet.isEmpty()) {
			return Double.NEGATIVE_INFINITY;
		}
		
		double valuation = valuations.get(itemSet);
		double perceivedPriceTotal = 0;
		
		for (AuctionItem item : itemSet) {
			if (Auction.auctioneer.isBidderXLeadingAuctionItemY(this.ID, item)) {
				perceivedPriceTotal += sunkAwarenessConstant * Auction.auctioneer.getLeadingBid(item);
			} else {
				perceivedPriceTotal += Auction.auctioneer.getLeadingBid(item) + Auction.minimumIncrement;
			}
		}
		
		System.out.print("{");
		for (AuctionItem i : itemSet) {
			System.out.print(i.getID() + " ");
		}
		System.out.print("}");
		System.out.println("Valuation: " + valuation + " perceviedTotal: " + perceivedPriceTotal);
		
		return valuation - perceivedPriceTotal;
	}
	
	protected Map<AuctionItem, Double> getFirstRoundBehavior() {
		
		Map<AuctionItem, Double> nextRoundBehaviour = new HashMap<AuctionItem, Double>();
			
		for (AuctionItem i : items) {
			List<AuctionItem> vAuctionItem = new ArrayList<AuctionItem>();
			vAuctionItem.add(i);
			nextRoundBehaviour.put(i, (double)valuations.get(vAuctionItem));
		}
		
		return nextRoundBehaviour;
	}
	
	protected Map<AuctionItem, Double> getNextRoundBehaviour() {
		
		Map<AuctionItem, Double> nextRoundBehaviour = new HashMap<AuctionItem, Double>();
		
		List<AuctionItem> optimalAuctionItemsToBidOn = new ArrayList<AuctionItem>();
		double maxSurplus = Double.NEGATIVE_INFINITY;
		
		for (List<AuctionItem> set : powerSet) {
			double currentSurplus = calculateSurplus(set);
			if (currentSurplus > maxSurplus) {
				optimalAuctionItemsToBidOn = new ArrayList<AuctionItem>(set);
				maxSurplus = currentSurplus;
			}
		}
		
		for (AuctionItem i : items) {
			if (optimalAuctionItemsToBidOn.contains(i)) {
				if (!Auction.auctioneer.isBidderXLeadingAuctionItemY(this.ID, i)) {
					nextRoundBehaviour.put(i, Auction.auctioneer.scoreboard.get(i).get(Auction.auctioneer.scoreboard.get(i).size()-1).getValue() + Auction.minimumIncrement);
				}
			} else {
				nextRoundBehaviour.put(i, 0.0);
			}
		}
		
		return nextRoundBehaviour;
	}

}
