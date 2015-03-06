package dataRepresentation;

import java.util.List;
import java.util.Map;

public abstract class Agent extends Bidder{
	
	public Agent(String name, String ip) {
		super(name, ip);
	}
	
	public abstract Bid auctionResponse(AuctionContext ac);
	
	public abstract Map<List<AuctionItem>, Double> getValuations();

}
