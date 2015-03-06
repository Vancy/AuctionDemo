package dataRepresentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CCAAgent extends Agent{
	
	public CCAAgent(String name, String ip) {
		super(name, ip);
	}

	/*This is demand vector which defines agent's demand of the number
	 *of item, based on the price change. 
	 e.g. demand vector <20, 18, 17, 15, 10, 5, 3, 2, 0> shows:
     bidder will bid 0 demand when the price is higher than $20,
     when the price is between $18-$20, the bidder will demand 1 item.
     when the price is between $17-$18, the bidder will demand 2 item.
     ...
     when the price is between $0-$2, the bidder will demand 7 item.
     when the price is $0, the bidder will demand 8 item.
	 */
	private HashMap<Integer, ArrayList<Double>> demandVectors;

	@Override
	public Bid auctionResponse(AuctionContext ac) {
		// TODO Auto-generated method stub
		return null;
	}


	public CCAAgent(String name, String ip, List<AuctionItem> items,
			Map<List<AuctionItem>, Double> valuations, HashMap<Integer, ArrayList<Double>> vector) {
		//super(name, ip, items, valuations, 0);
		super(name, ip);
		this.demandVectors = vector;
	}
/*	
	private double calculateSurplus(List<AuctionItem> valuationSet, List<AuctionItem> itemSet) {

		//get current package valuation
		double valuation = valuations.get(valuationSet);
		double actualPriceTotal = 0;

		for (AuctionItem valuationItem : valuationSet) {
			for (AuctionItem item : itemSet) {
				if (item.getID() == valuationItem.getID()) {
					int thisRoundDemand = calculateDemand(item);
					actualPriceTotal += item.getPrice() * thisRoundDemand;
				}
			}
		}

		// some debugging statements
		System.out.print("{");
		for (AuctionItem i : valuationSet) {
			System.out.print(i.getName());
		}
		System.out.print("}");
		System.out.println("Valuation: " + valuation + " actualPrice: "
				+ actualPriceTotal + " surplus: "
				+ (valuation - actualPriceTotal));

		return valuation - actualPriceTotal;
	}
	
	private int calculateDemand(AuctionItem  item) {
		int thisRoundDemand = 0;
		double currentPrice = item.getPrice();
		ArrayList<Double> demandVector = this.demandVectors.get(item.getID());
		int i;
		for (i=demandVector.size()-1; i>=0; i--) {
			if (Math.abs(currentPrice - demandVector.get(i)) < 0.00001) {
				//if currentPrice equals to vector position value
				break;
			}
			if (currentPrice < demandVector.get(i)) {
				//if currentPrice is smaller than vector position value
				// demand roll back one 
				i++;
				break;
			}
		}
		thisRoundDemand = i;
		return thisRoundDemand;
	}
	
	public Bid auctionResponse(AuctionContext ac) {

		List<AuctionItem> bidderItemList = new ArrayList<AuctionItem>();
		Map<AuctionItem, Integer> behaviour = getNextRoundCCABehaviour(ac);

		for (AuctionItem i : behaviour.keySet()) {
			i.setRequiredQuantity(behaviour.get(i)); 
			bidderItemList.add(new AuctionItem(i));
		}

		Bid bid = new Bid(this, bidderItemList);
		memory.add(bid);
		return bid;
	}
	
	protected Map<AuctionItem, Integer> getNextRoundCCABehaviour(AuctionContext ac) {
		
		Map<AuctionItem, Integer> nextRoundBehaviour = new HashMap<AuctionItem, Integer>();

		List<List<AuctionItem>> optimalSetsToBidOn = new ArrayList<List<AuctionItem>>();
		List<AuctionItem> finalSetToBidOn;
		
		double currentSurplus;
		double maxSurplus = 0;
		
		for (List<AuctionItem> valuationSet : valuations.keySet()) {
			currentSurplus = calculateSurplus(valuationSet, ac.getItemList());
			if (currentSurplus > maxSurplus) {
				optimalSetsToBidOn.clear();
				optimalSetsToBidOn.add(new ArrayList<AuctionItem>(valuationSet));
				maxSurplus = currentSurplus;
			} else if (currentSurplus == maxSurplus) {
				optimalSetsToBidOn.add(new ArrayList<AuctionItem>(valuationSet));
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
				nextRoundBehaviour.put(new AuctionItem(item), 0);
			}
			//if there is no optimal bid, just bid 0 for every item
			return nextRoundBehaviour;
		} else {
			finalSetToBidOn = optimalSetsToBidOn.get(indexOfLargestSet);

			for (AuctionItem item : ac.getItemList()) {
				if (finalSetToBidOn.contains(item)) {
					if (this.getID() != item.getOwner().getID()) {
						nextRoundBehaviour.put(new AuctionItem(item), calculateDemand(item));
					} else {
						nextRoundBehaviour.put(new AuctionItem(item), 0);
					}
				} else {
					nextRoundBehaviour.put(new AuctionItem(item), 0);
				}
			}
			return nextRoundBehaviour;
		}
	}
	
*/


	@Override
	public Map<List<AuctionItem>, Double> getValuations() {
		// TODO Auto-generated method stub
		return null;
	}

}
