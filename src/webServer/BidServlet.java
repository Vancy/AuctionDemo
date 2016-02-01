package webServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dataRepresentation.AuctionContext;
import dataRepresentation.AuctionEnvironment;
import dataRepresentation.AuctionItem;
import dataRepresentation.Bid;
import dataRepresentation.Bidder;
import dataRepresentation.CCABiddingPackage;
import dataRepresentation.LuaBid;

public class BidServlet extends DefaultServlet{

	private static final long serialVersionUID = 2483175565395222277L;
	
	private AuctionEnvironment auctionEnvironment = null;
	
	public BidServlet(AuctionEnvironment ae) {
		auctionEnvironment = ae;
	}

	public void init(){

	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				
		// read Post content by request.getReader(). store content into bufferedReader
		BufferedReader reader = request.getReader();
		StringBuilder builder = new StringBuilder();
		String aux = "";
		while ((aux = reader.readLine()) != null) {
		    builder.append(aux);
		}
		String xmlContent = builder.toString();
		//Document doc = convertStringToDocument(xmlContent);
		JsonObject doc = convertStringToJson(xmlContent);
		
		System.out.println(doc.toString());
		//place a bid to environment, auctioneer will handle this bid
		Bid myBid = placeBid(doc);
		if (null != myBid) {
			System.err.println(myBid.getBidder().getName()+"**********Get bid request*********");
		}		
		//Respond needn't be read by clients 
		PrintWriter out = response.getWriter();
		out.println(this.auctionEnvironment.context.generateJson());
	}
	
//    private static Document convertStringToDocument(String xmlStr) {
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
//        DocumentBuilder builder; 
//        try 
//        { 
//            builder = factory.newDocumentBuilder(); 
//            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) );
//            return doc;
//        } catch (Exception e) { 
//            e.printStackTrace(); 
//        }
//        return null;
//    }

//    private Bid placeBid(Document doc) {
//    	NodeList bidderNode = doc.getElementsByTagName("bidder");
//    	Element bidderInfo = (Element) (bidderNode.item(0));
//    	String name = bidderInfo.getAttribute("name");
//    	String ip = bidderInfo.getAttribute("ip");
//    	
//    	Bidder bidder = this.auctionEnvironment.bidderList.getBidder(name, ip);
//    	if (null == bidder) {
//    		try {
//				throw new Exception("BidServlet: Cannot find bidder inside bidderlist");
//			} catch (Exception e) {
//				e.printStackTrace();
//				return null;
//			}
//    	}
//    	
//    	List<AuctionItem> bidderItemList = new ArrayList<AuctionItem>();
//    	NodeList itemList = doc.getElementsByTagName("item");
//    	/*
//    	 * Check current auction type, SAA or CCA.
//    	 */
//    	if  (this.auctionEnvironment.context.getType() == AuctionContext.AuctionType.SAA) {
//        	for (int i=0; i<itemList.getLength(); i++) {
//        		Node currentNode = itemList.item(i);
//        		Element element = (Element) currentNode;
//        		String itemName = element.getAttribute("name");
//        		double itemPrice = Double.parseDouble(element.getAttribute("price"));
//        		int id = Integer.parseInt(element.getAttribute("id"));
//        		bidderItemList.add(new AuctionItem(id, itemName, itemPrice));
//        	}
//    	} else  if (this.auctionEnvironment.context.getType() == AuctionContext.AuctionType.CCA) {
//        	for (int i=0; i<itemList.getLength(); i++) {
//        		Node currentNode = itemList.item(i);
//        		Element element = (Element) currentNode;
//        		String itemName = element.getAttribute("name");
//        		int itemRequire = 0;
//        		try { //if bidder doesn't bid for this, the quantity_require may not be defined
//        			itemRequire = Integer.parseInt(element.getAttribute("quantity_require"));
//        		} catch(RuntimeException e) { // so put itemRequire as 0 
//        			itemRequire = 0;
//        		}
//        		int id = Integer.parseInt(element.getAttribute("id"));
//        		bidderItemList.add(new AuctionItem(id, itemName, itemRequire));
//        	}
//    	}
//
//
//    	Bid bid = new Bid(bidder, bidderItemList);
//    	bidder.placeBid(this.auctionEnvironment, bid);
//    	return bid;
//    }
    
    private static JsonObject convertStringToJson(String jsonStr) {
    	JsonParser parser = new JsonParser();
    	System.out.println("-->" + jsonStr);
    	JsonObject jsonObj = (JsonObject)parser.parse(jsonStr);
    	return jsonObj;
    }
    
