package auctionEngine;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

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

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import dataRepresentation.Agent;
import dataRepresentation.AuctionEnvironment;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

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
	
	private Agent createAgent(Document doc) {
    	NodeList bidderNode = doc.getElementsByTagName("agent");
    	Element agentElement = (Element) (bidderNode.item(0));
    	
		String agentName = agentElement.getAttribute("name");
		
		NodeList preferenceNode = agentElement.getElementsByTagName("preference");
		Element preferenceElement = (Element) (preferenceNode.item(0));
		
		NodeList strategyNode = agentElement.getElementsByTagName("strategy");
		Element strategyElement = (Element) strategyNode.item(0);
		String strategey = strategyElement.getAttribute("auctionType");
		
		NodeList parameterNodeList = strategyElement.getElementsByTagName("parameter");
		Element parameterElement1 = (Element) parameterNodeList.item(0);
		double s_a = Double.parseDouble(parameterElement1.getAttribute("value"));
		
		System.err.println("agent Name:"+ agentName);
		System.err.println("auction Type:" + strategey);
		System.err.println("sunk awareness parameter:" + s_a);
		
		Agent newAgent = new Agent(agentName, "not valid", null,  null, s_a);
		
		this.environment.bidderList.addBidder(newAgent);
		return null;
	}

}
