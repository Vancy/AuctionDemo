package webServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;

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
		
		// Get bidder Information
		String username = null;
		String userip = null;
		if( hasCookie(request.getCookies()) ) {
			for(Cookie c: request.getCookies()){
			    if("name".equals(c.getName())){
			        username = c.getValue(); 
			    }
			    if("ip".equals(c.getName())){
			        userip = c.getValue(); 
			    }
			}
		}
		System.out.println("Bidder submit(name):"+username);
		System.out.println("Bidder submit(ip):"+userip);
		
		// read Post content by request.getReader(). store content into bufferedReader
		BufferedReader reader = request.getReader();
		StringBuilder builder = new StringBuilder();
		String aux = "";
		while ((aux = reader.readLine()) != null) {
		    builder.append(aux);
		}
		String xmlContent = builder.toString();
		System.out.println("receve bid request:"+xmlContent);
		Document doc = convertStringToDocument(xmlContent);
		
		
		//place a bid to environment, auctioneer will handle this bid
		placeBid(username, userip, doc);
		// get next round's context through auctioneer
		AuctionContext context_updated  = this.auctionEnvironment.auctioneer.nextRound();
		
		//Respond latest AuctionContext
		PrintWriter out = response.getWriter();
		out.println(context_updated);

	}
	
	private boolean hasCookie(Cookie[] cookies){
		if (null != cookies) {
			for(Cookie c: cookies){
			    if("name".equals(c.getName())){
			        System.out.println("request has cookie:"+c.getValue());
			        return true; 
			    }
			}
		} 
		System.out.println("request doesn't have cookie");
		return false;
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
    
    private boolean placeBid(String name, String ip, Document doc) {
    	Bidder bidder = this.auctionEnvironment.bidderList.getBidder(name, ip);
    	if (null == bidder) {
    		try {
				throw new Exception("Cannot find bidder inside bidderlist");
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
    	}
    	ArrayList<AuctionItem> bidderItemList = new ArrayList<AuctionItem>();
    	NodeList itemList = doc.getElementsByTagName("item");
    	for (int i=0; i<itemList.getLength(); i++) {
    		Node currentNode = itemList.item(i);
    		Element element = (Element) currentNode;
    		String itemName = element.getAttribute("name");
    		double itemPrice = Double.parseDouble(element.getAttribute("price"));
    		bidderItemList.add(new AuctionItem(itemName, itemPrice));
    	}
    	Bid bid = new Bid(bidder, bidderItemList);
    	bidder.placeBid(this.auctionEnvironment, bid);
    	return true;
    }
	public void destroy() {
		// do nothing.
	}
}
