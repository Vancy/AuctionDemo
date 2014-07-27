package auctionEngine;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import dataRepresentation.AuctionEnvironment;

import java.awt.CardLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AuctionMainWindow {

	protected JFrame frame;
	
	private JPanel contentPane;
	private AuctionConfigPanel auctionConfigPane;
	private AuctionPanel auctionPane;
	

	AuctionEnvironment environment;
	
	/**
	 * Create the application.
	 */
	public AuctionMainWindow(AuctionEnvironment e) {
		this.environment = e;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setBounds(100, 100, 617, 670);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


	    JPanel contentPane = new JPanel();
	    contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	    contentPane.setLayout(new CardLayout());
	    this.auctionConfigPane = new AuctionConfigPanel(this.environment.context, this.frame);
	    this.auctionPane = new AuctionPanel(this.environment);
	    //contentPane.add(new JPanel(), "blankPane");
	    contentPane.add(auctionConfigPane, "ConfigPane"); 
	    contentPane.add(auctionPane, "AuctionPane");
	    frame.setContentPane(contentPane);
        //frame.pack();   
        frame.setLocationByPlatform(true);

	        
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnStart = new JMenu("Start");
		menuBar.add(mnStart);
		
		JMenuItem mntmNewAuction = new JMenuItem("New Auction");
		mntmNewAuction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("New Auction clicked");
				changeToAuctionCreation();
			}
		});
		mnStart.add(mntmNewAuction);
		
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		mnStart.add(mntmQuit);
		
		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);
		
		JMenuItem mntmHelp = new JMenuItem("Help");
		mnAbout.add(mntmHelp);
		
        frame.setVisible(true);
	}
	
	private void changeToAuctionCreation() {
		System.out.println("Create new Auction triggered");
		CardLayout contentPaneLayout = (CardLayout)this.contentPane.getLayout();
		contentPaneLayout.show(contentPane, "ConfigPane");
		frame.revalidate();  // fresh
		
	}

}
