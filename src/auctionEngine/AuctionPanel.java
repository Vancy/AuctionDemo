package auctionEngine;
import dataRepresentation.AuctionEnvironment;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.Timer;

import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;

public class AuctionPanel extends JPanel {


	private static final long serialVersionUID = 2896868153742237502L;

	private AuctionEnvironment environment;
	
	public AuctionListPanel auctionListPanel;
	private JSpinner spinner_Increment;
	private JLabel lblAuctionType;
	private JLabel lblRound;
	private JLabel lblTimer;
	
	private Timer displayTimer; 
	private JPanel panel_ForList;
	
	public AuctionPanel(AuctionEnvironment ae) {
		this.environment = ae;
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel_All = new JPanel();
		add(panel_All);
		panel_All.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		panel_ForList = new JPanel();
		panel_All.add(panel_ForList);
		
		JPanel panel_Display = new JPanel();
		panel_All.add(panel_Display);
		panel_Display.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		lblAuctionType = new JLabel("SAA auction");
		panel_Display.add(lblAuctionType);
		
		lblRound = new JLabel("Round:");
		panel_Display.add(lblRound);
		
		lblTimer = new JLabel("time:");
		panel_Display.add(lblTimer);
		//setLayout(null);
				
		JPanel panel = new JPanel();
		panel_All.add(panel);
		
		JLabel lblMinimunIncrement = new JLabel("Minimun Increment: ");
		panel.add(lblMinimunIncrement);
		
		spinner_Increment = new JSpinner();
		panel.add(spinner_Increment);
		
		JButton btnNewButton_Stop = new JButton("Set");
		btnNewButton_Stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				environment.context.setMinIncrement(Integer.parseInt(spinner_Increment.getValue().toString()));
			}
		});
		panel.add(btnNewButton_Stop);
		
		JButton btnNewButton_endAuction = new JButton("End Auction");
		btnNewButton_endAuction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setAuctionEndFlag();
			}
		});
		panel.add(btnNewButton_endAuction);
		
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Auction info update
				updateAuctionInfo();
				//Auction list update
				updateAuctionList();


			}
		};
		displayTimer = new Timer(1000, listener);
	}
	
	private void updateAuctionList() {
		if (this.environment.context.bidsProcessingFinished) {
			auctionListPanel.updateAuctionList();
			environment.context.setMinIncrement(Double.parseDouble(this.spinner_Increment.getValue().toString()));
			//Set flag, indicate GUI update end.
			environment.context.bidsProcessingFinished = false;
		}
	}
	
	protected void initAuctionList(){
		this.auctionListPanel = new AuctionListPanel(this.environment);
		this.panel_ForList.add(auctionListPanel);
	}
	
	private void updateAuctionInfo() {
		/*if current time remain is bigger than 0, minus it and display
		 * Else, direcly display 0, until Auctioneer class refresh it
		 */
		if (this.environment.context.roundTimeElapse > 0) {
			this.environment.context.roundTimeElapse--;
		}
		this.lblTimer.setText("Remain:"+Integer.toString(this.environment.context.roundTimeElapse));
		this.lblRound.setText("Round:"+Integer.toBinaryString(this.environment.context.getRound()));
	}
	private void setAuctionEndFlag() {
		this.environment.context.setFinalRound();
	}
	public void startAuction() {
		this.environment.context.roundTimeElapse = this.environment.context.getDurationTime();
		this.displayTimer.start();
	}
}
