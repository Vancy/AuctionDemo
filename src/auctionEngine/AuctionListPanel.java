package auctionEngine;

import java.awt.GridLayout;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

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
	
	private double calculatePayoff(dataRepresentation.Agent currAgent) {
		SortedSet<Map.Entry<List<AuctionItem>, Double>> sortedValuations = new TreeSet<Map.Entry<List<AuctionItem>, Double>>(
	            new Comparator<Map.Entry<List<AuctionItem>, Double>>() {

					@Override
					public int compare(
							Entry<List<AuctionItem>, Double> o1,
							Entry<List<AuctionItem>, Double> o2) {
						int diff = o2.getKey().size() - o1.getKey().size();
						if (diff == 0) {
							return System.identityHashCode(o2.getKey()) - System.identityHashCode(o1.getKey());
						} else {
							return diff;
						}
					}
	            });
		sortedValuations.addAll(currAgent.getValuations().entrySet());
		
		nextValuation:
		for (Entry<List<AuctionItem>, Double> agentItemList : sortedValuations) {
			double totalPricePaid = 0.0;
			for (AuctionItem agentItem : agentItemList.getKey()) {
				for (AuctionItem item : this.environment.context.getItemList()) {
					if (item.getID() == agentItem.getID()) {
						// item is in curr valuation
						if (item.getOwner().getID() == currAgent.getID()) {
							// agent won this item
							totalPricePaid += item.getPrice();
						} else {
							// agent lost this item
							continue nextValuation;
						}
					}
				}
			}
			//payoff = valuation - price paid;
			double payoff = agentItemList.getValue() - totalPricePaid;
			return payoff;
		}
		// agent didnt win anything that he has a valuation for. Therefore, his payoff is 0 - totalPricePaid
		double payoff = 0.0;
		for (AuctionItem item : this.environment.context.getItemList()) {
			if (item.getOwner().getID() == currAgent.getID()) {
				payoff -= item.getPrice();
			}
		}
		return payoff;
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
		if (this.environment.context.isFinalRound()) {
			int numberOfItems = this.environment.context.getItemList().size();
			int numberOfPayoffs = 0;
			Vector<String> divider = new Vector<String>();
			Vector<String> payoffHeader = new Vector<String>();
			Vector<String> payoffData = new Vector<String>();
			for (int i = 0; i < numberOfItems; i++) {
				divider.add("");
			}
			divider.add("");
			payoffHeader.add("Payoffs");
			payoffData.add("");
			
			for (dataRepresentation.Bidder currBidder : this.environment.bidderList.getList()) {
				if (!(currBidder instanceof dataRepresentation.Agent)) {
					continue;
				}
				dataRepresentation.Agent currAgent = (dataRepresentation.Agent) currBidder;
				
				payoffHeader.add(currAgent.getName());
				payoffData.add(String.valueOf(calculatePayoff(currAgent)));
				numberOfPayoffs++;
				
				if (numberOfPayoffs == numberOfItems) {
					tableModel.addRow(divider);
					tableModel.addRow(payoffHeader);
					tableModel.addRow(payoffData);
					payoffHeader = new Vector<String>();
					payoffData = new Vector<String>();
					divider.add("");
					payoffHeader.add("");
					payoffData.add("");
				}
			}
			
			tableModel.addRow(divider);
			tableModel.addRow(payoffHeader);
			tableModel.addRow(payoffData);
			
			
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
