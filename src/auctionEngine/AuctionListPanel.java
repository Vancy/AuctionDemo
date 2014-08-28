package auctionEngine;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import dataRepresentation.AuctionContext;
import dataRepresentation.AuctionEnvironment;
import dataRepresentation.AuctionItem;


public class AuctionListPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private AuctionEnvironment environment;
	private JTable table;
	private DefaultTableModel tableModel;

	/**
	 * Create the panel.
	 */
	public AuctionListPanel(AuctionEnvironment environment) {
		
		this.environment = environment;
		setLayout(new GridLayout(0, 1, 0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane);

		initTableModel(); //initialize table model to fill data
		
		table = new JTable(tableModel); //Buid up table based on table model
		table.setEnabled(false);
		table.setDefaultRenderer(Object.class, new PriceOwnerCellRenderer(this.environment));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		scrollPane.setViewportView(table);
		
	}
	
	protected void updateAuctionList() {
		switch (this.environment.context.getType()) {
		case SAA:
			updateSaaAuctionList();
			//keep table scroll down to the last row
			table.changeSelection(table.getRowCount() - 1, 0, false, false);
			break;
		case CCA:
			updateCcaAuctionList();
			//keep table scroll down to the last row
			table.changeSelection(table.getRowCount() - 1, 0, false, false);
			break;
		case ULA:
			updateUlaAuctionList();
			break;
		default:
			break;
		}
	}
	
	private void initTableModel() {
		switch (this.environment.context.getType()) {
		case SAA:
			initSaaTableModel();
			break;
		case CCA:
			initCcaTableModel();
			break;
		case ULA:
			initUlaTableModel();
			break;
		default:
			break;
		}
	}
	
	private void updateSaaAuctionList() {	
		if (this.environment.context.bidsProcessingFinished) {
			Vector<String> newRow = new Vector<String>(); 
			newRow.add(Integer.toString((this.environment.context.getRound()-1)));
			for (AuctionItem item: this.environment.context.getItemList()) {
				double price = item.getPrice();
				newRow.add(String.valueOf(price));
			}
			tableModel.addRow(newRow);
		}
	}
	
	private void updateCcaAuctionList() {
		if (this.environment.context.bidsProcessingFinished) {
			Vector<String> newRow = new Vector<String>(); 
			newRow.add(Double.toString(this.environment.context.getPriceTick()));
			for (AuctionItem item: this.environment.context.getItemList()) {
				int require = item.getRequiredQuantity();
//				if (item.biddingFinised) {
//					newRow.add("Finish"); //if finished, won't print the number
//				} else {
					newRow.add(String.valueOf(require));
//				}
				
			}
			tableModel.addRow(newRow);
		}
	}
	
	private void updateUlaAuctionList() {	
	}
	
	private void initSaaTableModel() {
		tableModel  = new DefaultTableModel();

		tableModel.addColumn("Round");
	
		for (AuctionItem item: this.environment.context.getItemList()) {
			String itemName = item.getName();
			tableModel.addColumn(itemName);
		}
		Vector<String> firstRow = new Vector<String>(); 
		firstRow.add("Initial");
		for (AuctionItem item: this.environment.context.getItemList()) {
			double price = item.getPrice();
			firstRow.add(String.valueOf(price));
		}
		tableModel.addRow(firstRow);
		tableModel.fireTableDataChanged();
	}
	
	private void initCcaTableModel() {
		tableModel  = new DefaultTableModel();

		tableModel.addColumn("Price");
	
		for (AuctionItem item: this.environment.context.getItemList()) {
			String itemName = item.getName();
			String quantity = "(" + item.getQuantity() + ")";
			tableModel.addColumn(itemName+quantity);
		}
		tableModel.fireTableDataChanged();
	}
	
	private void initUlaTableModel() {
		
	}
	
}
