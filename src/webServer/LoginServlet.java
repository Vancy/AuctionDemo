package webServer;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;

import dataRepresentation.AuctionEnvironment;
import dataRepresentation.Bidder;
import dataRepresentation.HumanBidder;

public class LoginServlet extends DefaultServlet{

	private static final long serialVersionUID = 2172189957056566935L;
	

	private AuctionEnvironment auctionEnvironment = null;
	
	public LoginServlet(AuctionEnvironment ae) {
		this.auctionEnvironment = ae;
	}
	
	public void init(){
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//set response content type
		response.setContentType("text/html");
		//get requester info
		String username = request.getParameter("name");
		System.out.println("RequesterName:"+username);
		String userip = request.getParameter("ip");
		System.out.println("RequesterIP:"+userip);
		
		//add user's info to bidder list if bidder info does not exist
		if (!auctionEnvironment.bidderList.containBidder(username, userip)) {
			Bidder newBidder = new HumanBidder(username, userip);
			auctionEnvironment.bidderList.addBidder(newBidder);
			//add LUA valuation message to this bidder, if any.
			this.setLuaValuationMsg(newBidder);
		}
		
		//send cookie if cookie is null
		if( !hasCookie(request.getCookies()) ) {
			Cookie nameCookie = new Cookie("name",username);
			Cookie ipCookie = new Cookie("ip",userip);
			nameCookie.setMaxAge(5);
			response.addCookie(nameCookie);
			response.addCookie(ipCookie);
		}
		
		// Actual logic goes here.
		PrintWriter out = response.getWriter();
		out.println(this.auctionEnvironment.context.generateJson());
	}
	
	private void setLuaValuationMsg(Bidder bidder) {
		int id = bidder.getID();
		ArrayList<ArrayList<String>> valuationSetups = this.auctionEnvironment.bidderList.getLuaValuationSetups();
		if (null == valuationSetups) {
			return;
		} else {
			//TODO
			//Bidderlist id starts from 1
			String msg = valuationSetups.get(0).get(id-1);
			bidder.setValuationMsg(msg);
		}
		
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
	public void destroy() {
		// do nothing.
	}
}
