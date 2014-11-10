package auctionEngine;
import dataRepresentation.AuctionEnvironment;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.JTextField;
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
	private JSpinner spinner_activityRuleWaivers;
	private JSpinner spinner_activityRuleStartRound;
	private JLabel lblAuctionType;
	private JLabel lblRound;
	private JLabel lblTimer;
	private JLabel lblActivityRuleStartRound;
	
	private JPanel panel;
	private JButton btnNewButton_StartActivityRule;
	
	private Timer displayTimer; 
	private JPanel panel_ForList;
	
	public AuctionPanel(AuctionEnvironment ae) {
		this.environment = ae;
		setLayout(null);
		
		JPanel panel_All = new JPanel();
		//panel_All.setBounds(31, 5, 571, 564);
		panel_All.setBounds(31, 5, 571, 1064);
		add(panel_All);
		panel_All.setLayout(null);
		
		panel_ForList = new JPanel();
		panel_ForList.setBounds(0, 0, 571, 505);
		panel_All.add(panel_ForList);
		
		JPanel panel_Display = new JPanel();
		panel_Display.setBounds(0, 503, 571, 25);
		panel_All.add(panel_Display);
		panel_Display.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		lblAuctionType = new JLabel("");
		panel_Display.add(lblAuctionType);
		
		lblRound = new JLabel("");
		panel_Display.add(lblRound);
		
		lblTimer = new JLabel("");
		panel_Display.add(lblTimer);
				
		panel = new JPanel();
		panel.setBounds(0, 529, 571, 535);
		panel_All.add(panel);
		
		
		JLabel lblMinimumIncrement = new JLabel("Min Inc: ");
		
		panel.add(lblMinimumIncrement);
		//SpinnerModel sm_minIncrement = new SpinnerNumberModel(1, 0, Double.MAX_VALUE, 1); //default value,lower bound,upper bound,increment by
		spinner_Increment = new JSpinner(/*sm_minIncrement*/);
		panel.add(spinner_Increment);
		
		JLabel lblTimeDuration = new JLabel("Rnd Time: ");
		panel.add(lblTimeDuration);

		//SpinnerModel sm_timeDuration = new SpinnerNumberModel(30, 0, Integer.MAX_VALUE, 1); //default value,lower bound,upper bound,increment by
		spinner_timeDuration = new JSpinner(/*sm_timeDuration*/);
		panel.add(spinner_timeDuration);
		
		JLabel lblActivityRuleWaivers = new JLabel("#Waivers: ");
		panel.add(lblActivityRuleWaivers);
		
		spinner_activityRuleWaivers = new JSpinner();
		panel.add(spinner_activityRuleWaivers);
		
		lblActivityRuleStartRound = new JLabel("Act rule start rnd: ");
		panel.add(lblActivityRuleStartRound);
		
		spinner_activityRuleStartRound = new JSpinner();
		panel.add(spinner_activityRuleStartRound);
		
		btnNewButton_StartActivityRule = new JButton("Start Activity Rule");
		btnNewButton_StartActivityRule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int spinnerValue = (int)Double.parseDouble(spinner_activityRuleStartRound.getValue().toString());
				int currentRound = environment.context.getRound();
				System.out.println(spinnerValue + " " + currentRound);
				if (spinnerValue - currentRound < 3) {
					if (spinnerValue == 1 && currentRound == 0) {
						// valid
					} else {
						return;
					}
				}
				environment.context.setActivityRuleAnnounced(true);
				environment.context.setActivityRuleStartRound(spinnerValue);
				System.out.println("Activity Rule announced in round " + currentRound + ". It begins in round " + spinnerValue);
				
				// destroy button and spinner
				panel.remove(btnNewButton_StartActivityRule);
				panel.remove(spinner_activityRuleStartRound);
				panel.remove(lblActivityRuleStartRound);
				
				// give text field to auctioneer to inform of when activity rule starts
				JTextField activityRuleTextField = new JTextField("The Activity Rule will take effect in round " + spinnerValue + ".");
				activityRuleTextField.setEditable(false);
				panel.add(activityRuleTextField);
				
				panel.updateUI();
			}
		});
		panel.add(btnNewButton_StartActivityRule);
		
		JButton btnNewButton_Stop = new JButton("Set");
		btnNewButton_Stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				environment.context.setMinIncrement(Double.parseDouble(spinner_Increment.getValue().toString()));
				environment.context.setDurationTime((int)Math.round(Double.parseDouble(spinner_timeDuration.getValue().toString())));
				environment.context.setNumberOfActivityRuleWaivers(Integer.parseInt(spinner_activityRuleWaivers.getValue().toString()));
				System.out.println("Minimum increment set to " + environment.context.getMinIncrement());
				System.out.println("Round duration set to " + environment.context.getDurationTime());
				System.out.println("Number of waivers set to " + environment.context.getNumberOfActivityRuleWaivers());
			}
		});
		panel.add(btnNewButton_Stop);
		
		JButton btnNewButton_endAuction = new JButton("End");
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
		//Set number of activity rule waivers to spinner
		this.spinner_activityRuleWaivers.setValue(environment.context.getNumberOfActivityRuleWaivers());
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
