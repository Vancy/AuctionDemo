package dataRepresentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SAAAgent extends Agent {

	private double sunkAwarenessConstant = 1;

	protected List<Bid> memory = new ArrayList<Bid>();
	List<AuctionItem> items;

	Map<List<AuctionItem>, Double> valuations;

	/**
	 * 
	 * @param name
	 * @param ip
	 * @param items
	 *            A list of items in the auction
	 * @param valuations
	 *            A map. List<AuctionItem> is mapped to Double representing the
	 *            valuation. You can use the getPowerSet method defined in this
	 *            Agent.java class to generate a power set from a list. For
	 *            example, inputting a list {1, 2, 3} will give you { {1}, {2},
	 *            {3}, {1, 2}, {1, 3}, {2, 3}, {1, 2, 3} }
	 * @param k
	 *            sunk-awareness constant
	 */
	public SAAAgent(String name, String ip, List<AuctionItem> items,
			Map<List<AuctionItem>, Double> valuations, double k) {
		super(name, ip);
		this.items = items;
		setSunkAwarenessConstant(k);
		this.valuations = new HashMap<List<AuctionItem>, Double>(valuations);
	}
	
	public Map<List<AuctionItem>, Double> getValuations() {
		return valuations;
	}
	
	public double getSunkAwarenessConstant() {
		return sunkAwarenessConstant;
	}

	public void setSunkAwarenessConstant(double sunkAwarenessConstant) {
		this.sunkAwarenessConstant = sunkAwarenessConstant;
	}

	public Bid auctionResponse(AuctionContext ac) {

		List<AuctionItem> bidderItemList = new ArrayList<AuctionItem>();
		Map<AuctionItem, Double> behaviour = getNextRoundBehaviour(ac);

		for (AuctionItem i : behaviour.keySet()) {
			// Xing add this line at 2014.8.11, put bid price into AuctionItem
			i.setPrice(behaviour.get(i)); // xing added
			bidderItemList.add(new AuctionItem(i));
		}

		Bid bid = new Bid(this, bidderItemList);
		memory.add(bid);
		return bid;
	}

//	private List<List<AuctionItem>> getPowerSet(List<AuctionItem> itemList) {
//		List<List<AuctionItem>> ps = new ArrayList<List<AuctionItem>>();
//		ps.add(new ArrayList<AuctionItem>()); // add the empty set
//
//		// for every item in the original list
//		for (AuctionItem i : itemList) {
//			List<List<AuctionItem>> newPs = new ArrayList<List<AuctionItem>>();
//
//			for (List<AuctionItem> subset : ps) {
//				// copy all of the current powerset's subsets
//				newPs.add(subset);
//
//				// plus the subsets appended with the current item
//				List<AuctionItem> newSubset = new ArrayList<AuctionItem>(subset);
//				newSubset.add(i);
//				newPs.add(newSubset);
//			}
//
//			// powerset is now powerset of list.subList(0, list.indexOf(item)+1)
//			ps = newPs;
//		}
//		ps.remove(0); // remove the emptyset
//		return ps;
//	}

	private double calculateSurplus(List<AuctionItem> valuationSet,
			List<AuctionItem> itemSet, double increment) {

		double valuation = valuations.get(valuationSet);
		double perceivedPriceTotal = 0;

		for (AuctionItem valuationItem : valuationSet) {
			for (AuctionItem item : itemSet) {
				if (item.getID() == valuationItem.getID()) {
					if (this.getID() == item.getOwner().getID()) {
						perceivedPriceTotal += sunkAwarenessConstant
								* item.getPrice();
					} else {
						perceivedPriceTotal += item.getPrice() + increment;
					}
				}
			}
		}

		// some debugging statements
		System.out.print("{");
		for (AuctionItem i : valuationSet) {
			System.out.print(i.getName());
		}
		System.out.print("}");
		System.out.println("Valuation: " + valuation + " perceviedTotal: "
				+ perceivedPriceTotal + " surplus: "
				+ (valuation - perceivedPriceTotal));

		return valuation - perceivedPriceTotal;
	}

	protected Map<AuctionItem, Double> getNextRoundBehaviour(AuctionContext ac) {

		Map<AuctionItem, Double> nextRoundBehaviour = new HashMap<AuctionItem, Double>();

		List<List<AuctionItem>> optimalSetsToBidOn = new ArrayList<List<AuctionItem>>();
		List<AuctionItem> finalSetToBidOn;

		double maxSurplus = 0;
		double currentSurplus;

		// calculate surpluses for all combinations of items.
		for (List<AuctionItem> valuationSet : valuations.keySet()) {
			currentSurplus = calculateSurplus(valuationSet, ac.getItemList(),
					ac.getMinIncrement());
			if (currentSurplus > maxSurplus) {
				optimalSetsToBidOn.clear();
				optimalSetsToBidOn
						.add(new ArrayList<AuctionItem>(valuationSet));
				maxSurplus = currentSurplus;
			} else if (currentSurplus == maxSurplus) {
				optimalSetsToBidOn
						.add(new ArrayList<AuctionItem>(valuationSet));
				maxSurplus = currentSurplus;
			}
		}
		// from the combinations of items with the highest surplus -
		// find the greatest sized set to be the set to bid on
		// eg. {1, 2} surplus = 30. {1, 2, 3} surplus = 30 too. Pick the larger
		// set.
		int greatestSize = -1;
		int indexOfLargestSet = 0;
		for (int i = 0; i < optimalSetsToBidOn.size(); i++) {
			if (optimalSetsToBidOn.get(i).size() > greatestSize) {
				greatestSize = optimalSetsToBidOn.get(i).size();
				indexOfLargestSet = i;
			}
		}

		if (optimalSetsToBidOn.isEmpty()) {
			for (AuctionItem item : ac.getItemList()) {
				nextRoundBehaviour.put(new AuctionItem(item), 0.0);
			}
			return nextRoundBehaviour;
		} else {

			finalSetToBidOn = optimalSetsToBidOn.get(indexOfLargestSet);

			for (AuctionItem item : ac.getItemList()) {
				if (finalSetToBidOn.contains(item)) {
					if (this.getID() != item.getOwner().getID()) {
						// losing bid on desired item. Outbid it.
						nextRoundBehaviour.put(new AuctionItem(item),
								item.getPrice() + ac.getMinIncrement());
					} else {
						// agent is winning the bid for the item - does not need
						// to
						// bid again
						nextRoundBehaviour.put(new AuctionItem(item), 0.0);
					}
				} else {
					nextRoundBehaviour.put(new AuctionItem(item), 0.0);
				}
			}

			return nextRoundBehaviour;

		}
	}

}
