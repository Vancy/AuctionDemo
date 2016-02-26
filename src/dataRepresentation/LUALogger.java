package dataRepresentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Row;

public class LUALogger {
	
	private static String targetFile = "./LuaAuctionResults.xls"; 

	private ConcurrentHashMap<Integer, ArrayList<LuaBid>> luaBids;
	private AuctionEnvironment environment;
	private ArrayList<AuctionItem> items;
	
	public LUALogger(ConcurrentHashMap<Integer, ArrayList<LuaBid>> luaBids, AuctionEnvironment environment) {
		this.luaBids = luaBids;
		this.environment = environment;
		this.items = this.environment.context.getItemList();
	}

	
	public void printResults() {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("LUA auction results");
		
		createRawLuaBidResults(sheet);
		createWinnerResults(sheet);
		
		close(workbook);
	}

	private void createRawLuaBidResults(HSSFSheet sheet) {
		
		printRawResultsHeader(sheet, environment.context.getItemList());
		
		int contentRow = 2;
		for (int bidderID: luaBids.keySet()) {
			Row thisRow = sheet.createRow(contentRow);
			String bidderName = environment.bidderList.getBidderName(bidderID);
			thisRow.createCell(0).setCellValue(bidderID);
			thisRow.createCell(1).setCellValue(bidderName);
			int priceCursor = 2;
			for (LuaBid bid: luaBids.get(bidderID)) {
				double licenced = bid.getLicencedBidPrice();
				double unlicenced = bid.getUnlicencedBidPrice();
				thisRow.createCell(priceCursor++).setCellValue(licenced);
				thisRow.createCell(priceCursor++).setCellValue(unlicenced);
			}
			contentRow++;
		}

	}
	
	
	private void createWinnerResults(HSSFSheet sheet) {
		printWinnerResultsHeader(sheet);
		HashMap<Integer, LUAItemWinningResult> winnerResults = processWinners();
		int firstAvailableRow = sheet.getLastRowNum() + 1;
		for(int itemId: winnerResults.keySet()) {
			LUAItemWinningResult itemWinResult = winnerResults.get(itemId);
			Row headerRow = sheet.createRow(firstAvailableRow);
			headerRow.createCell(0).setCellValue(itemWinResult.getItemName());
			headerRow.createCell(1).setCellValue(itemWinResult.getWinnerLprice());
			headerRow.createCell(2).setCellValue(itemWinResult.getUnlicencedPriceSum());
			headerRow.createCell(3).setCellValue(itemWinResult.getWinnerType()==LUAItemWinningResult.WinnerType.LicencedWin? "L":"U");
			headerRow.createCell(4).setCellValue(itemWinResult.getSecondHighestPrice());
			headerRow.createCell(5).setCellValue(itemWinResult.getWinnerDistributionResult());
			firstAvailableRow++;
		}
	}
	
	private void printWinnerResultsHeader(HSSFSheet sheet) {
		int firstAvailableRow = sheet.getLastRowNum();
		Row headerRow = sheet.createRow(firstAvailableRow);
		headerRow.createCell(0).setCellValue("Item");
		headerRow.createCell(1).setCellValue("Winner L");
		headerRow.createCell(2).setCellValue("Sum U");
		headerRow.createCell(3).setCellValue("Winner type");
		headerRow.createCell(4).setCellValue("Second highest");
		headerRow.createCell(5).setCellValue("Winners");
	}
	
	
	private void printRawResultsHeader(HSSFSheet sheet, ArrayList<AuctionItem> items) {
	    Row headerRow = sheet.createRow(0);
	    Row subheaderRow = sheet.createRow(1);
	    
	    sheet.addMergedRegion(new CellRangeAddress(0,0,0,1));
	    headerRow.createCell(0).setCellValue("Bidder");
	    subheaderRow.createCell(0).setCellValue("ID");
	    subheaderRow.createCell(1).setCellValue("name");
	    
		Collections.sort(items);

	    for(AuctionItem item: items) {
	    	int id = item.getID();
			int itemCursor = id+1;
	    	headerRow.createCell(itemCursor*2).setCellValue(item.getName());
		    subheaderRow.createCell(itemCursor*2).setCellValue("L");
		    subheaderRow.createCell(itemCursor*2+1).setCellValue("U");
	    }
	}
	
	
	private HashMap<Integer, LUAItemWinningResult> processWinners() {
		HashMap<Integer, LUAItemWinningResult> results = new HashMap<Integer, LUAItemWinningResult>();
		for (int bidderID: luaBids.keySet()) {
			
			for (LuaBid bid: luaBids.get(bidderID)) {
				int itemID = bid.ItemID();
				LUAItemWinningResult itemResult;
				if (!results.containsKey(itemID)) {
					itemResult = results.put(itemID, new LUAItemWinningResult(itemID, getItemNameByID(itemID)));
				} else {
					itemResult = results.get(itemID);		
				}
				itemResult.updateBidInfo(bidderID, bid);
			}
		}
		return results;
	}
	
	private String getItemNameByID(int id) {
		for(AuctionItem item: this.items) {
			if(item.getID() == id) {
				return item.getName();
			} else {
				continue;
			}
		}
		throw new RuntimeException("Cannot find item id in current item list:"+id);
	}
	
	private void close(HSSFWorkbook workbook) {
		try {
		    FileOutputStream out = new FileOutputStream(new File(targetFile));
		    workbook.write(out);
		    out.close();  
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		System.out.println("Excel file written successfully!");   
	}
}
