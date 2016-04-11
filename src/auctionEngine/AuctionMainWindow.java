package auctionEngine;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import dataRepresentation.AuctionEnvironment;
import webServer.WebServer;

import java.awt.CardLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AuctionMainWindow {

	protected JFrame frame;
	
	private WebServer server;
	
    private JPanel auctionContentPane;
	private AuctionConfigPanel auctionConfigPane;
	protected AuctionPanel auctionPane;
	protected BidderListPanel bidderListPanel;

	AuctionEnvironment environment;
	
	/**
	 * Create the application.
	 */
	public AuctionMainWindow(AuctionEnvironment e, WebServer server) {
		this.environment = e;
		this.server = server;
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
		
		auctionContentPane = new JPanel();
	    //auctionContentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	    auctionContentPane.setLayout(new CardLayout());
	    this.auctionConfigPane = new AuctionConfigPanel(this, this.environment, this.frame);
	    this.auctionPane = new AuctionPanel(this.environment, this.frame);
	    this.auctionContentPane.add(auctionConfigPane, "ConfigPane"); 
	    this.auctionContentPane.add(auctionPane, "AuctionPane");
	    
	    
	    this.bidderListPanel = new BidderListPanel(this.environment, this.auctionPane);
	    
	    splitPane.setResizeWeight(0.99);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
		splitPane.setRightComponent(this.bidderListPanel);
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
				try {
					server.stopServer();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				frame.setVisible(false);
				frame.dispose();
				System.out.println("Quit auction engine");
				System.exit(0);
			}
		});
		mnStart.add(mntmQuit);
		
		//-----------------------------------------------------
		
		JMenu mnConfig = new JMenu("Config");
		menuBar.add(mnConfig);
		
		JMenuItem mntmLuaValuationSetting= new JMenuItem("LUA valuation setting");
		mnConfig.add(mntmLuaValuationSetting);
		mntmLuaValuationSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LuaValuationSettingDialog luaValuationSettingDialog = new LuaValuationSettingDialog(environment, bidderListPanel);
				luaValuationSettingDialog.setLocationByPlatform(true);
				luaValuationSettingDialog.setVisible(true);
			}
		});
		
		//-----------------------------------------------------

		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);
		
		JMenuItem mntmHelp = new JMenuItem("Help");
		mnAbout.add(mntmHelp);
		

		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
	}
	
	private void changeToAuctionCreation() {
		
		//clear previous round bidder winning message, if any.
		this.environment.bidderList.clearBiddersLuaWinningMsg();
				
		CardLayout contentPaneLayout = (CardLayout)this.auctionContentPane.getLayout();
		contentPaneLayout.show(auctionContentPane, "ConfigPane");
		frame.revalidate();  // fresh
		
	}

}
