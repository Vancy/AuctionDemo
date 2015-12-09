package auctionEngine;


import dataRepresentation.AuctionEnvironment;
import dataRepresentation.AuctionItem;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;




public class AuctionConfigPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6898719932567042813L;
	private DefaultTableModel tableModel = new DefaultTableModel();
	private JComboBox<String>  typeComboBox;
	private JPanel panel_auctionParemeters;
	private JSpinner spinner_minIncrement;
	private JSpinner spinner_roundDuration;
	private String auctionTypes[] = {"SAA", "CCA", "LUA"};

	private JLabel lblRoundDurationsec;
	private JLabel lblMinimumIncrement;
	private JPanel panel_minimumInrement;
	private AuctionEnvironment environment;
	private JFrame parentFrame;
	private JTable table;
	
	private AuctionMainWindow mainWindow;

	/**
	 * Create the panel.
	 */
	public AuctionConfigPanel(AuctionMainWindow mw, AuctionEnvironment environment, JFrame parent) {
		this.mainWindow = mw;
		this.environment = environment;
		this.parentFrame = parent;
		setLayout(null);
		
		JPanel panel_auctionType = new JPanel();
		panel_auctionType.setBounds(51, 30, 415, 40);
		add(panel_auctionType);
		panel_auctionType.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel lblAuctionType = new JLabel("Auction Type:");
		lblAuctionType.setFont(new Font("Dialog", Font.BOLD, 17));
		panel_auctionType.add(lblAuctionType);
		
		typeComboBox = new JComboBox<String>();

		for (int i=0; i<auctionTypes.length; i++) {
			typeComboBox.addItem(auctionTypes[i]);
		}
		panel_auctionType.add(typeComboBox);
		
		SpinnerModel sm_roundDuration = new SpinnerNumberModel(30, 0, Integer.MAX_VALUE, 1); //default value,lower bound,upper bound,increment by
		
		SpinnerModel sm_minIncrement = new SpinnerNumberModel(1, 0, Double.MAX_VALUE, 1); //default value,lower bound,upper bound,increment by
		
		panel_auctionParemeters = new JPanel();
		panel_auctionParemeters.setBounds(51, 82, 415, 139);
		add(panel_auctionParemeters);
		panel_auctionParemeters.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_roundDuration = new JPanel();
		panel_auctionParemeters.add(panel_roundDuration);
		panel_roundDuration.setLayout(new GridLayout(0, 2, 0, 0));
		
		lblRoundDurationsec = new JLabel("Round Duration (sec):");
		lblRoundDurationsec.setFont(new Font("Dialog", Font.BOLD, 17));
		panel_roundDuration.add(lblRoundDurationsec);
		spinner_roundDuration = new JSpinner(sm_roundDuration); 
		panel_roundDuration.add(spinner_roundDuration);
		
		JLabel label = new JLabel("");
		panel_auctionParemeters.add(label);
		
		panel_minimumInrement = new JPanel();
		panel_auctionParemeters.add(panel_minimumInrement);
		panel_minimumInrement.setLayout(new GridLayout(1, 0, 0, 0));
		
		lblMinimumIncrement = new JLabel("Minimum Increment:");
		lblMinimumIncrement.setFont(new Font("Dialog", Font.BOLD, 17));
		panel_minimumInrement.add(lblMinimumIncrement);
		spinner_minIncrement = new JSpinner(sm_minIncrement);
		panel_minimumInrement.add(spinner_minIncrement);
		
		JPanel panel_auctionItems = new JPanel();
		panel_auctionItems.setBounds(51, 222, 415, 229);
		add(panel_auctionItems);
		
        panel_auctionItems.setLayout(new GridLayout(0, 1, 0, 0));
		
        JScrollPane scrollPane = new JScrollPane();
		panel_auctionItems.add(scrollPane);
		
		table = new JTable(tableModel);
		scrollPane.setViewportView(table);
		
				
		JPanel panel_tableOperation = new JPanel();
		panel_auctionItems.add(panel_tableOperation);
		panel_tableOperation.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                int rowCount = table.getRowCount() + 1;  
				String[] rowValues = {"Item"+rowCount, "0"};
                tableModel.addRow(rowValues);
            }
        });
		panel_tableOperation.add(btnAdd);
				
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                int selectedRow = table.getSelectedRow();
                if(selectedRow!=-1)  
                {
                    tableModel.removeRow(selectedRow); 
                }
            }
        });
		panel_tableOperation.add(btnDelete);
		
		JPanel panel_quit = new JPanel();
		panel_quit.setBounds(51, 463, 415, 40);
		add(panel_quit);
		
		JButton btnConfirm = new JButton("Confirm");
		btnConfirm.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                startAuction();
                
            }
        });
		panel_quit.add(btnConfirm);
		
		JButton btnQuit = new JButton("Quit");
		panel_quit.add(btnQuit);
		
		/*
		 * This listener must put at the end of Constructor, after
		 * all widgets initialized
		 */
		typeComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				/*
				 * When detecting comboBox label changes,
				 * repaint the AuctionItemTable,
				 * Modify Parameter Panel
				 */
				changeParameterPanel(e.getItem().toString());
		 		initTable(e.getItem().toString());
		 	}
		 });
		initTable("SAA");
	}
	
	private void changeParameterPanel(String auctionType) {
		if (auctionType.equals("SAA")) {
			this.lblRoundDurationsec.setText("Round Duration:(s)");
			this.spinner_roundDuration.setValue(30);
			this.lblMinimumIncrement.setText("Minimum Increment:");

		} else if (auctionType.equals("CCA")) {
			this.lblRoundDurationsec.setText("Round Tick:(ms)");
			this.spinner_roundDuration.setValue(10000);
			this.lblMinimumIncrement.setText("Minimum Increment:");
		} else if (auctionType.equals("LUA")) {
			this.lblRoundDurationsec.setText("Tick Duration:(ms)");
			this.spinner_roundDuration.setValue(200);
			this.lblMinimumIncrement.setText("Tick Increment:");
		}
	}
	private void initTable(String auctionType) {
		System.err.println(auctionType);
		if (auctionType.equals("SAA")) {
			tableModel.setColumnCount(0);
			tableModel.setRowCount(0);
			tableModel.addColumn("Item");
			tableModel.addColumn("Initial Price");
			//put some initial data
			String[] s1 = {"ItemA", "0"};
			String[] s2 = {"ItemB", "0"};
			String[] s3 = {"ItemC", "0"};
			tableModel.addRow(s1);
			tableModel.addRow(s2);
			tableModel.addRow(s3);
		} else if (auctionType.equals("CCA")) {
			tableModel.setColumnCount(0);
			tableModel.setRowCount(0);
			tableModel.addColumn("Item");
			tableModel.addColumn("Quantity");
			tableModel.addColumn("Initial Price");
			tableModel.addColumn("Unit Eligibility");
			//put some initial data
			String[] s1 = {"ItemA", "5", "0", "5"};
			String[] s2 = {"ItemB", "10", "5", "10"};
			String[] s3 = {"ItemC", "8", "2", "8"};
			tableModel.addRow(s1);
			tableModel.addRow(s2);
			tableModel.addRow(s3);
		} else if (auctionType.equals("LUA")) {
			tableModel.setColumnCount(0);
			tableModel.setRowCount(0);
			tableModel.addColumn("Item");
			tableModel.addColumn("Start Price");
			tableModel.addColumn("Max Price");
			//put some initial data
			String[] s1 = {"ItemA", "0", "100"};
			String[] s2 = {"ItemB", "0", "500"};
			String[] s3 = {"ItemC", "5", "400"};
			tableModel.addRow(s1);
			tableModel.addRow(s2);
			tableModel.addRow(s3);
		}
		
	}
	
	private void AuctionEvironmentReset() {
		this.environment.context.roundTimeRemain = this.environment.context.getDurationTime();
		this.environment.context.setRound(0);
	}
	
	private void startAuction() {
		AuctionEvironmentReset();
		
		String auctionType = this.typeComboBox.getSelectedItem().toString();

		if (auctionType.equals("SAA")) {
			startSaaAuction();
		} else if (auctionType.equals("CCA")) {
			startCcaAuction();
		}else if (auctionType.equals("LUA")) {
			startLuaAuction();
		}
		
		/*************inform GUI to update**********************/
		JSplitPane sp = (JSplitPane)(this.parentFrame.getContentPane());
		JPanel auctionContentPanel = (JPanel) sp.getLeftComponent();
		CardLayout contentPaneLayout = (CardLayout)auctionContentPanel.getLayout();
		contentPaneLayout.show(auctionContentPanel, "AuctionPane");
		//Update AuctionListPanel
		this.mainWindow.auctionPane.initAuctionList();
		
		//enable Bidderlist auction start button 
		BidderListPanel bidderListPanel = (BidderListPanel) sp.getRightComponent();
		bidderListPanel.btnKickOut.setEnabled(true);
		bidderListPanel.btnAddAgent.setEnabled(true);
	}
	
	private void startSaaAuction() {

		this.environment.context.setType("SAA");
		
		int time_duration = Integer.parseInt(this.spinner_roundDuration.getValue().toString());
		float min_increment = Float.parseFloat(this.spinner_minIncrement.getValue().toString());

		ArrayList<AuctionItem> list = new ArrayList<AuctionItem>();

		for (int i=0; i<this.table.getRowCount(); i++) {
			String name = this.table.getValueAt(i, 0).toString();
			float price = Float.parseFloat(this.table.getValueAt(i, 1).toString());

			list.add(new AuctionItem(name, price, 0/*invalid for SAA*/,0/*eligibility is invalid for SAA*/));
		}
		this.environment.context.setData(time_duration, min_increment, list);
		System.out.println(this.environment.context.generateXml());

	}
	
	private void startCcaAuction() {

		this.environment.context.setType("CCA");
		
		int time_duration = Integer.parseInt(this.spinner_roundDuration.getValue().toString());
		time_duration = time_duration / 1000;
		float min_increment = Float.parseFloat(this.spinner_minIncrement.getValue().toString());
		
		ArrayList<AuctionItem> list = new ArrayList<AuctionItem>();

		for (int i=0; i<this.table.getRowCount(); i++) {
			String name = this.table.getValueAt(i, 0).toString();
			int quantity = Integer.parseInt(this.table.getValueAt(i, 1).toString());
			int startingPrice = Integer.parseInt(this.table.getValueAt(i, 2).toString());
			int eligibility = Integer.parseInt(this.table.getValueAt(i, 3).toString());
			list.add(new AuctionItem(name, startingPrice, quantity, eligibility));
		}
		this.environment.context.setData(time_duration, min_increment, list);
		System.out.println(this.environment.context.generateXml());
		
	}
	
	private void startLuaAuction() {
		//TODO: ULA auction preparation
	}
}
