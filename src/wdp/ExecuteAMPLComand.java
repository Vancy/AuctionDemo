package wdp;

import java.io.File;
import java.io.IOException;

 
public class ExecuteAMPLComand {
	
	private static String coreCmd = "ampl.exe";
	public static String amplFolderPath = "C:\\AMPL\\";
	public static String wdpRunFile = "wdp.run";
 

	public static void getAnsFile() {
		deleteOldFiles();
		String command = ".\\" + coreCmd + " " + wdpRunFile;
		String directory = amplFolderPath;
		System.out.println("execute command:" + command);
		executeCommand(directory, command);
	}
	
	private static void deleteOldFiles() {
		try{
    		File oldansfile = new File(amplFolderPath + "wdp.ans");
    		oldansfile.delete();
    	}
		catch(Exception e){
    		e.printStackTrace();
    	}
	}
 
	private static void executeCommand(String directory, String command) {
 
		String[] cmds = new String[] {"cmd", "/c", command};
		Thread execThread = new Thread("executing thread") {
			public void run(){
				try {
					Process p = Runtime.getRuntime().exec(cmds, null, new File(directory));
					while ( !Thread.currentThread().isInterrupted()) ;
					p.destroy();
				} catch (IOException e) {
					e.printStackTrace();
				}
		     }
		};
		execThread.start();
		File ansFile = new File(amplFolderPath + "wdp.ans");
		while( !ansFile.exists() );
		execThread.interrupt();
	}
 
}