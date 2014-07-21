package dataRepresentation;

import java.util.ArrayList;

public class AuctionContext {
	private int timeLimit_Sec;
	private ArrayList<AuctionItem> itemList;
	private String remark;
	
	public AuctionContext() {
		this.timeLimit_Sec = 60;
		this.itemList = new ArrayList<AuctionItem>();
		this.remark = "You can put any remark on this line";
	}
	
	public AuctionContext(int time, ArrayList<AuctionItem> list) {
		this.timeLimit_Sec = time;
		this.itemList = list;
		this.remark = "You can put any remark on this line";
	}
}
