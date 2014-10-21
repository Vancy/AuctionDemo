package auctionEngine;
import dataRepresentation.AuctionEnvironment;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class AuctionPanel extends JPanel {

	private static final long serialVersionUID = 2896868153742237502L;

	private AuctionEnvironment environment;
	
	public AuctionListPanel auctionListPanel;
	private JSpinner spinner_Increment;
	private JSpinner spinner_timeDuration;
	private JLabel lblAuctionType;
	private JLabel lblRound;
	private JLabel lblTimer;
	
	private Timer displayTimer; 
	private JPanel panel_ForList;
	
	public AuctionPanel(AuctionEnvironment ae) {
		this.environment = ae;
		setLayout(null);
		
		JPanel panel_All = new JPanel();
		panel_All.setBounds(31, 5, 571, 564);
		add(panel_All);
		panel_All.setLayout(null);
		
		panel_ForList = new JPanel();
		panel_ForList.setBounds(0, 0, 571, 505);
		panel_All.add(panel_ForList);
		
		JPanel panel_Display = new JPanel();
		panel_Display.setBounds(0, 503, 571, 25);
		panel_All.add(panel_Display);
		panel_Display.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		lblAuctionType = new JLabel("Auction");
		panel_Display.add(lblAuctionType);
		
		lblRound = new JLabel("Round:");
		panel_Display.add(lblRound);
		
		lblTimer = new JLabel("time:");
		panel_Display.add(lblTimer);
		//setLayout(null);
				
		JPanel panel = new JPanel();
		panel.setBounds(0, 529, 571, 35);
		panel_All.add(panel);
		
		
		JLabel lblMinimunIncrement = new JLabel("Minimun Increment: ");
		
		panel.add(lblMinimunIncrement);
		//SpinnerModel sm_minIncrement = new SpinnerNumberModel(1, 0, Double.MAX_VALUE, 1); //default value,lower bound,upper bound,increment by
		spinner_Increment = new JSpinner(/*sm_minIncrement*/);
		panel.add(spinner_Increment);
		
		JLabel lblTimeDuration = new JLabel("Duration Time: ");
		panel.add(lblTimeDuration);

		//SpinnerModel sm_timeDuration = new SpinnerNumberModel(30, 0, Integer.MAX_VALUE, 1); //default value,lower bound,upper bound,increment by
		spinner_timeDuration = new JSpinner(/*sm_timeDuration*/);
		panel.add(spinner_timeDuration);
		
		JButton btnNewButton_Stop = new JButton("Set");
		btnNewButton_Stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				environment.context.setMinIncrement(Double.parseDouble(spinner_Increment.getValue().toString()));
				environment.context.setDurationTime((int)Math.round(Double.parseDouble(spinner_timeDuration.getValue().toString())));
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
			//Set flag, indicate GUI update end.
			environment.context.bidsProcessingFinished = false;
		}
	}
	
	protected void initAuctionList(){
		this.auctionListPanel = new AuctionListPanel(this.environment);
		this.panel_ForList.add(auctionListPanel);
		
		//Set minimun increment to spinner
		this.spinner_Increment.setValue(environment.context.getMinIncrement());
		//Set time_duration to spinner
		this.spinner_timeDuration.setValue(environment.context.getDurationTime());
	}
	
	private void updateAuctionInfo() {
		if (this.environment.context.isFinalRound()) {
			this.lblTimer.setText("Auction Finished");
			this.lblRound.setText("Round:"+Integer.toString(this.environment.context.getRound()));
			return;
		}
		this.lblAuctionType.setText(this.environment.context.getType() + " auction");
		/*if current time remain is bigger than 0, minus it and display
		 * Else, direcly display 0, until Auctioneer class refresh it
		 */
		if (this.environment.context.roundTimeRemain > 0) {
			this.environment.context.roundTimeRemain--;
		}
		this.lblTimer.setText("Remain:"+Integer.toString(this.environment.context.roundTimeRemain));
		this.lblRound.setText("Round:"+Integer.toString(this.environment.context.getRound()));
	}
	private void setAuctionEndFlag() {
		this.environment.context.setFinalRound();
	}
	public void startAuction() {
		this.environment.context.roundTimeRemain = this.environment.context.getDurationTime();
		this.displayTimer.start();
	}
}
