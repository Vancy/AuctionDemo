package auctionEngine;


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

//		 if (row >=1 && col >=1) {
//	    	 int itemID = col - 1;
//	    	 int ownerID = -1;
//	    	 
//	    	 for(AuctionItem item: context.getItemList()) {
//	    		if (item.getID() == itemID) {
//	    			ownerID = item.getOwner().getID();
//	    		}
//	    	 }
//	    	 setText(value.toString());    
//	    	 setBackground(BidderList.colorList.get(ownerID));
//	    	  
//		 }
		 setBackground(BidderList.colorList.get(0));
         return this;
    }
}
