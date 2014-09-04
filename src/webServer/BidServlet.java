package webServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import dataRepresentation.AuctionContext;
import dataRepresentation.AuctionEnvironment;
import dataRepresentation.AuctionItem;
import dataRepresentation.Bid;
import dataRepresentation.Bidder;

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
		System.out.println("receive bid request:\n"+xmlContent);
		Document doc = convertStringToDocument(xmlContent);
		

		//place a bid to environment, auctioneer will handle this bid
		Bid myBid = placeBid(doc);
		System.err.println(myBid.getBidder().getName()+"**********waiting response*********");
		
		
		// waiting for Auctioneer process bids, once next round info ready, get context update.
		while ( this.auctionEnvironment.auctioneer.nextRoundNotReady ) {}
		// get next round's context through auctioneer
		AuctionContext context_updated  =  this.auctionEnvironment.auctioneer.nextRound();
		System.out.println("updated:"+ context_updated.generateXml());
		//Respond latest AuctionContext
		PrintWriter out = response.getWriter();
		out.println(context_updated.generateXml());
		System.err.println(myBid.getBidder().getName()+"***********Response sent************");
		//infrom auctioneer get the repsonse sucessfully 
		ConfirmResponseSent(myBid);
	}
	
    private static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
        DocumentBuilder builder; 
        try 
        { 
            builder = factory.newDocumentBuilder(); 
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) );
            return doc;
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return null;
    }

    private Bid placeBid(Document doc) {
    	NodeList bidderNode = doc.getElementsByTagName("bidder");
    	Element bidderInfo = (Element) (bidderNode.item(0));
    	String name = bidderInfo.getAttribute("name");
    	String ip = bidderInfo.getAttribute("ip");
    	
    	Bidder bidder = this.auctionEnvironment.bidderList.getBidder(name, ip);
    	if (null == bidder) {
    		try {
				throw new Exception("BidServlet: Cannot find bidder inside bidderlist");
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
    	}
    	
    	List<AuctionItem> bidderItemList = new ArrayList<AuctionItem>();
    	NodeList itemList = doc.getElementsByTagName("item");
    	/*
    	 * Check current auction type, SAA or CCA.
    	 */
    	if  (this.auctionEnvironment.context.getType() == AuctionContext.AuctionType.SAA) {
        	for (int i=0; i<itemList.getLength(); i++) {
        		Node currentNode = itemList.item(i);
        		Element element = (Element) currentNode;
        		String itemName = element.getAttribute("name");
        		double itemPrice = Double.parseDouble(element.getAttribute("price"));
        		int id = Integer.parseInt(element.getAttribute("id"));
        		bidderItemList.add(new AuctionItem(id, itemName, itemPrice));
        	}
    	} else  if (this.auctionEnvironment.context.getType() == AuctionContext.AuctionType.CCA) {
        	for (int i=0; i<itemList.getLength(); i++) {
        		Node currentNode = itemList.item(i);
        		Element element = (Element) currentNode;
        		String itemName = element.getAttribute("name");
        		int itemRequire = 0;
        		try { //if bidder doesn't bid for this, the quantity_require may not be defined
        			itemRequire = Integer.parseInt(element.getAttribute("quantity_require"));
        		} catch(RuntimeException e) { // so put itemRequire as 0 
        			itemRequire = 0;
        		}
        		int id = Integer.parseInt(element.getAttribute("id"));
        		bidderItemList.add(new AuctionItem(id, itemName, itemRequire));
        	}
    	}


    	Bid bid = new Bid(bidder, bidderItemList);
    	bidder.placeBid(this.auctionEnvironment, bid);
    	return bid;
    }
    
    private boolean ConfirmResponseSent(Bid bid) {
    	if (this.auctionEnvironment.auctioneer.removeBid(bid)) {
    		return true;
    	}else {
    		throw new RuntimeException("ERROR: Invalid Bid removing");
    	}
    }
	public void destroy() {
		// do nothing.
	}
}
