package auctionEngine;

import javax.swing.JPanel;

import dataRepresentation.AuctionEnvironment;
import java.awt.GridLayout;
import javax.swing.JSplitPane;

public class AuctionPanel extends JPanel {

	private AuctionEnvironment environment;
	
	private AuctionListPanel auctionListPanel;
	private BidderListPanel bidderListPanel;
	
	public AuctionPanel(AuctionEnvironment ae) {
		this.environment = ae;
		this.auctionListPanel = new AuctionListPanel(this.environment.context);
		this.bidderListPanel = new BidderListPanel(this.environment.bidderList);
		JSplitPane splitPane = new JSplitPane();
		add(splitPane);
		
		splitPane.setLeftComponent(this.auctionListPanel);
		splitPane.setRightComponent(this.bidderListPanel);
		
	}

}
