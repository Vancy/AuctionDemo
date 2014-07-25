package auctioneer.bidder.communication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Agent extends Bidder {
	
	private double sunkAwarenessConstant = 1;
	
	List<Item> items;
	
	List<List<Item>> powerSet;
	
	Map<List<Item>, Integer> valuations = new TreeMap<List<Item>, Integer>();
	
	public Agent(List<Item> items) {
		super(items);
		items = new ArrayList<Item>(items);
		powerSet = getPowerSet(items);
	}
	
	public Agent(String name, List<Item> items) {
		super(name, items);
		items = new ArrayList<Item>(items);
		powerSet = getPowerSet(items);
	}
	
	public Agent(List<Item> items, double sunkAwarenessConstant) {
		super(items);
		this.setSunkAwarenessConstant(sunkAwarenessConstant);
		items = new ArrayList<Item>(items);
		powerSet = getPowerSet(items);
	}
	
	public Agent(String name, List<Item> items, double sunkAwarenessConstant) {
		super(name, items);
		this.setSunkAwarenessConstant(sunkAwarenessConstant);
		items = new ArrayList<Item>(items);
		powerSet = getPowerSet(items);
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
	
	private void assignValuations() {
		boolean printedFirstOne;
		for (List<Item> powerSetList : powerSet) {
			printedFirstOne = false;
			System.out.print("Enter the value for the set of items {");
			for (Item i : powerSetList) {
				if (printedFirstOne) {
					System.out.print(", ");
				}
				System.out.print(i.getID());
				printedFirstOne = true;
			}
			System.out.println("}");
			int value = Integer.parseInt(ScannerSingleton.getInstance().nextLine());
			valuations.put(powerSetList, value);
		}
	}
	
	private double calculateSurplus(List<Item> itemSet) {
		
		double valuation = valuations.get(itemSet);
		double perceivedPriceTotal = 0;
		
		for (Item item : itemSet) {
			if (Auction.auctioneer.isBidderXLeadingItemY(this.ID, item)) {
				perceivedPriceTotal += sunkAwarenessConstant * Auction.auctioneer.getLeadingBid(item).getValue();
			} else {
				perceivedPriceTotal += Auction.auctioneer.getLeadingBid(item).getValue() + Auction.minimumIncrement;
			}
		}
	
		return valuation - perceivedPriceTotal;
	}
	
	private List<Item> getNextRoundBehaviour() {
		List<Item> optimalItemsToBidOn = new ArrayList<Item>();
		int maxSurplus = 0;
		
		for (List<Item> set : powerSet) {
			if (calculateSurplus(set) > maxSurplus) {
				optimalItemsToBidOn = new ArrayList<Item>(set);
			}
		}
		
		// if optimalItemsToBidOn is empty, it means all surpluses were negative
		return optimalItemsToBidOn;
	}

}
