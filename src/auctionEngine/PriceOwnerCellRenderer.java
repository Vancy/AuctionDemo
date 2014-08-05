package auctionEngine;


import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import dataRepresentation.AuctionContext;
import dataRepresentation.AuctionEnvironment;
import dataRepresentation.AuctionItem;
import dataRepresentation.BidderList;

public class PriceOwnerCellRenderer extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	private AuctionEnvironment environment;
	
	public PriceOwnerCellRenderer(AuctionEnvironment e) {
		this.environment = e;
	}
	
	private static final long serialVersionUID = 1999946219503976635L;

	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
		
		setBackground(Color.WHITE); //firstly set back to original color
		 if (row >0 && col >0) {

	 		int round = row;
	    	int itemID = col - 1;
	    	int ownerID = -1;
	    	for (AuctionContext context: this.environment.auctioneer.getLog()) {
	    		if (context.getRound() == row) {
	    			for(AuctionItem item: context.getItemList()) {
		    			if (item.getID() == itemID) {
		    				if (null != item.getOwner()) {
		    					ownerID = item.getOwner().getID();
		    				}	
		 	    		}
		 	    	 }  
	    		}
	    	}
	    	
	    	if (-1 != ownerID){
	    		setBackground(BidderList.colorList.get(ownerID));  
	    	} 
		 }
		 setText(value.toString());   
		 return this;
    }
}
