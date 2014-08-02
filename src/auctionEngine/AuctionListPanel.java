package auctionEngine;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import dataRepresentation.AuctionContext;
import dataRepresentation.AuctionItem;


public class AuctionListPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private AuctionContext context;
	private JTable table;
	private DefaultTableModel tableModel;

	/**
	 * Create the panel.
	 */
	public AuctionListPanel(AuctionContext c) {
		
		this.context = c;
		setLayout(new GridLayout(0, 1, 0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane);
		
		table = new JTable();
		initTableModel(); //initialize table model to fill data
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		scrollPane.setViewportView(table);
		
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateAuctionList();	
			}
		};
		Timer displayTimer = new Timer(1000, listener);
		displayTimer.start();

		
	}
	
	public void updateAuctionList() {
	
		if (this.context.bidsProcessingFinished) {
			Vector<String> firstRow = new Vector<String>(); 
			firstRow.add(Integer.toString((this.context.getRound()-1)));
			for (AuctionItem item: this.context.getItemList()) {
				double price = item.getPrice();
				firstRow.add(String.valueOf(price));
			}
			tableModel.addRow(firstRow);
			this.context.bidsProcessingFinished = false;
		}

	}
	
	private void initTableModel() {
		
		tableModel = new DefaultTableModel();
		tableModel.addColumn("Round");
	
		for (AuctionItem item: this.context.getItemList()) {
			String itemName = item.getName();
			tableModel.addColumn(itemName);
		}
		Vector<String> firstRow = new Vector<String>(); 
		firstRow.add("Initial");
		for (AuctionItem item: this.context.getItemList()) {
			double price = item.getPrice();
			firstRow.add(String.valueOf(price));
		}
		tableModel.addRow(firstRow);
		tableModel.fireTableDataChanged();
		
		//Add Highest Price Owner for AuctionListTable
//		table.setDefaultsRenderer(String.class, new PriceOwnerCellRenderer(this.context));
		System.err.println("current colum"+tableModel.getColumnCount());
//		table.getColumnModel().getColumn(1).setCellRenderer(new PriceOwnerCellRenderer(this.context));
//		for (int i=1; i<tableModel.getColumnCount(); i++) {
//			table.getColumnModel().getColumn(i).setCellRenderer(new PriceOwnerCellRenderer(this.context));
//		}
	}
	
}
