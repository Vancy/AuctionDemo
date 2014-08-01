package dataRepresentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Agent extends Bidder {
	
	private double sunkAwarenessConstant = 1;
	
	private int numberOfItems;
	
	List<List<Integer>> powerSet;
	
	Map<List<Integer>, Integer> valuations = new HashMap<List<Integer>, Integer>();
	
	public Agent(String name, String ip, int numberOfItems) {
		super(name, ip);
		this.numberOfItems = numberOfItems;
		powerSet = getPowerSet(numberOfItems);
		assignValuationsAndSunkAwareness();
	}
	
	public Agent(String name, String ip, int numberOfItems, double sunkAwarenessConstant) {
		super(name, ip);
		this.setSunkAwarenessConstant(sunkAwarenessConstant);
		this.numberOfItems = numberOfItems;
		powerSet = getPowerSet(numberOfItems);
		assignValuationsAndSunkAwareness();
	}

	public double getSunkAwarenessConstant() {
		return sunkAwarenessConstant;
	}

	public void setSunkAwarenessConstant(double sunkAwarenessConstant) {
		this.sunkAwarenessConstant = sunkAwarenessConstant;
	}
	
	private List<List<Integer>> getPowerSet(Integer numberOfItems) {
		List<List<Integer>> ps = new ArrayList<List<Integer>>();
		ps.add(new ArrayList<Integer>()); // add the empty set

		// for every item in the original list
		for (int i = 0; i < numberOfItems; i++) {
			List<List<Integer>> newPs = new ArrayList<List<Integer>>();

			for (List<Integer> subset : ps) {
				// copy all of the current powerset's subsets
				newPs.add(subset);

				// plus the subsets appended with the current item
				List<Integer> newSubset = new ArrayList<Integer>(subset);
				newSubset.add(i);
				newPs.add(newSubset);
			}

			// powerset is now powerset of list.subList(0, list.indexOf(item)+1)
			ps = newPs;
		}
		return ps;
	}
	
	private void assignValuationsAndSunkAwareness() {
		for (List<Integer> powerSetList : powerSet) {
			if (powerSetList.size() == 1) {
				System.out.print("Enter agent " + this.getID() + "'s value for item " + powerSetList.get(0) + ": ");
				// can't use the command line to enter valuations, going to have to do it another way
				int value = Integer.parseInt(ScannerSingleton.getInstance().nextLine());
				valuations.put(powerSetList, value);
			} else {
				valuations.put(powerSetList, 0);
			}
		}
		System.out.print("Enter agent " + this.getID() + "'s sunk-awareness constant: ");
		int value = Integer.parseInt(ScannerSingleton.getInstance().nextLine());
		setSunkAwarenessConstant(value);
	}
	
	private double calculateSurplus(List<Integer> itemSet) {
		
		// ignore the empty set of items generated from the power set
		if (itemSet.isEmpty()) {
			return Double.NEGATIVE_INFINITY;
		}
		
		double valuation = valuations.get(itemSet);
		double perceivedPriceTotal = 0;
		
		for (Integer itemID : itemSet) {
			if (this.getID().equals(auctioneer.fetchItemOwner(itemID))) {
				// I need access to an auctioneer object so I can fetch the item price for a given item
				perceivedPriceTotal += sunkAwarenessConstant * auctioneer.fetchItemPrice(itemID);
			} else {
				perceivedPriceTotal += auctioneer.fetchItemPrice(itemID) + AuctionContext.minIncreament;
			}
		}	
		
		// some debugging statements
		System.out.print("{");
		for (Integer i : itemSet) {
			System.out.print(i + " ");
		}
		System.out.print("}");
		System.out.println("Valuation: " + valuation + " perceviedTotal: " + perceivedPriceTotal);
		
		return valuation - perceivedPriceTotal;
	}
	
	/**
	 * This method specifies the agents behaviour for the first round. This needs
	 * to be separated from the other rounds because the agent usually responds to
	 * other bidders' bids and the first round does not have any bids to begin with.
	 * 
	 * The behaviour will simply be bidding the agents valuations on all items.
	 * @return
	 */
	protected Map<Integer, Double> getFirstRoundBehavior() {
		
		Map<Integer, Double> nextRoundBehaviour = new HashMap<Integer, Double>();
			
		for (int i = 0; i < numberOfItems; i++) {
			List<Integer> vAuctionItem = new ArrayList<Integer>();
			vAuctionItem.add(i);
			nextRoundBehaviour.put(i, (double)valuations.get(vAuctionItem));
		}
		
		return nextRoundBehaviour;
	}
	
	protected Map<Integer, Double> getNextRoundBehaviour() {
		
		Map<Integer, Double> nextRoundBehaviour = new HashMap<Integer, Double>();
		
		List<Integer> optimalAuctionItemsToBidOn = new ArrayList<Integer>();
		double maxSurplus = Double.NEGATIVE_INFINITY;
		
		for (List<Integer> set : powerSet) {
			double currentSurplus = calculateSurplus(set);
			if (currentSurplus > maxSurplus) {
				optimalAuctionItemsToBidOn = new ArrayList<Integer>(set);
				maxSurplus = currentSurplus;
			}
		}
		
		for (int i = 0; i < numberOfItems; i++) {
			if (optimalAuctionItemsToBidOn.contains(i)) {
				if (!this.getID().equals(auctioneer.fetchItemOwner(i))) {
					nextRoundBehaviour.put(i, auctioneer.fetchItemPrice(i) + AuctionContext.minIncreament);
				} else {
					// agent is winning the bid for the item - does not need to bid again
				}
			} else {
				nextRoundBehaviour.put(i, 0.0);
			}
		}
		
		return nextRoundBehaviour;
	}

}
