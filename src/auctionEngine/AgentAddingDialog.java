package auctionEngine;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import dataRepresentation.Agent;
import dataRepresentation.AuctionEnvironment;
import dataRepresentation.AuctionItem;
import dataRepresentation.CCAAgent;
import dataRepresentation.SAAAgent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentAddingDialog extends JDialog {

	private static final long serialVersionUID = -3516059279738106067L;
	
	private AuctionEnvironment environment;
	
	private final JPanel contentPanel = new JPanel();
	private JTextField filePathTextField;
	private JFileChooser configFileChooser;  


	public AgentAddingDialog(AuctionEnvironment environment) {
		
		this.environment = environment;
		
		this.setTitle("Adding Agents");
		
		configFileChooser = new JFileChooser();
		configFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Config XML File","xml", "txt");
		configFileChooser.setFileFilter(filter);
		configFileChooser.setDialogTitle("Select Agent Configuration File");  

  

		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JLabel lblConfigFile = new JLabel("Config File:");
			contentPanel.add(lblConfigFile);
		}
		{
			filePathTextField = new JTextField();
			contentPanel.add(filePathTextField);
			filePathTextField.setColumns(20);
		}
		{
			JButton btnBrowse = new JButton("Browse");
			btnBrowse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					 int returnValue = configFileChooser.showOpenDialog(null) ;
					    if( returnValue == JFileChooser.FILES_ONLY ) {
					        File file = configFileChooser.getSelectedFile();
					        filePathTextField.setText(file.getPath());
					    } else {
					    	filePathTextField.setText("Not Valid");
					    }
				}
			});
			contentPanel.add(btnBrowse);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						parseConfigFile(new File(filePathTextField.getText()));
						System.err.println("Successfully parsed file");
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	private Document parseConfigFile(File file) {
		File xmlFile = new File(this.filePathTextField.getText());
		xmlFile.setReadOnly();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

		Document doc = null;
		try {
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder(); 
			doc = dBuilder.parse(xmlFile);
		} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
		}
		createAgent(doc);
		return doc;
	}
	
	private void createAgent(Document doc) {
    	NodeList bidderNode = doc.getElementsByTagName("agent");
    	//Adding all agents in config file	
    	for (int agentID=0; agentID<bidderNode.getLength(); agentID++) {
    		Element agentElement = (Element) (bidderNode.item(agentID));
    		String agentName = agentElement.getAttribute("name");
    		
    		NodeList preferenceNode = agentElement.getElementsByTagName("preference");
    		Element preferenceElement = (Element) (preferenceNode.item(0));
    		
      		NodeList packageList = preferenceElement.getElementsByTagName("package");
    		Element packageElement = null;
    		HashMap<List<AuctionItem>, Double> preference = new HashMap<List<AuctionItem>, Double>();
    		for (int i=0; i<packageList.getLength(); i++) {
    			double packageValuation = 0;
    			ArrayList<AuctionItem> itemList = new ArrayList<AuctionItem>();
    			
    			packageElement = (Element) packageList.item(i);
    			packageValuation = Double.parseDouble(packageElement.getAttribute("valuation"));
    			
    			NodeList itemNodeList = packageElement.getElementsByTagName("item");
    			Element itemElement = null;
    			for (int j=0; j<itemNodeList.getLength(); j++) {
    				itemElement = (Element) itemNodeList.item(j);
    				int itemID = Integer.parseInt(itemElement.getAttribute("id"));
    				itemList.add(new AuctionItem(this.environment.context.searchItem(itemID)));
    			}
    			
    			preference.put(itemList, packageValuation);
    		}
    		
    		NodeList strategyNode = agentElement.getElementsByTagName("strategy");
    		Element strategyElement = (Element) (strategyNode.item(0));
    		String strategyName = strategyElement.getAttribute("auctionType");
    		if (strategyName.equals("SAA")) {
    			createAgentSAA(agentName, preference, strategyNode);
    		} else if (strategyName.equals("CCA")) {
    			createAgentCCA(agentName, preference, strategyNode);
    		}
    	}
		
	}
	
	private void createAgentSAA(String agentName, Map<List<AuctionItem>, Double> preference, NodeList strategyNodes) {
		
		Element strategyElement = (Element) strategyNodes.item(0);
		NodeList parameterNodeList = strategyElement.getElementsByTagName("parameter");
		Element parameterElement1 = (Element) parameterNodeList.item(0);
		double s_a = Double.parseDouble(parameterElement1.getAttribute("value"));
		
		System.err.println("agent Name:"+ agentName);
		System.err.println("auction Type: SSA");
		System.err.println("sunk awareness parameter:" + s_a);
		
		SAAAgent newAgent = new SAAAgent(agentName, "SSA", environment.context.getItemList(), preference, s_a);
		this.environment.bidderList.addBidder(newAgent);
		
	}
	
	private void createAgentCCA(String agentName, Map<List<AuctionItem>, Double> preference, NodeList strategyNodes) {
		
		HashMap<Integer, ArrayList<Double>> demandVectors = new HashMap<Integer, ArrayList<Double>>();
		Element strategyElement = (Element) strategyNodes.item(0);
		NodeList parameterNodeList = strategyElement.getElementsByTagName("parameter");
		for (int j=0; j<parameterNodeList.getLength(); j++) {
			Element parameterElement = (Element) parameterNodeList.item(j);
			String demandVectorString = parameterElement.getAttribute("value");
			String[] strNumbers = demandVectorString.split(",");
			ArrayList<Double> demandVector = new ArrayList<Double>();
			for (int i=0; i<strNumbers.length; i++) {
				demandVector.add(i, Double.parseDouble(strNumbers[i]));
			}
			
			NodeList itemNodeList = parameterElement.getElementsByTagName("item");
			for (int oliver=0; oliver<itemNodeList.getLength(); oliver++) {
				Element itemElement = (Element) itemNodeList.item(oliver);
				int itemID = Integer.parseInt(itemElement.getAttribute("id"));
				demandVectors.put(itemID, demandVector);
				
			}
			
		}
		
		CCAAgent newAgent = new CCAAgent(agentName, "CCA", environment.context.getItemList(), preference, demandVectors);
		this.environment.bidderList.addBidder(newAgent);
	}

}
