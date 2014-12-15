package wdp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import dataRepresentation.AuctionItem;


public class AnsParser {
	
	public static String ansFilePath = "C:\\AMPL\\";
	public static String ansFileName = "wdp.ans";
		
	private ArrayList<AuctionItem> items;
	private String content;
	
	private String tokenBuf = "";
	private int parseCursor = 0;
	private int tokenizerState = 0;
	
	public AnsParser(ArrayList<AuctionItem> itemlist) {
		this.items = itemlist;
	}
	
	public void getResult() {
		try {
			content = readFile(ansFilePath + ansFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		parse();
	}
	
	private void parse() {
		
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
	
	private Token nextToken() {

		int streamLength = this.content.length();
		this.tokenBuf = "";
		this.tokenizerState = 0;
		
		Pattern abcPattern = Pattern.compile("[A-Za-z]");
		Pattern bidderNamePattern = Pattern.compile("[0-9]");
		
		
		while(this.parseCursor != streamLength) {
			String nextChar = this.content.substring(this.parseCursor, this.parseCursor+1);
			this.parseCursor++;
//			if (nextChar.equals(" ") || nextChar.equals("\n") || nextChar.equals("\t")) {
//				continue;
//			}
						
			if (0 == tokenizerState) {
				if (nextChar.equals("=")) {
					return new Token(Token.equal, Token.equalToken); 
				} else if (nextChar.equals(",")) {
					return new Token(Token.comma, Token.commaToken); 
				} else if (nextChar.equals("*")) {
					return new Token(Token.star, Token.starToken); 
				} else if (nextChar.equals("[")) {
					return new Token(Token.leftB, Token.leftBToken); 
				} else if (nextChar.equals("]")) {
					return new Token(Token.rightB, Token.rightBToken); 
				} else if (nextChar.equals("x")) {
					return new Token(Token.x, Token.xToken); 
				} 
				
				////
				
				else if (nextChar.equals(":")) {
					tokenizerState = 1;
					//this.tokenBuf += nextChar;
				}
				else if (nextChar.equals("r")) {
					tokenizerState = 3;
					//this.tokenBuf += nextChar;
				}
				
				////
				
			}
			
			if (1 == tokenizerState) {
				if (nextChar.equals("=")) {
					return new Token(Token.assign, Token.assignToken); 
				} else {
					tokenizerState = 0;
					this.tokenBuf += nextChar;
					return new Token(Token.colon, Token.colonToken); 
				}
			}
			
			if (3 == tokenizerState) {
				if (nextChar.equals("e")) {
					tokenizerState = 4;
				} else {
					tokenizerState = 11;
					this.tokenBuf += nextChar;
					return new Token(Token.colon, Token.colonToken); 
				}
			}
			

			
			tokenizerState = 1;
			
			


		}
		return null;
	}
	
	class Token {
		
		public static final String revenue = "revenue";
		public static final String equal = "=";
		public static final String x = "x";
		public static final String leftB = "[";
		public static final String rightB = "]";
		public static final String star = "*";
		public static final String comma = ",";
		public static final String colon = ":";
		public static final String assign = ":=";
		
		public static final int revenueToken = 11;
		public static final int equalToken = 12;
		public static final int xToken = 13;
		public static final int leftBToken = 14;
		public static final int rightBToken = 15;
		public static final int starToken = 16;
		public static final int commaToken = 17;
		public static final int colonToken = 18;
		public static final int assignToken = 19;
		public static final int digitToken = 1;
		public static final int biddernameToken = 2;
		
		public String name;
		public int info;
		
		Token(String name, int seq) {
			this.name = name;
			this.info =  seq;
		}
	}
}
