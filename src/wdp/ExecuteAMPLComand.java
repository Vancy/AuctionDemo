package wdp;
 
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
 
public class ExecuteAMPLComand {
	
	private static String coreCmd = "ampl.exe";
	public static String amplFolderPath = "C:\\AMPL\\";
	public static String wdpRunFile = "wdp.run";
 
	public static void main(String[] args) {
		getAnsFile();
	}
	
	public static File getAnsFile() {
		deleteOldFiles();
		File ansFile;
		String command = "cd " + amplFolderPath + " ; " + " .\\" + coreCmd + " " + wdpRunFile;
		System.out.println(command);
		System.out.println(executeCommand(command));
		ansFile = new File(amplFolderPath + "wdp.ans");
		return ansFile;
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
 
	private static String executeCommand(String command) {
 
		StringBuffer output = new StringBuffer();
		Process p;
		
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString(); 
	}
 
}