    private Bid placeBid(JsonObject json) {
    	
    	JsonObject jsonBid = json.get("bid").getAsJsonObject();
    	JsonObject jsonBidder = jsonBid.get("bidder").getAsJsonObject();
    	String name = jsonBidder.get("name").getAsString();
    	String ip = jsonBidder.get("ip").getAsString();
    	
    	Bidder bidder = this.auctionEnvironment.bidderList.getBidder(name, ip);
    	if (null == bidder) {
				throw new RuntimeException("BidServlet: Cannot find bidder inside bidderlist");
    	}
    	
    	if (null != jsonBid.get("luaBidPackage")) {
    		this.auctionEnvironment.auctioneer.getLuaBidPackage(bidder, getLuaPackageBid(jsonBid, bidder));
    		return null;
    	}
    	
    	/*
    	 * If this bid contain key words "packageList", this bid is
    	 * supplymentary round of CCA, so process it as CCA supplymentary round bid.
    	 */
    	if (null != jsonBid.get("packageList")) {
    		
    		this.auctionEnvironment.auctioneer.getSupplementaryRoundBid(bidder, getCcaPackageBid(jsonBid, bidder));
    		return null;
    	}
    	/*
    	 * else this bid is first stage bidding, so process it as first stage bidding(SAA or CCA)
    	 */
    	List<AuctionItem> bidderItemList = new ArrayList<AuctionItem>();
    	JsonArray itemList = jsonBid.get("itemList").getAsJsonArray();
    	/*
    	 * Check current auction type, SAA or CCA.
    	 */
    	if  (this.auctionEnvironment.context.getType() == AuctionContext.AuctionType.SAA) {
    		for (int i=0; i<itemList.size(); i++) {
    			JsonObject item = itemList.get(i).getAsJsonObject();
    			String itemName = item.get("name").getAsString();
    			double itemPrice = item.get("price").getAsDouble();
    			int id  = item.get("ID").getAsInt();
    			System.out.println("name:"+itemName+"price:"+ itemPrice);
    			bidderItemList.add(new AuctionItem(id, itemName, itemPrice));
    		}
    	} else  if (this.auctionEnvironment.context.getType() == AuctionContext.AuctionType.CCA) {
    		for (int i=0; i<itemList.size(); i++) {
    			JsonObject item = itemList.get(i).getAsJsonObject();
    			String itemName = item.get("name").getAsString();
    			int itemRequire = 0;
    			try { //if bidder doesn't bid for this, the quantity_require may not be defined
        			itemRequire = item.get("quantity_required").getAsInt();
        		} catch(RuntimeException e) { // so put itemRequire as 0 
        			itemRequire = 0;
        		}
    			int id  = item.get("ID").getAsInt();
    			bidderItemList.add(new AuctionItem(id, itemName, itemRequire));
    		}
    	}

    	Bid bid = new Bid(bidder, bidderItemList);
    	bidder.placeBid(this.auctionEnvironment, bid);
    	return bid;
    }
    
	public void destroy() {
		// do nothing.
	}
	
	private ArrayList<CCABiddingPackage> getCcaPackageBid(JsonObject jsonBid, Bidder bidder) {
		JsonArray packageList = jsonBid.get("packageList").getAsJsonArray();
		ArrayList<CCABiddingPackage> packages= new ArrayList<CCABiddingPackage>();
		for (int i=0; i<packageList.size(); i++) {
			JsonArray combination = packageList.get(i).getAsJsonObject().get("combination").getAsJsonArray();
			double price = packageList.get(i).getAsJsonObject().get("price").getAsDouble();
			ArrayList<AuctionItem> itemList = new ArrayList<AuctionItem>();
			for (int j=0; j<combination.size(); j++) {
				int id = combination.get(j).getAsJsonObject().get("ID").getAsInt();
				String name = combination.get(j).getAsJsonObject().get("name").getAsString();
				int require = combination.get(j).getAsJsonObject().get("amount").getAsInt();
				AuctionItem item = new AuctionItem(id, name, require);
				itemList.add(item);
			}
			CCABiddingPackage thisPkg = new CCABiddingPackage(bidder, price, itemList);
			packages.add(thisPkg);
		}
		return packages;
	}
	
	private ArrayList<LuaBid> getLuaPackageBid(JsonObject jsonBid, Bidder bidder) {
		JsonArray packageList = jsonBid.get("luaBidPackage").getAsJsonArray();
		ArrayList<LuaBid> bids = new ArrayList<LuaBid>();
		for (int i=0; i<packageList.size(); i++) {
			int id = packageList.get(i).getAsJsonObject().get("item").getAsJsonObject().get("ID").getAsInt();
			String name = packageList.get(i).getAsJsonObject().get("item").getAsJsonObject().get("name").getAsString();
			double licenced_price = packageList.get(i).getAsJsonObject().get("licence").getAsDouble();
			double unlicenced_price = packageList.get(i).getAsJsonObject().get("unlicence").getAsDouble();
			AuctionItem item = new AuctionItem(id, name, -1/*we use this constructor but required is not valid*/);
			LuaBid luaBid = new LuaBid(item, licenced_price, unlicenced_price);
			bids.add(luaBid);
		}
		return bids;
	}
}
