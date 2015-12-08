package auctionEngine;
import dataRepresentation.AuctionEnvironment;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class AuctionPanel extends JPanel {

	private static final long serialVersionUID = 2896868153742237502L;

	private AuctionEnvironment environment;
	
	private JFrame parentFrame;
	
	public AuctionListPanel auctionListPanel;
	private JSpinner spinner_Increment;
	private JSpinner spinner_timeDuration;
	private JSpinner spinner_activityRuleWaivers;
	private JSpinner spinner_activityRuleStartRound;
	private JLabel lblAuctionType;
	private JLabel lblRound;
	private JLabel lblTimer;
	private JLabel lblActivityRuleStartRound;
	
	private JButton btn_StartActivityRule;
	private JButton btn_StartAuction;
	private JButton btn_Back;
	
	private Timer displayTimer; 
	private JPanel panel_AuctionTable;
	
	public AuctionPanel(AuctionEnvironment ae, JFrame parentFrame) {
		this.environment = ae;
		this.parentFrame = parentFrame;
		setLayout(new BorderLayout());

		JPanel panel_All = new JPanel();
		panel_All.setBounds(0, 0, 800, 1000);
		add(panel_All);
		panel_All.setLayout(new GridLayout(2,1)); //two rows and only one column.
		
		panel_AuctionTable = new JPanel();
		panel_AuctionTable.setBounds(0, 0, 800, 1500);
		panel_All.add(panel_AuctionTable);
			
		JPanel panel_Control = new JPanel();
		panel_Control.setLayout(new GridLayout(12,1)); //only one column and many rows.
		panel_All.add(panel_Control);
		//////////////////////////////////////////////////////////////////
		
		JPanel panel_AuctionInfoDisplay = new JPanel();

		panel_AuctionInfoDisplay.setLayout(new FlowLayout());
		panel_Control.add(panel_AuctionInfoDisplay);
		
		lblAuctionType = new JLabel("");
		panel_AuctionInfoDisplay.add(lblAuctionType);
		
		lblRound = new JLabel("");
		panel_AuctionInfoDisplay.add(lblRound);
		
		lblTimer = new JLabel("");
		panel_AuctionInfoDisplay.add(lblTimer);
		
		//////////////////////////////////////////////////////////////////
		
		JPanel panel_AuctionArgsSetting = new JPanel();
		panel_AuctionArgsSetting.setBounds(0, 529, 571, 535);
		panel_Control.add(panel_AuctionArgsSetting);
		
		JLabel lblMinimumIncrement = new JLabel("Minimun Increment: ");
		
		panel_AuctionArgsSetting.add(lblMinimumIncrement);
		//SpinnerModel sm_minIncrement = new SpinnerNumberModel(1, 0, Double.MAX_VALUE, 1); //default value,lower bound,upper bound,increment by
		spinner_Increment = new JSpinner(/*sm_minIncrement*/);
		Component spinner_Increment_Editor = spinner_Increment.getEditor();
		JFormattedTextField jftf_increment = ((JSpinner.DefaultEditor) spinner_Increment_Editor).getTextField();
		jftf_increment.setColumns(3);
		panel_AuctionArgsSetting.add(spinner_Increment);
		
		JLabel lblTimeDuration = new JLabel("Round Time: ");
		panel_AuctionArgsSetting.add(lblTimeDuration);

		//SpinnerModel sm_timeDuration = new SpinnerNumberModel(30, 0, Integer.MAX_VALUE, 1); //default value,lower bound,upper bound,increment by
		spinner_timeDuration = new JSpinner(/*sm_timeDuration*/);
		Component spinner_timeDuration_Editor = spinner_timeDuration.getEditor();
		JFormattedTextField jftf_timeDuration = ((JSpinner.DefaultEditor) spinner_timeDuration_Editor).getTextField();
		jftf_timeDuration.setColumns(3);
		panel_AuctionArgsSetting.add(spinner_timeDuration);
		
		JButton btnNewButton_Set = new JButton("Set");
		btnNewButton_Set.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				environment.context.setMinIncrement(Double.parseDouble(spinner_Increment.getValue().toString()));
				environment.context.setDurationTime((int)Math.round(Double.parseDouble(spinner_timeDuration.getValue().toString())));
				environment.context.setNumberOfActivityRuleWaivers(Integer.parseInt(spinner_activityRuleWaivers.getValue().toString()));
				System.out.println("Minimum increment set to " + environment.context.getMinIncrement());
				System.out.println("Round duration set to " + environment.context.getDurationTime());
				System.out.println("Number of waivers set to " + environment.context.getNumberOfActivityRuleWaivers());
			}
		});
		panel_AuctionArgsSetting.add(btnNewButton_Set);
		
		//////////////////////////////////////////////////////////////////////////
		
		final JPanel panel_AuctionRuleSetting = new JPanel();
		panel_AuctionRuleSetting.setBounds(0, 529, 571, 535);
		panel_Control.add(panel_AuctionRuleSetting);
		
		JLabel lblActivityRuleWaivers = new JLabel("#Waivers: ");
		panel_AuctionRuleSetting.add(lblActivityRuleWaivers);
		
		spinner_activityRuleWaivers = new JSpinner();
		Component spinner_activityRuleWaivers_Editor = spinner_activityRuleWaivers.getEditor();
		JFormattedTextField jftf_activityRuleWaivers = ((JSpinner.DefaultEditor) spinner_activityRuleWaivers_Editor).getTextField();
		jftf_activityRuleWaivers.setColumns(2);
		panel_AuctionRuleSetting.add(spinner_activityRuleWaivers);
		
		lblActivityRuleStartRound = new JLabel("Act rule start rnd: ");
		panel_AuctionRuleSetting.add(lblActivityRuleStartRound);
		
		spinner_activityRuleStartRound = new JSpinner();
		Component spinner_activityRuleStartRound_Editor = spinner_activityRuleStartRound.getEditor();
		JFormattedTextField jftf_activityRuleStartRound = ((JSpinner.DefaultEditor) spinner_activityRuleStartRound_Editor).getTextField();
		jftf_activityRuleStartRound.setColumns(3);
		panel_AuctionRuleSetting.add(spinner_activityRuleStartRound);
		
		btn_StartActivityRule = new JButton("Start Activity Rule");
		btn_StartActivityRule.addActionListener(new ActionListener() {
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
				panel_AuctionRuleSetting.remove(btn_StartActivityRule);
				panel_AuctionRuleSetting.remove(spinner_activityRuleStartRound);
				panel_AuctionRuleSetting.remove(lblActivityRuleStartRound);
				
				// give text field to auctioneer to inform of when activity rule starts
				JTextField activityRuleTextField = new JTextField("The Activity Rule will take effect in round " + spinnerValue + ".");
				activityRuleTextField.setEditable(false);
				panel_AuctionRuleSetting.add(activityRuleTextField);
				
				panel_AuctionRuleSetting.updateUI();
			}
		});
		panel_AuctionRuleSetting.add(btn_StartActivityRule);
		
		JPanel panel_AuctionStartEnd = new JPanel();
		panel_AuctionStartEnd.setBounds(0, 529, 571, 535);
		panel_Control.add(panel_AuctionStartEnd);
		
		btn_StartAuction = new JButton("Start");
		btn_StartAuction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				environment.AuctionStarted = true;
				environment.context.incrementRound(); // increment round from 0 to 1
				environment.auctioneer.start();
				startAuction();
				System.err.println("Click Auction Start:"+ environment.AuctionStarted);
				btn_Back.setEnabled(false);
			}
		});
		panel_AuctionStartEnd.add(btn_StartAuction);
		
		JButton btn_endAuction = new JButton("End");
		btn_endAuction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setAuctionEndFlag();
			}
		});
		panel_AuctionStartEnd.add(btn_endAuction);
		
		btn_Back = new JButton("Back");
		btn_Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backToAuctionConfig();
			}
		});
		panel_AuctionStartEnd.add(btn_Back);
		
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
		this.panel_AuctionTable.removeAll(); //remove all previous tables
		this.panel_AuctionTable.add(auctionListPanel);
		
		
		//reset all labels
		this.lblAuctionType.setText("");
		this.lblRound.setText("");
		this.lblTimer.setText("");
		
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
			btn_Back.setEnabled(true);
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
	private void startAuction() {
		this.environment.context.roundTimeRemain = this.environment.context.getDurationTime();
		this.displayTimer.start();
	}
	
	private void backToAuctionConfig() {
		
		JSplitPane sp = (JSplitPane)(this.parentFrame.getContentPane());
		JPanel auctionContentPanel = (JPanel) sp.getLeftComponent();
		CardLayout contentPaneLayout = (CardLayout)auctionContentPanel.getLayout();
		contentPaneLayout.show(auctionContentPanel, "ConfigPane");

	}
}
