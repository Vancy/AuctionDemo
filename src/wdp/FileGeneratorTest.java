package wdp;

public class FileGeneratorTest {

	public static void main(String[] args) {
		/*
		 * Gnerating .mod file for AMPL
		 */
		
		//ModGenerator modGenerator = new ModGenerator(this.environment.context.getItemList());
		//modGenerator.generateFile();
		
		/*
		 * Gnerating .dat file for AMPL
		 */
		//ExecuteAMPLComand.getAnsFile();
		AnsParser ap = new AnsParser();
		ap.printResults();
		
	}

}
