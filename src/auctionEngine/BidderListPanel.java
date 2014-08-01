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
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

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
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{450, 0};
		gridBagLayout.rowHeights = new int[]{150, 150, 0};
		gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		table = new JTable();
		String[] columnNames = {"ID", "Name", "IP"};
		String[][] tableVales = {};
		tableModel = new DefaultTableModel(tableVales,columnNames);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		add(scrollPane, gbc_scrollPane);
		
		table = new JTable(tableModel);
		table.setDefaultRenderer(String.class, new NameTableCellRenderer());
		table.getColumnModel().getColumn(0).setCellRenderer(new NameTableCellRenderer());
		table.getColumnModel().getColumn(2).setCellRenderer(new NameTableCellRenderer());
		table.getColumn("Name").setCellRenderer(new NameTableCellRenderer());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		scrollPane.setViewportView(table);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.BASELINE;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		add(panel, gbc_panel);
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