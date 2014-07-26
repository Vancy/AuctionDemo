package auctioneer.bidder.communication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Agent extends Bidder {
	
	private double sunkAwarenessConstant = 1;
	
	List<Item> items;
	
	List<List<Item>> powerSet;
	
	Map<List<Item>, Integer> valuations = new HashMap<List<Item>, Integer>();
	
	public Agent(List<Item> items) {
		super(items);
		this.items = new ArrayList<Item>(items);
		powerSet = getPowerSet(items);
		assignValuationsAndSunkAwareness();
	}
	
	public Agent(String name, List<Item> items) {
		super(name, items);
		this.items = new ArrayList<Item>(items);
		powerSet = getPowerSet(items);
		assignValuationsAndSunkAwareness();
	}
	
	public Agent(List<Item> items, double sunkAwarenessConstant) {
		super(items);
		this.setSunkAwarenessConstant(sunkAwarenessConstant);
		this.items = new ArrayList<Item>(items);
		powerSet = getPowerSet(items);
		assignValuationsAndSunkAwareness();
	}
	
	public Agent(String name, List<Item> items, double sunkAwarenessConstant) {
		super(name, items);
		this.setSunkAwarenessConstant(sunkAwarenessConstant);
		this.items = new ArrayList<Item>(items);
		powerSet = getPowerSet(items);
		assignValuationsAndSunkAwareness();
	}

	public double getSunkAwarenessConstant() {
		return sunkAwarenessConstant;
	}

	public void setSunkAwarenessConstant(double sunkAwarenessConstant) {
		this.sunkAwarenessConstant = sunkAwarenessConstant;
	}
	
	private List<List<Item>> getPowerSet(Collection<Item> list) {
		List<List<Item>> ps = new ArrayList<List<Item>>();
		ps.add(new ArrayList<Item>()); // add the empty set

		// for every item in the original list
		for (Item item : list) {
			List<List<Item>> newPs = new ArrayList<List<Item>>();

			for (List<Item> subset : ps) {
				// copy all of the current powerset's subsets
				newPs.add(subset);

				// plus the subsets appended with the current item
				List<Item> newSubset = new ArrayList<Item>(subset);
				newSubset.add(item);
				newPs.add(newSubset);
			}

			// powerset is now powerset of list.subList(0, list.indexOf(item)+1)
			ps = newPs;
		}
		return ps;
	}
	
	private void assignValuationsAndSunkAwareness() {
		for (List<Item> powerSetList : powerSet) {
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
	
	private double calculateSurplus(List<Item> itemSet) {
		
		// ignore the empty set of items geenrated from the power set
		if (itemSet.isEmpty()) {
			return Double.NEGATIVE_INFINITY;
		}
		
		double valuation = valuations.get(itemSet);
		double perceivedPriceTotal = 0;
		
		for (Item item : itemSet) {
			if (Auction.auctioneer.isBidderXLeadingItemY(this.ID, item)) {
				perceivedPriceTotal += sunkAwarenessConstant * Auction.auctioneer.getLeadingBid(item);
			} else {
				perceivedPriceTotal += Auction.auctioneer.getLeadingBid(item) + Auction.minimumIncrement;
			}
		}
		
		System.out.print("{");
		for (Item i : itemSet) {
			System.out.print(i.getID() + " ");
		}
		System.out.print("}");
		System.out.println("Valuation: " + valuation + " perceviedTotal: " + perceivedPriceTotal);
		
		return valuation - perceivedPriceTotal;
	}
	
	protected Map<Item, Double> getNextRoundBehaviour() {
		
		Map<Item, Double> nextRoundBehaviour = new HashMap<Item, Double>();
		
		if (Auction.roundNumber == 1) {
			for (Item i : items) {
				List<Item> vItem = new ArrayList<Item>();
				vItem.add(i);
				nextRoundBehaviour.put(i, (double)valuations.get(vItem));
			}
			return nextRoundBehaviour;
		}
		
		List<Item> optimalItemsToBidOn = new ArrayList<Item>();
		double maxSurplus = Double.NEGATIVE_INFINITY;
		
		for (List<Item> set : powerSet) {
			double currentSurplus = calculateSurplus(set);
			if (currentSurplus > maxSurplus) {
				optimalItemsToBidOn = new ArrayList<Item>(set);
				maxSurplus = currentSurplus;
			}
		}
		
		for (Item i : items) {
			if (optimalItemsToBidOn.contains(i)) {
				if (!Auction.auctioneer.isBidderXLeadingItemY(this.ID, i)) {
					nextRoundBehaviour.put(i, Auction.auctioneer.scoreboard.get(i).get(Auction.auctioneer.scoreboard.get(i).size()-1).getValue() + Auction.minimumIncrement);
				}
			} else {
				nextRoundBehaviour.put(i, 0.0);
			}
		}
		
		return nextRoundBehaviour;
	}

}
