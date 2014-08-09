package dataRepresentation;

import java.util.ArrayList;
import java.util.Timer;


public class Auctioneer extends Thread{
	
	/*
	 * Auctioneer is start by GUI Bidder list Start Button
	 * The process of each round:
	 * 1> Generate all agents' bids, by invoking agent's auctionResponse(), store in request list
	 * 2> Wait until round duration time up.
	 * 3> Collect next round variable from GUI if any
	 * 4> update information for next round
	 * 5> set flag, bidServlet checks this flag, send response.
	 */
	
	ArrayList<Bid> requestedBids;
	AuctionEnvironment environment;
	ArrayList<AuctionContext> auctionLog = new ArrayList<AuctionContext>();
	Timer roundTimer = new Timer();
	
	public volatile boolean nextRoundNotReady = true;
	
	public Auctioneer(AuctionEnvironment e) {
		this.environment = e;
		this.requestedBids = new ArrayList<Bid>();
	}
	
	public void getBid(Bid bid) {
		synchronized(this.requestedBids) {
			this.requestedBids.add(bid);
			System.err.println("get a request current size:"+requestedBids.size()+"bidder list size:"+this.environment.bidderList.getList().size());
		}
		
	}
	
	@Override
	public void run() {
		while (true) {
			/**************deliberate delay**************/
			try {
				Thread.currentThread();
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/**************deliberate delay**************/
			
			//this.nextRoundNotReady = true;
//			System.err.println("collecting Agents' bids");
			//Collect Agent's bid
			for (Bidder bidder: this.environment.bidderList.getList()) {
				if (bidder instanceof Agent) {
					requestedBids.add(((Agent)bidder).auctionResponse(this.environment.context));
				}
			}
			
			while(this.environment.context.roundTimeElapse > 0) {
				// Wait until current round time up, or all bidder send their bid
				try {
					Thread.currentThread();
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				synchronized(this.requestedBids) {
					if (this.requestedBids.size() == this.environment.bidderList.getList().size()) {
						break;
					}
				}
			}
			
			System.err.println("Processing Bids...");
			processBids();
			System.err.println("next round starting...");
			updateNextRoundContext();
			/**************deliberate delay**************/
			try {
				Thread.currentThread();
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			/**************deliberate delay**************/
		}
	}
	
	
	public void setNextRoundReady() {
		this.nextRoundNotReady = false;
	}
	
	public AuctionContext nextRound() {
		
		return this.environment.context;
	}
	
	private void processBids() {
		
		//Xing at 2014.7.31: Process current bids, update auction context;
		//Xing change at 2014.8.2: Process all bids at once.
		
		System.err.println("request bid num:"+this.requestedBids.size());
		for (Bid bid: this.requestedBids) {
			for (AuctionItem bidderItem: bid.getItemList()) {
				double originalPrice = fetchItemPrice(bidderItem.getID());
//				System.out.println("original price:"+originalPrice+"for bidder"+bid.getBidder().getName());
				if (originalPrice < bidderItem.getPrice()) {
					putItemPrice(bid.getBidder(), bidderItem.getID(), bidderItem.getPrice());
				}
			}
		}
		synchronized(this.requestedBids) {
			requestedBids.clear();
		}
		//record current round log 
		this.recordLog();

	}
	
	private void updateNextRoundContext() {
		this.environment.context.bidsProcessingFinished = true;
		
		while (false == this.environment.context.bidsProcessingFinished); //wait till GUI update table
		this.environment.context.incrementRound();
		this.environment.context.roundTimeElapse = this.environment.context.getDurationTime();
		this.setNextRoundReady();
	}
	private void recordLog() {
		
		this.auctionLog.add(new AuctionContext(this.environment.context));
	}
	
	public ArrayList<AuctionContext> getLog() {
		return this.auctionLog;
	}
	
	private double fetchItemPrice(int itemID) {
		for (AuctionItem item: this.environment.context.getItemList()) {
			if (itemID == item.getID()) {
				return item.getPrice();
			}
		}
		return Double.MAX_VALUE;
	}
	
	private void putItemPrice(Bidder bidder, int itemID, double price) {
		for (AuctionItem item: this.environment.context.getItemList()) {
			if (itemID == item.getID()) {
				item.setPrice(price);
				item.setOwner(bidder);
			}
		}
	}
	

}
