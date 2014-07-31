package auctionEngine;

import dataRepresentation.AuctionContext;
import dataRepresentation.AuctionItem;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
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

import javax.swing.ScrollPaneConstants;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;


public class AuctionConfigPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6898719932567042813L;
	private DefaultTableModel tableModel;
	private JComboBox<String>  typeComboBox;
	private JSpinner spinner_minIncrement;
	private JSpinner spinner_roundDuration;
	private String auctionTypes[] = {"SAA", "CCA", "ULA"};
	private JTextField textField_name;
	private JTextField textField_price;
	
	private AuctionContext context;
	private JFrame parentFrame;
	private JTable table;

	/**
	 * Create the panel.
	 */
	public AuctionConfigPanel(AuctionContext context, JFrame parent) {
		this.context = context;
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
		
		JPanel panel_roundDuration = new JPanel();
		panel_roundDuration.setBounds(51, 90, 415, 40);
		add(panel_roundDuration);
		panel_roundDuration.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel lblRoundDurationsec = new JLabel("Round Duration (sec):");
		lblRoundDurationsec.setFont(new Font("Dialog", Font.BOLD, 17));
		panel_roundDuration.add(lblRoundDurationsec);
		
		SpinnerModel sm = new SpinnerNumberModel(60, 0, Integer.MAX_VALUE, 1); //default value,lower bound,upper bound,increment by
		spinner_roundDuration = new JSpinner(sm); 
		panel_roundDuration.add(spinner_roundDuration);
		
		JPanel panel_minimumInrement = new JPanel();
		panel_minimumInrement.setBounds(51, 157, 415, 40);
		add(panel_minimumInrement);
		panel_minimumInrement.setLayout(new GridLayout(1, 0, 0, 0));
		
		JLabel lblMinimumIncrement = new JLabel("Minimum Increment:");
		lblMinimumIncrement.setFont(new Font("Dialog", Font.BOLD, 17));
		panel_minimumInrement.add(lblMinimumIncrement);
		
		sm = new SpinnerNumberModel(0, 0, Double.MAX_VALUE, 1); //default value,lower bound,upper bound,increment by
		spinner_minIncrement = new JSpinner(sm);
		panel_minimumInrement.add(spinner_minIncrement);
		
		JPanel panel_auctionItems = new JPanel();
		panel_auctionItems.setBounds(51, 222, 415, 229);
		add(panel_auctionItems);
		
		String[] columnNames = {"Item", "Price"};
		String[][] tableVales = {{"Item1","10.0"},{"Item3","20.0"},{"Item2","30.0"},{"Item4","40.0"}}; 
        tableModel = new DefaultTableModel(tableVales,columnNames);
        panel_auctionItems.setLayout(new GridLayout(0, 1, 0, 0));
		
        JScrollPane scrollPane = new JScrollPane();
		panel_auctionItems.add(scrollPane);
		
		table = new JTable(tableModel);
		table.addMouseListener(new MouseAdapter() {
		      public void mouseClicked(MouseEvent e) {
		            int row = table.rowAtPoint(e.getPoint());
		            textField_name.setText(table.getValueAt(row, 0).toString());
		            textField_price.setText(table.getValueAt(row, 1).toString());
		        }
		 });
		scrollPane.setViewportView(table);
		
				
		
				
				
		JPanel panel_tableOperation = new JPanel();
		panel_auctionItems.add(panel_tableOperation);
		panel_tableOperation.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
				
		JPanel panel_1 = new JPanel();
		panel_tableOperation.add(panel_1);
			
		JLabel lblName = new JLabel("Name:");
		panel_1.add(lblName);
				
		textField_name = new JTextField();
		panel_1.add(textField_name);
		textField_name.setColumns(10);
				
				JLabel lblPrice = new JLabel("Price:");
				panel_1.add(lblPrice);
				
				textField_price = new JTextField();
				panel_1.add(textField_price);
				textField_price.setColumns(10);
				
				JPanel panel_2 = new JPanel();
				panel_tableOperation.add(panel_2);
				
				JButton btnAdd = new JButton("Add");
				btnAdd.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String[] rowValues = {textField_name.getText(),textField_price.getText()};
                tableModel.addRow(rowValues);  
                int rowCount = table.getRowCount() + 1;   
                textField_name.setText("Item"+rowCount);
                textField_price.setText("0");
            }
        });
				panel_2.add(btnAdd);
				
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
				panel_2.add(btnDelete);
				
				JButton btnAlter = new JButton("Alter");
				btnAlter.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                int selectedRow = table.getSelectedRow();
                if(selectedRow!= -1)  
                {
                    tableModel.setValueAt(textField_name.getText(), selectedRow, 0);
                    tableModel.setValueAt(textField_price.getText(), selectedRow, 1);
                }
            }
        });
		panel_2.add(btnAlter);
		
		JPanel panel__quit = new JPanel();
		panel__quit.setBounds(51, 463, 415, 40);
		add(panel__quit);
		
		JButton btnConfirm = new JButton("Confirm");
		btnConfirm.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                startAuction();
                
            }
        });
		panel__quit.add(btnConfirm);
		
		JButton btnQuit = new JButton("Quit");
		panel__quit.add(btnQuit);

	}
	private void startAuction() {
		String typeName = this.typeComboBox.getSelectedItem().toString();
		this.context.setType(typeName);
		
		int time_duration = Integer.parseInt(this.spinner_roundDuration.getValue().toString());
		float min_increment = Float.parseFloat(this.spinner_minIncrement.getValue().toString());
	
		ArrayList<AuctionItem> list = new ArrayList<AuctionItem>();
		for (int i=0; i<this.table.getRowCount(); i++) {
			String name = this.table.getValueAt(i, 0).toString();
			float price = Float.parseFloat(this.table.getValueAt(i, 1).toString());
			list.add(new AuctionItem(name, price));
		}
		this.context.setData(time_duration, min_increment, list);
		System.out.println(this.context.generateXml());
		JSplitPane sp = (JSplitPane)(this.parentFrame.getContentPane());
		JPanel auctionContentPanel = (JPanel) sp.getLeftComponent();
		CardLayout contentPaneLayout = (CardLayout)auctionContentPanel.getLayout();
		contentPaneLayout.show(auctionContentPanel, "AuctionPane");

	}
}
