package auctionEngine;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class WarningDialog extends JDialog{
	
	private static final long serialVersionUID = 1L;

	public WarningDialog(JDialog parent, String title, String msg) {
		super(parent, title, true);
		// add label to center
		add(new JLabel(msg),BorderLayout.CENTER);
		//OK button closes the dialog
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				setVisible(false);
			}
		});

      // add Ok button to southern border

		JPanel panel = new JPanel();
		panel.add(ok);
		add(panel, BorderLayout.SOUTH);
		pack();
		this.setLocationRelativeTo(parent);

   }
   
   public void setMessage() {
	   
   }
}