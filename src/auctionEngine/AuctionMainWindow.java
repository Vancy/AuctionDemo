package auctionEngine;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import dataRepresentation.AuctionEnvironment;

import java.awt.CardLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AuctionMainWindow {

	protected JFrame frame;
	
	private JPanel contentPane;
	private AuctionConfigPanel auctionConfigPane;
	protected AuctionPanel auctionPane;
	

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
		frame.setTitle("Auction Simulation System");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JSplitPane splitPane = new JSplitPane();
		
	    JPanel auctionContentPane = new JPanel();
	    //auctionContentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	    auctionContentPane.setLayout(new CardLayout());
	    this.auctionConfigPane = new AuctionConfigPanel(this, this.environment, this.frame);
	    this.auctionPane = new AuctionPanel(this.environment);
	    auctionContentPane.add(auctionConfigPane, "ConfigPane"); 
	    auctionContentPane.add(auctionPane, "AuctionPane");
	    
	    splitPane.setResizeWeight(0.99);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
		splitPane.setRightComponent(new BidderListPanel(this.environment));
		splitPane.setLeftComponent(auctionContentPane);
		//splitPane.setDividerLocation(0.1);

	    frame.setContentPane(splitPane); 


	        
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
		

		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
	}
	
	private void changeToAuctionCreation() {
		System.out.println("Create new Auction triggered");
		CardLayout contentPaneLayout = (CardLayout)this.contentPane.getLayout();
		contentPaneLayout.show(contentPane, "ConfigPane");
		frame.revalidate();  // fresh
		
	}

}
