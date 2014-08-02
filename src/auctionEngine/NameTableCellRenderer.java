package auctionEngine;


import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import dataRepresentation.BidderList;

public class NameTableCellRenderer extends DefaultTableCellRenderer {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3302061774697795886L;

	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
    	 
		 setText(value.toString());    
    	 setBackground(BidderList.colorList.get(row));
         return this;
    }
}
