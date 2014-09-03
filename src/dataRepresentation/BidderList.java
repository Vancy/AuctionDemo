package dataRepresentation;

import java.awt.Color;
import java.util.ArrayList;

public class BidderList {
	
	public static ArrayList<Color> colorList;
	
	static{
		colorList = new ArrayList<Color>();
		colorList.add(Color.GREEN);
		colorList.add(Color.RED);
		colorList.add(Color.BLUE);
		colorList.add(Color.MAGENTA);
		colorList.add(Color.CYAN);
		colorList.add(Color.ORANGE);
		colorList.add(Color.PINK);
		colorList.add(Color.YELLOW);
		colorList.add(Color.DARK_GRAY);
		colorList.add(Color.LIGHT_GRAY);
		colorList.add(Color.WHITE);
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

}
