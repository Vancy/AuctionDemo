package auctionEngine;

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
	
	private void updateAuctionList() {
	
		
		Vector<String> firstRow = new Vector<String>(); 
		firstRow.add("0");
		for (AuctionItem item: this.context.getItemList()) {
			double price = item.getPrice();
			firstRow.add(String.valueOf(price));
		}
		tableModel.addRow(firstRow);
		tableModel.fireTableDataChanged();
	}
	
	private void initTableModel() {
		tableModel = new DefaultTableModel();
		tableModel.addColumn("ID");
	
		
		System.err.println("size"+this.context.getItemList().size());
		for (AuctionItem item: this.context.getItemList()) {
			String itemName = item.getName();
			System.out.println(itemName);
			tableModel.addColumn(itemName);
		}
		Vector<String> firstRow = new Vector<String>(); 
		firstRow.add("0");
		for (AuctionItem item: this.context.getItemList()) {
			double price = item.getPrice();
			firstRow.add(String.valueOf(price));
		}
		tableModel.addRow(firstRow);
		tableModel.fireTableDataChanged();
	}
	
}
