package dataRepresentation;

import java.util.HashSet;


public class LUAItemWinningResult {
		public enum WinnerType {LicencedWin, UnlicencedWin};
		private int itemID;
		private String itemName;
		private double L_winner_price = 0;
		private int L_winner_bidder_id = 0;
		private double U_Sum = 0;
		private HashSet<Integer> U_bidders = new HashSet<Integer>();
		private WinnerType winnerType;
		private double L_secondHighest_price = 0;
		
		public LUAItemWinningResult(int id, String name) {
			this.itemID = id;
			this.itemName = name;
		}
		
		public void updateBidInfo(int bidderID, LuaBid bid) {
			double licenced = bid.getLicencedBidPrice();
			double unlicenced = bid.getUnlicencedBidPrice();
			if(licenced > this.L_winner_price) {
				this.L_secondHighest_price = this.L_winner_price;
				this.L_winner_price = licenced; 
				this.L_winner_bidder_id = bidderID;
			}
			if(unlicenced > 0) {
				this.U_Sum += unlicenced;
				this.U_bidders.add(bidderID);
			}
		}
		
		public String getItemName() {
			return this.itemName;
		}
		
		public WinnerType processWinner() {
			if(this.L_winner_price >= this.U_Sum) {
				return this.winnerType = WinnerType.LicencedWin;
			} else {
				return this.winnerType = WinnerType.UnlicencedWin;
			}
		}
}
