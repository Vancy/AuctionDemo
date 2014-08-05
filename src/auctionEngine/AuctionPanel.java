package auctionEngine;
import dataRepresentation.AuctionEnvironment;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AuctionPanel extends JPanel {

private AuctionEnvironment environment;
	
	public AuctionListPanel auctionListPanel;

	
	public AuctionPanel(AuctionEnvironment ae) {
		this.environment = ae;
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		
		JPanel panel = new JPanel();
		add(panel);
		
		JLabel lblMinimunIncrement = new JLabel("Minimun Increment: ");
		panel.add(lblMinimunIncrement);
		
		JSpinner spinner_Increment = new JSpinner();
		panel.add(spinner_Increment);
		
		JButton btnNewButton_endAuction = new JButton("End Auction");
		btnNewButton_endAuction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setAuctionEndFlag();
			}
		});
		panel.add(btnNewButton_endAuction);
		
		JButton btnNewButton_Stop = new JButton("Stop");
		panel.add(btnNewButton_Stop);
	}
	
	public void updateAuctionList(){
		this.auctionListPanel = new AuctionListPanel(this.environment.context);
		add(auctionListPanel);
	}
	
	private void setAuctionEndFlag() {
		this.environment.context.setFinalRound();
	}

}
