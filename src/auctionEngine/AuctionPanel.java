package auctionEngine;

import javax.swing.JPanel;
import dataRepresentation.AuctionEnvironment;


public class AuctionPanel extends JPanel {

	private AuctionEnvironment environment;
	
	private AuctionListPanel auctionListPanel;

	
	public AuctionPanel(AuctionEnvironment ae) {
		this.environment = ae;
		this.auctionListPanel = new AuctionListPanel(this.environment.context);
		
		add(auctionListPanel);
	}

}
