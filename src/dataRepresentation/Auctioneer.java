package dataRepresentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;


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
	
	HashMap<Integer, Bid> requestedBids;
	AuctionEnvironment environment;
	ArrayList<AuctionContext> auctionLog = new ArrayList<AuctionContext>();
	ArrayList<Collection<Bid>> excelLog = new ArrayList<Collection<Bid>>();
	Timer roundTimer = new Timer();
	
	public volatile boolean nextRoundNotReady = true;
	
	public Auctioneer(AuctionEnvironment e) {
		this.environment = e;
		this.requestedBids = new HashMap<Integer, Bid>();
	}
	
	public void getBid(Bid bid) {
		synchronized(this.requestedBids) {
			int bidderID = bid.getBidder().getID();
			String bidderName = bid.getBidder().getName();
			this.requestedBids.put(bidderID, bid);
			System.err.println("get bidder " + bidderName + "'s new bid");
		}
		
	}
	
	private boolean removeBids() {
			this.requestedBids.clear();
			System.out.println("current request list size after remove all:"+requestedBids.size());
			return true;
	}
	
	@Override
	public void run() {
		while (true) {
			/**************Next Round Start**************/
			System.err.println("******Round " + this.environment.context.getRound() + " Start*****Min increment " + this.environment.context.getMinIncrement() + "*****");
			
			//Collect Agent's bid
			collectAgentBid();
			
			while(this.environment.context.roundTimeElapse > 0) {
				// Wait until current round time up
				deliberateDelay(0.2);
			}

			// Wait one more seconds, to wait all defaults bids
			deliberateDelay(1);
			
			System.out.println("Processing Bids...");
			processBids();
			System.out.println("next round starting...");
			removeBids();
			deliberateDelay(1);
			updateNextRoundContext();
			/***************Next Round Will Start**********************/
			if (this.environment.context.isFinalRound()) {
				System.err.println("Auction End");
				createExcelLogSheet();
				break; // break from while loop, terminate auctioneer
			}
		}
	}
	
	private void createExcelLogSheet() {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Sample sheet");
		
		ArrayList<AuctionItem> items = this.environment.context.getItemList();
		ArrayList<Bidder> bidders = this.environment.bidderList.getList();
		
		Collections.sort(items);
		
		int numberOfBidders = bidders.size();
		int numberOfItems = items.size();
		
		Map<Integer, Object[]> data = new TreeMap<Integer, Object[]>();
		Object[] itemRowHeader = new Object[numberOfItems * numberOfBidders];
		Object[] bidderRowHeader = new Object[numberOfItems * numberOfBidders];
		
		itemRowHeader[0] = "";
		bidderRowHeader[0] = "";
		
		int counter = 0;
		for (int i = 0; i < numberOfItems * numberOfBidders; i++) {
			if (i == 0) {
				itemRowHeader[i] = items.get(counter).getName();
				counter++;
				continue;
			}
			if (i % numberOfBidders == 0) { 
				itemRowHeader[i] = items.get(counter).getName();
				counter++;
			} else {
				itemRowHeader[i] = "";
			}
		}
		
		counter = 0;
		for (int i = 0; i < numberOfItems; i++) {
			for (int j = 0; j < numberOfBidders; j++) {
				bidderRowHeader[counter] = bidders.get(j).getName();
				counter++;
			}
		}
		
		data.put(1, itemRowHeader);
		data.put(2, bidderRowHeader);
		
		counter = 3;
		Object[] entry = new Object[numberOfItems * numberOfBidders];
		for (Collection<Bid> bids : getExcelLog()) {
			entry = new Object[numberOfItems * numberOfBidders];
			int currentBidder = 0;
			for (Bid bid : bids) {
				int pos = currentBidder;
				for (AuctionItem ai : bid.getItemList()) {
					entry[pos] = ai.getPrice();
					pos += numberOfBidders;
				}
				currentBidder++;
			}
			data.put(counter, entry);
			counter++;
		}
		
		Set<Integer> keyset = data.keySet();
		int rownum = 0;
		int roundNumber = 1;
		for (Integer key : keyset) {
		    Row row = sheet.createRow(rownum);
		    // create the round numbers column
		    if (rownum == 1) {
		    	row.createCell(0).setCellValue("Round");
		    }
		    if (rownum > 1) {
		    	row.createCell(0).setCellValue(roundNumber);
		    	roundNumber++;
		    }
		    
		    Object [] objArr = data.get(key);
		    int cellnum = 1;
		    for (Object obj : objArr) {
		        Cell cell = row.createCell(cellnum);
		        if(obj instanceof String) {
		            cell.setCellValue((String)obj);
		    	} else if(obj instanceof Double) {
		        	if (((Double) obj) == 0d) {
		        		if (sheet.getRow(rownum-1).getCell(cellnum).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
		        			cell.setCellValue(sheet.getRow(rownum-1).getCell(cellnum).getNumericCellValue());
		        		} else {
		        			cell.setCellValue((Double)obj);
		        		}
		        	} else {
		        		cell.setCellValue((Double)obj);
		        	}
		        }
		        cellnum++;
		    }
		    rownum++;
		}
		 
		try {
		    FileOutputStream out = 
		            new FileOutputStream(new File("C:\\Users\\oodi687\\workspace\\AuctionResults.xls"));
		    workbook.write(out);
		    out.close();
		    System.out.println("Excel written successfully!");
		     
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	private void setNextRoundReady() {
		this.nextRoundNotReady = false;
	}
	
	private void setNextRoundNotReady() {
		this.nextRoundNotReady = true;
	}
	
	public AuctionContext nextRound() {
		//this method is invoked by BidServlet, to get the latest auctionContext
		return this.environment.context;
	}
	
	private void processBids() {
		
		//Xing at 2014.7.31: Process current bids, update auction context;
		//Xing change at 2014.8.2: Process all bids at once.
		if  (this.environment.context.getType() == AuctionContext.AuctionType.SAA) {
			processSAABids();
		} else if (this.environment.context.getType() == AuctionContext.AuctionType.CCA) {
			processCCABids();
		}
		
		synchronized(this.requestedBids) {
			requestedBids.clear();
		}
		//record current round log 
		this.recordLog();

	}
	
	private void processSAABids() {
		/*
		 * newBids is true when there is at least one new bid, then auction will keep going,
		 * otherwise, newBids is false, means nobody bids, then next round is the final. 
		 */
		boolean newBids = false;
		HashMap<Bidder, ArrayList<AuctionItem>> bidderPrices = new HashMap <Bidder, ArrayList<AuctionItem>>();
		HashMap<Integer, Double> originalPrices = new HashMap <Integer, Double>();
		for (Bid bid: this.requestedBids.values()) {
			Bidder currBidder = bid.getBidder();
			int numberOfItemsBidOn = 0;
			int numberOfItemsLeading = 0;
			for (AuctionItem bidderItem: bid.getItemList()) {
				
				double originalPrice = fetchItemPrice(bidderItem.getID());
				originalPrices.put(bidderItem.getID(), fetchItemPrice(bidderItem.getID()));
				if (currBidder.getID() == fetchItemOwnerID(bidderItem.getID())) {
					numberOfItemsLeading++;
				} 
				if (bidderItem.getPrice() > originalPrice) {
					ArrayList<AuctionItem> items;
					if (bidderPrices.get(currBidder) == null) {
						items = new ArrayList<AuctionItem>();
					} else {
						items = bidderPrices.get(currBidder);
					}
					items.add(bidderItem);
					bidderPrices.put(currBidder, items);
					if (!newBids) {
						newBids = true;
					}
					numberOfItemsBidOn++;
				} else {
					// bidder aborted bid for this item
				}
			}
			if (this.environment.context.getRound() == 1) {
				currBidder.setActivity(numberOfItemsBidOn);
				currBidder.setEligibility(numberOfItemsBidOn);
			} else {
				// set eligibility to previous activity and calculate current activity
				// this will determine whether the bidder has become inactive
				currBidder.setEligibility(bid.getBidder().getActivity());
				currBidder.setActivity(numberOfItemsBidOn + numberOfItemsLeading);
				if (currBidder.getActivity() > currBidder.getEligibility()) {
					currBidder.decrementActivityCounter();
					if (currBidder.getActivityCounter() == 0) {
						// kick currBidder
					}
				}
			}
		}
		
		// apply the item updates
		for (Bidder bidder : bidderPrices.keySet()) {
			for (AuctionItem item : bidderPrices.get(bidder)) {
				double bidderPrice = item.getPrice();
				if (bidderPrice > fetchItemPrice(item.getID())) {
					putItemPrice(bidder, item.getID(), bidderPrice);				
				} else if (Math.abs(bidderPrice - fetchItemPrice(item.getID())) <= 0.001 && flipCoinWin()) {
					putItemPrice(bidder, item.getID(), bidderPrice);
				}
			}
		}
		
		if (!newBids) {
			this.environment.context.setFinalRound();
		}
		
		recordExcelLog(this.requestedBids.values()); 
	}
	
	private void processCCABids() {
		
		//firstly clear last round temporary owners
		for(AuctionItem item: this.environment.context.getItemList()) {
			if (!item.biddingFinised) {
				item.clearOwners();
			}
		}
		
		//create a temporary array to story the total number all bidders require each round
		int itemNumber = this.environment.context.getItemList().size();
		int[] thisRoundRequirment = new int[itemNumber];
		for (int i=0; i<itemNumber; i++) {
			thisRoundRequirment[i] = 0;
		}
		
		//collect requirment of each item
		for (Bid bid: this.requestedBids.values()) {
			for (AuctionItem bidderItem: bid.getItemList()) {
				thisRoundRequirment[bidderItem.getID()] += bidderItem.getRequiredQuantity();
				placeItemOwner(bidderItem.getID(), bid.getBidder().getName(), bidderItem.getRequiredQuantity());
				System.out.println("Bidder "+ bid.getBidder().getName()+ " wants itemID:" + bidderItem.getID() + " require " + bidderItem.getRequiredQuantity());
			}
		}
		for (AuctionItem item: this.environment.context.getItemList()) {
			item.setRequiredQuantity(thisRoundRequirment[item.getID()]);
			if (item.getRequiredQuantity() <= item.getQuantity()) {
				//set this item is finish bidding
				item.biddingFinised = true; 
			}
		}
		
	}
	
	private void updateNextRoundContext() {
		
		/*
		 * reset parameter via GUI, e.g. minimun_increment
		 */
		this.environment.context.bidsProcessingFinished = true; //set flag true: GUI can update info
		while (false == this.environment.context.bidsProcessingFinished); //wait till GUI finish update
		
		this.environment.context.incrementRound();
		this.environment.context.roundTimeElapse = this.environment.context.getDurationTime();
		
		//some update for CCA Auction
		if (this.environment.context.getType() == AuctionContext.AuctionType.CCA) 
			updateNextRoundPriceForCCA();
		
		//set flag true, bidservlet can send response
		this.setNextRoundReady();
		
		//make sure every one receive their responses
		while (this.requestedBids.size() > 0) {
			deliberateDelay(0.5);
			//waiting to confirm every html response sent
		}
		
		//set flag false, next round bidservlet wait till ready.
		this.requestedBids.clear();
		this.setNextRoundNotReady();
	}
	
	private void collectAgentBid() {
		for (Bidder bidder: this.environment.bidderList.getList()) {
			if (bidder instanceof CCAAgent) {
				Bid agentBid = ((Agent)bidder).auctionResponse(this.environment.context);
				requestedBids.put(agentBid.getBidder().getID(), agentBid);
				System.err.println("Agent:"+bidder.getName()+" place a bid:");
				for (AuctionItem item: agentBid.getItemList()) {
					System.out.println(bidder.getName()+" demands:"+item.getRequiredQuantity()+"for item:"+item.getName());
				}
			} else if (bidder instanceof Agent) {
				Bid agentBid = ((Agent)bidder).auctionResponse(this.environment.context);
				requestedBids.put(agentBid.getBidder().getID(), agentBid);
				System.err.println("Agent:"+bidder.getName()+" place a bid:");
				for (AuctionItem item: agentBid.getItemList()) {
					System.out.println(bidder.getName()+"'s price:"+item.getPrice()+"for item:"+item.getName());
				}
			} 	
		}
	}
	
	private void recordLog() {	
		this.auctionLog.add(new AuctionContext(this.environment.context));
	}
	
	public ArrayList<AuctionContext> getLog() {
		return this.auctionLog;
	}
	
	public void recordExcelLog(Collection<Bid> bids) {
		this.excelLog.add(new ArrayList<Bid>(bids));
	}

	public ArrayList<Collection<Bid>> getExcelLog() {
		return this.excelLog;
	}

	private void updateNextRoundPriceForCCA() {
		double priceTick = this.environment.context.getPriceTick() + this.environment.context.getMinIncrement();
		this.environment.context.setPriceTick(priceTick);
		ArrayList<AuctionItem> auctionItems = this.environment.context.getItemList();
		for (AuctionItem item: auctionItems) {
			if (!item.biddingFinised) {
				item.setPrice(priceTick);
			}
		}
	}
	/*
	 * three helper methods: fetchItemPrice, putItemPrice, placeItemOwner
	 */
	private double fetchItemPrice(int itemID) {
		for (AuctionItem item: this.environment.context.getItemList()) {
			if (itemID == item.getID()) {
				return item.getPrice();
			}
		}
		return Double.MAX_VALUE;
	}
	
	private int fetchItemOwnerID(int itemID) {
		for (AuctionItem item: this.environment.context.getItemList()) { 
			if (itemID == item.getID()) {
				System.err.println(item.getOwner().getName());
				return item.getOwner().getID();
			}
		}
		return -1;
	}
	
	private void putItemPrice(Bidder bidder, int itemID, double price) {
		for (AuctionItem item: this.environment.context.getItemList()) {
			if (itemID == item.getID()) {
				item.setPrice(price);
				item.setOwner(bidder);
			}
		}
	}
	
	private void placeItemOwner(int itemID, String name, int amount) {
		for (AuctionItem item: this.environment.context.getItemList()) {
			if (itemID == item.getID()) {
				item.placeOwner(name, amount);
			}
		}
	}
	
	/*
	 * Two utility funcitons: deliberateDelay, flipCoinWin
	 */
	static private void deliberateDelay(double sec) {
		try {
			Thread.currentThread();
			Thread.sleep((int)(sec * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	static private boolean flipCoinWin() {
		Random generator = new Random(System.currentTimeMillis());
		int coin = generator.nextInt(100);
		return coin > 50 ? true : false;
	}

}
