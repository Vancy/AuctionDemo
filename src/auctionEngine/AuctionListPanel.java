package auctionEngine;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
		String[] columnNames = {"ID", "Item", "Price"};
		String[][] tableVales = {};
		tableModel = new DefaultTableModel(tableVales,columnNames);
		
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
		
		//Delete all rows before update
		if (tableModel.getRowCount() > 0) {
			for (int i=tableModel.getRowCount()-1; i>-1; i--) {
				tableModel.removeRow(i);
			}
		}
		
		for (AuctionItem item: this.context.getItemList()) {
			String id = Integer.toString(item.getID());
			String name = item.getName();
			double price = item.getPrice();
			String[] rowValue = {id,name,Double.toString(price)};
			tableModel.addRow(rowValue);
		}
	}

}
