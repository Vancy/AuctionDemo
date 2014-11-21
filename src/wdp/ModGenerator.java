package wdp;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

import dataRepresentation.AuctionItem;

public class ModGenerator {
	private String content = new String();
	private List<String> itemSymbols;

	/////////////////////////////////////////////////////////////
	public ModGenerator(List<AuctionItem> list) {
		this.itemSymbols = new ArrayList<String>();
		for (int i=0; i<list.size(); i++) {
			this.itemSymbols.add(list.get(i).getName());
		}
	}
	public void generateFile() {
		generateContent();
		File file = new File("./wdp.mod");
		FileParser.writeToFile(file, this.content);
	}
	private void generateContent() {
		content += printSetOfBidders();
		content += printItemSets();
		content += printCombinations();
		content += printLimititionSets();
		content += printParamBid();
		content += printParamSizeN();
		content += printParamNumOfItems();
		content += printVarX();
		content += printRevenue();
		content += printSubjectAtMostOne();
		content += printSubjectItemUnitLimitations();
	}

	/////////////////////////////////////////////////////////////
	private String printSetOfBidders() {
		return "set N;\n";
	}
	private String printItemSets() {
		String ret = "";
		for (String item: this.itemSymbols) {
			ret += "set " + item + "; ";
		}
		return ret+"\n";
	}
	private String printCombinations() {
		String ret = "set Combinations within {N, " + itemSetsStr() + "\n";
		return ret;
		
	}
	private String printLimititionSets() {
		
		String ret = "";
		for (int i=0; i<this.itemSymbols.size(); i++) {
			ret += "set Limit_" + i + " within {N," + itemSetsStr() + "\n";
		}
		return ret+"\n";
	}
	
	private String printParamBid() {
		return "param bid {Combinations};\n";
	}
	private String printParamSizeN() {
		String ret = "";
		for (String item: this.itemSymbols) {
			ret += "param size_" + item + " > 0;\n";
		}
		return ret+"\n";
	}
	private String printParamNumOfItems() {
		String ret = "";
		for (int i=0; i<this.itemSymbols.size(); i++) {
			ret += "param number_of_bid_units_of_item_" + i + " {Limit_" + i + "};\n";
		}
		return ret+"\n";
	}
	private String printVarX() {
		return "var x {N," + itemSetsStr() + " binary;\n";
	}
	private String printRevenue() {
		String ret = "maximize revenue:\n\t\t";
		ret += "sum{(j," + itemSetsVar() + 
				") in Combinations}  bid[j," + 
				itemSetsVar() + "] * x[j," +
				itemSetsVar() + "];";
		return ret+"\n";
	}
	private String printSubjectAtMostOne() {
		String ret = "subject to at_most_one_package {j in N}:\n\t\t";
		ret += "sum{(j," + itemSetsVar() + 
				") in Combinations}  x[j," + 
				itemSetsVar() + "] <=1;";
		return ret+"\n";
	}
	private String printSubjectItemUnitLimitations() {
		String ret = "";
		for (int i=0; i<this.itemSymbols.size(); i++) {
			ret += "subject to item_unit_limit_" + i + ":\n\t\t";
			ret += "sum{(j," + itemSetsVar()
					+ ") in Limit_"+ i + "} number_of_bid_units_of_item_"
					+ i + "[j," + itemSetsVar() + "] " 
					+ "* x[j," + itemSetsVar()
					+ "<= size_" + this.itemSymbols.get(i) + ";";
		}
		return ret;
	}
	////////////////////////////////////////////////
	private String itemSetsStr() {
		String itemSetsStr = "";
		// should be: A1, A2, A3, A4};
		for (int i=0; i<this.itemSymbols.size(); i++) {
			itemSetsStr += this.itemSymbols;
			if (i != this.itemSymbols.size()-1) {
				itemSetsStr += ", ";
			}
			else {
				itemSetsStr += "};";
			}
		}
		return itemSetsStr;
	}
	private String itemSetsVar() {
		String itemSetsStr = "";
		// should be: a1, a2, a3, a4};
		for (int i=0; i<this.itemSymbols.size(); i++) {
			itemSetsStr += "a" + i;
			if (i != this.itemSymbols.size()-1) {
				itemSetsStr += ", ";
			}
		}
		return itemSetsStr;
	}
}
