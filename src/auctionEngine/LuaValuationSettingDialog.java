package auctionEngine;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class LuaValuationSettingDialog extends JDialog{
	
	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
	private JTextField filePathTextField;
	private JFileChooser configFileChooser; 
	
	public LuaValuationSettingDialog() {
		
		this.setTitle("Setting LUA bidding valuations");
		
		configFileChooser = new JFileChooser();
		configFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Config Excel File","csv", "xlsx", "xlm");
		configFileChooser.setFileFilter(filter);
		configFileChooser.setDialogTitle("Select LUA bidding valuation setup File");  

  
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		JLabel lblConfigFile = new JLabel("Setup File:");
		contentPanel.add(lblConfigFile);
		
		
		filePathTextField = new JTextField();
		contentPanel.add(filePathTextField);
		filePathTextField.setColumns(20);
		

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
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parseSetupFile(new File(filePathTextField.getText()));
				System.err.println("Successfully parsed file");
				dispose();
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
		
	}
	
	private void parseSetupFile(File file) {
		// TODO Auto-generated method stub
		
	}
}
