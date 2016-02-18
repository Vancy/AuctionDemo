package dataRepresentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Row;

public class LUALogger {
	
	public static String targetFile = "./LuaAuctionResults.xls"; 

	
	public void createRawLuaBidResults(ConcurrentHashMap<Integer, ArrayList<LuaBid>> luaBids, AuctionEnvironment environment) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("LUA auction results");
		
		printHeader(sheet, environment.context.getItemList());
		
		//int numberOfBidders = environment.bidderList.size();
		//int numberOfItems = environment.context.getItemList().size();
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
		close(workbook);
	}
	
	private void printHeader(HSSFSheet sheet, ArrayList<AuctionItem> items) {
	    Row headerRow = sheet.createRow(0);
	    Row subheaderRow = sheet.createRow(1);
	    
	    sheet.addMergedRegion(new CellRangeAddress(0,0,0,1));
	    headerRow.createCell(0).setCellValue("Bidders");
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
