package wdp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class AnsParser {
	
	public static void main(String[] args) {
		ExecuteAMPLComand.getAnsFile();
		AnsParser ansParser = new AnsParser();
		System.out.println("Max revenue:" + ansParser.getResult());
		
	}
	
	public static String ansFilePath = "C:\\AMPL\\";
	public static String ansFileName = "wdp.ans";
		
	private String content;
	
	private ArrayList<String> results = new ArrayList<String>();
	private double revenue = 0;
	
	public AnsParser() {
	}
	
	public double getResult() {
		try {
			content = readFile(ansFilePath + ansFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		parse();
		return this.revenue;
	}
	
	public ArrayList<String> getWinningBids() {
		return this.results;
	}
	
	private static String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}
		
	private void parse() {
		
		//substitute all line breakers and tabs to normal spaces
		this.content = this.content.replaceAll("\n", " ");
		this.content = this.content.replaceAll("\t", " ");
		StringTokenizer st = new StringTokenizer(this.content, " ");
		ArrayList<String> tokenList = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			tokenList.add(st.nextToken());
		}
		// logic processing
		this.revenue = Double.parseDouble(tokenList.get(2));
	    // get winning packages
		int index = 4; //first patternVector from 4th
		while(index < tokenList.size() && !tokenList.get(index).equals(Token.semicolon)) {
			if(! tokenList.get(index).startsWith(Token.leftB)) {
				throw new AnsParsingException("Get error when parsing ans file, expect [");
			} else {
				String patternVector = tokenList.get(index++);
				//System.out.println("pattern"+patternVector);
				ArrayList<String> secondStarVector = new ArrayList<String>();
				index++; // skip next ":"
				while (!tokenList.get(index).equals(Token.assign) && index < tokenList.size()) {
					secondStarVector.add(tokenList.get(index));
					//System.out.println("add second Token:"+ tokenList.get(index));
					index++;
				}
				index++; // skip next :=
				while (!tokenList.get(index).startsWith(Token.leftB) && !tokenList.get(index).equals(Token.semicolon) && index < tokenList.size()) {
					String firstStarToken = tokenList.get(index++);
					//System.out.println("firstToken:"+ firstStarToken);
					for (int i=0; i<secondStarVector.size(); i++) {
						String secondStarToken = secondStarVector.get(i);
						if (tokenList.get(index+i).equals(Token.one)) {
							String result = patternVector.replaceFirst("\\*", firstStarToken).replaceFirst("\\*", secondStarToken);
							System.out.println(result + "  first:" + firstStarToken + " sec:" + secondStarToken);
							this.results.add(result);
						}
					}
					index += secondStarVector.size();
				}
			}
		}
	}
	
	class Token {
		
		public static final String revenue = "revenue";
		public static final String equal = "=";
		public static final String x = "x";
		public static final String leftB = "[";
		public static final String rightB = "]";
		public static final String star = "*";
		public static final String comma = ",";
		public static final String semicolon = ";";
		public static final String colon = ":";
		public static final String assign = ":=";
		public static final String one = "1";
		public static final String zero = "0";

	}
	
	class AnsParsingException extends RuntimeException {
		private static final long serialVersionUID = -638645498146454664L;
		public AnsParsingException(String s) {
			super(s);
		}
	}
}
