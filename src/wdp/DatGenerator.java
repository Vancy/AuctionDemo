package wdp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dataRepresentation.AuctionItem;
import dataRepresentation.Bidder;
import dataRepresentation.CCABiddingPackage;

public class DatGenerator {
	private String content = new String();
	private List<Bidder> bidderList;
	private List<CCABiddingPackage> packageList;
	private List<AuctionItem> itemList;
	private List<String> itemSymbols;

	/////////////////////////////////////////////////////////////
	public DatGenerator(List<Bidder> bidderList, List<CCABiddingPackage> packageList, List<AuctionItem> itemList) {
		this.itemSymbols = new ArrayList<String>();
		this.bidderList = bidderList;
		this.packageList = packageList;
		this.itemList = itemList;
		for (int i=0; i<itemList.size(); i++) {
			this.itemSymbols.add(itemList.get(i).getName());
		}
	}
	
	public void generateFile() {
		generateContent();
		File file = new File("./wdp.dat");
		FileParser.writeToFile(file, this.content);
	}
	
	private void generateContent() {
		content += printBidderSet();
		content += printItemNumSets();
		content += printParamItemNum();
		content += printParamCombinations();
		content += printParamLimitations();
	}
	
	
	private String printBidderSet() {
		String ret = "set N:= ";
		for(Bidder bidder: this.bidderList){
			String bidderID = "B" + bidder.getID() + " ";
			ret += bidderID;
		}
		ret += ";\n";
		return ret;
	}
	
	private String printItemNumSets() {
		String ret = "";
		for(AuctionItem item: this.itemList){
			String itemName = item.getName();
			String s = "set " + itemName + ":= ";
			for(int i=0; i<=item.getQuantity(); i++) {
				s += " " + i;
			}
			s += ";\n";
			ret += s;
		}
		ret += "\n";
		return ret;
	}
	
	private String printParamItemNum() {
		String ret = "";
		for(AuctionItem item: this.itemList){
			String itemName = item.getName();
			String s = "param size_" + itemName + ":= ";
			s += "" + item.getQuantity() + ";\n";
			ret += s;
		}
		ret += "\n";
		return ret;
	}
	
	private String printParamCombinations() {
		String ret = "param: Combinations: bid :=\n";
		for(CCABiddingPackage pkg: this.packageList){
			ret += "\t" + printSinglePackage(pkg);
			ret += "\n";
		}
		ret += ";\n";
		return ret;
	}
	
	private String printParamLimitations() {
		String ret = "";
		for (AuctionItem item: this.itemList) {
			ret += printParamLimitation(item.getID());
		}
		return ret;
	}
	///////////////////////////////////////////////////////////////////////////////////////
	private String printParamLimitation(int itemID) {
		String ret = "param: Limit_"+ itemID + ": number_of_bid_units_of_item_" + itemID + ":=\n";
		for(CCABiddingPackage pkg: this.packageList){
			ret += printSinglePackage(pkg);
			String reqPkg = getRequiredQuantityOfItemID(pkg, itemID);
			ret += " " + reqPkg + "\n";
		}
		ret += ";\n";
		return ret;
	}
	
	private String printSinglePackage(CCABiddingPackage pkg) {
		String BidderID = "B" + pkg.getBidder().getID() + " ";
		String s = BidderID;
		String pkgPrice = "" + pkg.getPrice();
		for (AuctionItem item: pkg.getItemList()) {
			s += item.getRequiredQuantity() + " ";
		}
		s += pkgPrice;
		return s;
	}
	
	private String getRequiredQuantityOfItemID(CCABiddingPackage pkg, int itemID) {
		int reqOfThisPkg = 0;
		for(AuctionItem item: pkg.getItemList()) {
			if (item.getID() == itemID) {
				reqOfThisPkg = item.getRequiredQuantity();
			} 
		}
		
		return ""+reqOfThisPkg;
	}
}
