package dataRepresentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Agent extends Bidder {
    
    private double sunkAwarenessConstant = 1;
    
    private List<Bid> memory = new ArrayList<Bid>();
    List<AuctionItem> items;
    
    Map<List<AuctionItem>, Double> valuations;
    
    /**
     * 
     * @param name
     * @param ip
     * @param items A list of items in the auction
     * @param valuations A map. List<AuctionItem> is mapped to Double representing the valuation.
     *                          You can use the getPowerSet method defined in this Agent.java class
     *                          to generate a power set from a list. For example, inputting a list {1, 2, 3}
     *                          will give you { {1}, {2}, {3}, {1, 2}, {1, 3}, {2, 3}, {1, 2, 3} }
     * @param k sunk-awareness constant
     */
    public Agent(String name, String ip, List<AuctionItem> items, Map<List<AuctionItem>, Double> valuations, double k) {
        super(name, ip);
        this.items = items;
        setSunkAwarenessConstant(k);
        this.valuations = new HashMap<List<AuctionItem>, Double>(valuations);
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
        	//Xing add this line at 2014.8.11, put bid price into AuctionItem
        	i.setPrice(behaviour.get(i)); //xing added
            bidderItemList.add(new AuctionItem(i));
        }
        
        Bid bid = new Bid(this, bidderItemList);
        memory.add(bid);
        return bid;
    }
    
    private List<List<AuctionItem>> getPowerSet(List<AuctionItem> itemList) {
        List<List<AuctionItem>> ps = new ArrayList<List<AuctionItem>>();
        ps.add(new ArrayList<AuctionItem>()); // add the empty set
        
        // for every item in the original list
        for (AuctionItem i : itemList) {
            List<List<AuctionItem>> newPs = new ArrayList<List<AuctionItem>>();
            
            for (List<AuctionItem> subset : ps) {
                // copy all of the current powerset's subsets
                newPs.add(subset);
                
                // plus the subsets appended with the current item
                List<AuctionItem> newSubset = new ArrayList<AuctionItem>(subset);
                newSubset.add(i);
                newPs.add(newSubset);
            }
            
            // powerset is now powerset of list.subList(0, list.indexOf(item)+1)
            ps = newPs;
        }
        ps.remove(0); // remove the emptyset
        return ps;
    }
    
    /*private void assignValuations(List<Double> singleItemValuations) {
        // this for loop assigns valuations to every single item
        for (int i = 0; i < singleItemValuations.size(); i++) {
            List<AuctionItem> singleItemPack = new ArrayList<AuctionItem>();
            singleItemPack.add(items.get(i));
            valuations.put(singleItemPack, singleItemValuations.get(i));
        }
        // this for loop assigns valuations to every combination of items
        // by summing the individual valuations
        for (List<AuctionItem> itemPack : powerSet) {
            if (itemPack.size() == 0) {
                continue;
            }
            double currentValuation = 0;
            for (AuctionItem item : itemPack) {
            	List<AuctionItem> i = new ArrayList<AuctionItem>();
                i.add(item);
                currentValuation += valuations.get(i);
            }
            valuations.put(itemPack, currentValuation);
        }
    }*/
    
    private double calculateSurplus(List<AuctionItem> itemSet, double increment) {
        
        double valuation = valuations.get(itemSet);
        double perceivedPriceTotal = 0;
        
        for (AuctionItem item : itemSet) {
            if (this.getID() == item.getOwner().getID()) {
                perceivedPriceTotal += sunkAwarenessConstant * item.getPrice();
            } else {
                perceivedPriceTotal += item.getPrice() + increment;
            }
        } 
        
        // some debugging statements
        System.out.print("{");
        for (AuctionItem i : itemSet) {
            System.out.print(i.getID());
        }
        System.out.print("}");
        System.out.println("Valuation: " + valuation + " perceviedTotal: " + perceivedPriceTotal + " surplus: " + (valuation - perceivedPriceTotal));
        
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
    /*protected Map<Integer, Double> getFirstRoundBehaviour() {
        
        Map<Integer, Double> nextRoundBehaviour = new HashMap<Integer, Double>();
        
        for (int i = 0; i < numberOfItems; i++) {
            List<Integer> vAuctionItem = new ArrayList<Integer>();
            vAuctionItem.add(i);
            nextRoundBehaviour.put(i, (double)valuations.get(vAuctionItem));
        }
        
        return nextRoundBehaviour;
    }*/
    
    protected Map<AuctionItem, Double> getNextRoundBehaviour(AuctionContext ac) {
        
        Map<AuctionItem, Double> nextRoundBehaviour = new HashMap<AuctionItem, Double>();
        
        List<List<AuctionItem>> optimalSetsToBidOn = new ArrayList<List<AuctionItem>>();
        List<AuctionItem> finalSetToBidOn;
        
        double maxSurplus = Double.NEGATIVE_INFINITY;
        
        // calculate surpluses for all combinations of items.
        for (List<AuctionItem> set : valuations.keySet()) {
            double currentSurplus = calculateSurplus(set, ac.getMinIncrement());
            if (currentSurplus >= maxSurplus) {
                optimalSetsToBidOn.add(new ArrayList<AuctionItem>(set));
                maxSurplus = currentSurplus;
            }
        }
        // from the combinations of items with the highest surplus -
        // find the greatest sized set to be the set to bid on
        // eg. {1, 2} surplus = 30. {1, 2, 3} surplus = 30 too. Pick the larger set.
        int greatestSize = -1;
        int indexOfLargestSet = 0;
        for (int i = 0; i < optimalSetsToBidOn.size(); i++) {
            if (optimalSetsToBidOn.get(i).size() > greatestSize) {
                greatestSize = optimalSetsToBidOn.get(i).size();
                indexOfLargestSet = i;
            }
        }
        finalSetToBidOn = optimalSetsToBidOn.get(indexOfLargestSet);

        for (AuctionItem item : ac.getItemList()) {
            if (finalSetToBidOn.contains(item)) {
                if (this.getID() != item.getOwner().getID()) {
                    // losing bid on desired item. Outbid it.
                    nextRoundBehaviour.put(new AuctionItem(item), item.getPrice() + ac.getMinIncrement());
                } else {
                    // agent is winning the bid for the item - does not need to bid again
                    nextRoundBehaviour.put(new AuctionItem(item), 0.0);
                }
            } else {
                nextRoundBehaviour.put(new AuctionItem(item), 0.0);
            }
        }

        return nextRoundBehaviour;
    }
    
}
