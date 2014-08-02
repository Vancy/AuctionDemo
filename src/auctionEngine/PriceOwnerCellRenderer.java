package auctionEngine;


import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import dataRepresentation.AuctionContext;
import dataRepresentation.AuctionItem;
import dataRepresentation.BidderList;

public class PriceOwnerCellRenderer extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	AuctionContext context;
	
	public PriceOwnerCellRenderer(AuctionContext c) {
		this.context = c;
	}
	
	private static final long serialVersionUID = 1999946219503976635L;

	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
		
		setBackground(Color.WHITE); //firstly set back to original color
		 if (row >0 && col >0) {

	 		System.out.println("COL:"+col+"ROW:"+row);
	    	 int itemID = col - 1;
	    	 int ownerID = -1;
	    	 for(AuctionItem item: context.getItemList()) {
	    		if (item.getID() == itemID) {
	    			if (null != item.getOwner()) {
	    				ownerID = item.getOwner().getID();
	    			}
	    			
	    		}
	    	 }  
	    	 System.out.println("owner id:"+ownerID+"in item"+itemID);
	    	 if (-1 != ownerID){
	    		 setBackground(BidderList.colorList.get(ownerID));  
	    	 } 
		 }
		
		 setText(value.toString());   
		 return this;
    }
}
