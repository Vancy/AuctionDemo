package dataRepresentation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;


public class LUAItemWinningResult {
		public enum WinnerType {LicencedWin, UnlicencedWin};
		private int itemID;
		private String itemName;
		private double L_winner_price = 0;
		private int L_winner_bidder_id = 0;
		private double U_Sum = 0;
		private HashMap<Integer, Double> U_bidder_prices = new HashMap<Integer, Double>();
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
				this.U_bidder_prices.put(bidderID, unlicenced);
			}
		}
		public int getItemID() {
			return this.itemID;
		}
		public String getItemName() {
			return this.itemName;
		}
		
		public WinnerType getWinnerType() {
			if(this.L_winner_price >= this.U_Sum) {
				return WinnerType.LicencedWin;
			} else {
				return WinnerType.UnlicencedWin;
			}
		}
		
		public double getWinnerPrice() {
			if(this.L_winner_price >= this.U_Sum) {
				return this.L_winner_price;
			} else {
				return this.U_Sum;
			}
		}
		public double getWinnerLprice() {
			return this.L_winner_price;
		}
		
		public double getUnlicencedPriceSum() {
			return this.U_Sum;
		}
		
		public double getSecondHighestPrice() {
			if (this.L_winner_price < this.U_Sum) {
				return this.L_winner_price;
			}
			if (this.L_secondHighest_price > this.U_Sum) {
				return this.L_secondHighest_price;
			} else {
				return this.U_Sum;
			}
		}
		
		public String getWinnerDistributionResult() {
			StringBuffer result = new StringBuffer();
			double payPrice = this.getSecondHighestPrice();
			if (WinnerType.LicencedWin == this.getWinnerType()) {
				result.append(this.L_winner_bidder_id);
				result.append("(");
				result.append(payPrice);
				result.append(")");
			} else if (WinnerType.UnlicencedWin == this.getWinnerType()){
				for (int bidderID: this.U_bidder_prices.keySet()) {
					result.append(bidderID);
					result.append("(");
					double winPriceRaw  = new Double(payPrice * this.U_bidder_prices.get(bidderID) / this.U_Sum);
					double winPrice = new BigDecimal(winPriceRaw)
				    						.setScale(2, BigDecimal.ROUND_HALF_UP)
				    						.doubleValue(); //set float precision as 2, and using round half up
					result.append(winPrice);
					result.append(")");
				}
			}
			return result.toString();
		}
}
