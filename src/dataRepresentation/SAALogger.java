package dataRepresentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class SAALogger {
	
	public static String directory = "./AuctionResults.xls"; 
	
	ArrayList<Collection<Bid>> excelLog;

	public SAALogger() {
		excelLog = new ArrayList<Collection<Bid>>();
	}
	
	public void addToExcelLog(Collection<Bid> bids) {
		excelLog.add(new ArrayList<Bid>(bids));
	}
	
	public void createExcelLogSheet(ArrayList<AuctionItem> items, ArrayList<Bidder> bidders, String excelSheetName) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(excelSheetName);
		
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
		for (Collection<Bid> bids : excelLog) {
			entry = new Object[numberOfItems * numberOfBidders];
			int currentBidder = 0;
			for (Bid bid : bids) {
				int pos = currentBidder;
				List<AuctionItem> itemz = bid.getItemList();
				Collections.sort(itemz);
				for (AuctionItem ai : itemz) {
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
		            new FileOutputStream(new File(directory));
		    workbook.write(out);
		    out.close();
		    System.out.println("Excel file written successfully!");
		     
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
}
