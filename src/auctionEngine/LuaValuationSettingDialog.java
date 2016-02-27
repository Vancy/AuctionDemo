package auctionEngine;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class LuaValuationSettingDialog extends JDialog{
	
	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
	private JTextField filePathTextField;
	private JFileChooser configFileChooser; 
	
	public LuaValuationSettingDialog() {
		
		this.setTitle("Setting LUA bidding valuations");
		
		configFileChooser = new JFileChooser();
		configFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Config Excel File","csv", "xlsx", "xls");
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
		FileInputStream fileInputStream;
		HSSFWorkbook workbook = null;
		try {
			fileInputStream = new FileInputStream(file);
			workbook = new HSSFWorkbook(fileInputStream);
		} catch (IOException e) {
			WarningDialog dialog = new WarningDialog(this, "There is an error opening excel file:" + file.getName(), e.getMessage());
            dialog.setVisible(true); // pop up warning dialog
		}

		HSSFSheet worksheet = workbook.getSheet("sheet1");
		int bidderNumber = (int)worksheet.getRow(2).getCell(2).getNumericCellValue();
		int itemNumber = (int)worksheet.getRow(3).getCell(2).getNumericCellValue();
		int auctionNumber = (int)worksheet.getRow(4).getCell(2).getNumericCellValue();
		
		ArrayList<ArrayList<String>> allAuction_valuationMsgs = new ArrayList<ArrayList<String>>();
		
		int rowCursor = 6; //the real data starts from 6th row.
		final int rowGap = 4; //between every two auctions, the gap is 4.
		final int colGap = 3; //each item consume 3 columns.
		for(int an=0; an<auctionNumber; an++) {
			//Store valuations of this auction.
			ArrayList<String> thisAuction_valuationMsg = new ArrayList<String>();
			//Store item names into a vector.
			ArrayList<String> itemNames = new ArrayList<String>();
			final int startcol = 3;
			for(int in=0; in<itemNumber; in++) {
				itemNames.add(in, worksheet.getRow(rowCursor).getCell(startcol+colGap*in).getStringCellValue());
			}
			rowCursor+=2;
			for(int bn=0; bn<bidderNumber; bn++) {
				StringBuffer valuations = new StringBuffer();
				for(int in=0; in<itemNumber; in++) {
					String licenced = worksheet.getRow(rowCursor).getCell(startcol+colGap*in).getStringCellValue();
					String unlicenced = worksheet.getRow(rowCursor).getCell(startcol+colGap*in+1).getStringCellValue();
					valuations.append(itemNames.get(in)+":");
					valuations.append(licenced.isEmpty()?"NA":licenced);
					valuations.append("(L)");
					valuations.append(unlicenced.isEmpty()?"NA":unlicenced);
					valuations.append("(U)");
					valuations.append("\n");
					thisAuction_valuationMsg.add(valuations.toString());
					rowCursor++;
				}
			}
			rowCursor+=rowGap;
			allAuction_valuationMsgs.add(thisAuction_valuationMsg); 
		}
		
	}
}
