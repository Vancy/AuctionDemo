package dataRepresentation;

import java.awt.Color;
import java.util.ArrayList;

public class BidderList {
	
	public static ArrayList<Color> colorList;
	
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
