package dataRepresentation;

import java.awt.Color;
import java.util.ArrayList;

public class BidderList {
	
	public static ArrayList<Color> colorList;
	
	private ArrayList<ArrayList<String>> lua_allAuction_valuationMsgs;
	
	static{
		colorList = new ArrayList<Color>();
		colorList.add(Color.WHITE);
		colorList.add(Color.GREEN);
		colorList.add(Color.RED);
		colorList.add(Color.CYAN);
		colorList.add(Color.ORANGE);
		colorList.add(Color.PINK);
		colorList.add(Color.YELLOW);
		colorList.add(Color.LIGHT_GRAY);
		colorList.add(Color.MAGENTA);
		colorList.add(new Color(210,240,120));
		colorList.add(new Color(139,214,58));
	}
	
	private ArrayList<Bidder> list;
	
	public BidderList() {
		this.list = new ArrayList<Bidder>();
	}
	
	public void addBidder(Bidder b) {
		this.list.add(b);
	}
	
	public ArrayList<Bidder> getList() {
		return this.list;
	}
	
	public int size() {
		return list.size();
	}
	
	public void setLuaValuationSetups(ArrayList<ArrayList<String>> msg) {
		this.lua_allAuction_valuationMsgs = msg;
	}
	
	public ArrayList<ArrayList<String>> getLuaValuationSetups() {
		return this.lua_allAuction_valuationMsgs;
	}
	
	public int getLuaValuationSetupNumber() {
		return this.lua_allAuction_valuationMsgs.size();
	}
	
	public void valuationMsgDistribution(int luaAuctionRound) {
		ArrayList<String> toDistribute = lua_allAuction_valuationMsgs.get(luaAuctionRound);
		for (int i=0; i<toDistribute.size(); i++) {
			if (this.list.size() < i+1) {
				System.out.println("the bidder number is smaller than distribution qouta, stop distribution.");
				return;
			}
			Bidder targetBidder = this.list.get(i);
			targetBidder.setValuationMsg(toDistribute.get(i));
		}
	}
		
	public Bidder getBidder(String name, String ip) {
		for (Bidder b: this.list) {
			if ((b.getName().equals(name)) && (b.getIP().equals(ip))) {
				return b;
			}
		}
		return null;
	}
	
	public String getBidderName(int id) {
		for (Bidder b: this.list) {
			if (b.getID() == id) {
				return b.getName();
			}
		}
		throw new RuntimeException("cannot find bidder id:" + id);
	}

}
