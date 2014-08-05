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
		table.setDefaultRenderer(Object.class, new PriceOwnerCellRenderer(this.environment));
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
	
		if (this.environment.context.bidsProcessingFinished) {
			Vector<String> newRow = new Vector<String>(); 
			newRow.add(Integer.toString((this.environment.context.getRound()-1)));
			for (AuctionItem item: this.environment.context.getItemList()) {
				double price = item.getPrice();
				newRow.add(String.valueOf(price));
			}
			tableModel.addRow(newRow);
			this.environment.context.bidsProcessingFinished = false;
		}

	}
	
	private void initTableModel() {
		
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
	
}
