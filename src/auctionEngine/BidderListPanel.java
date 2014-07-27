package auctionEngine;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.table.DefaultTableModel;

import dataRepresentation.Bidder;
import dataRepresentation.BidderList;

public class BidderListPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private BidderList bidderList;
	private JTable table;
	private DefaultTableModel tableModel;

	/**
	 * Create the panel.
	 */
	public BidderListPanel(BidderList list) {
		
		bidderList = list;
		setLayout(new GridLayout(0, 1, 0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane);
		
		table = new JTable();
		String[] columnNames = {"ID", "Name", "IP"};
		String[][] tableVales = {};
		tableModel = new DefaultTableModel(tableVales,columnNames);
		
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		scrollPane.setViewportView(table);
		
		JPanel panel = new JPanel();
		add(panel);
		JButton btnKickOut = new JButton("Kick Out");
		panel.add(btnKickOut);
		
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateBidderList();
				
			}
		};
		Timer displayTimer = new Timer(1000, listener);
		displayTimer.start();

	}
	
	private void updateBidderList() {
		
		//Delete all rows before update
		if (tableModel.getRowCount() > 0) {
		    for (int i=tableModel.getRowCount()-1; i>-1; i--) {
		    	tableModel.removeRow(i);
		    }
		}
		
		//put bidderlist to tablemodel
		for (Bidder bidder: this.bidderList.getList()) {
			String id = Integer.toString(bidder.getID());
			String name = bidder.getName();
			String ip = bidder.getIP();
			String[] rowValue = {id,name,ip};
			tableModel.addRow(rowValue);
		}
	}


}
