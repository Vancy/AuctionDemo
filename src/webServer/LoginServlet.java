package webServer;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;

import dataRepresentation.AuctionContext;
public class LoginServlet extends DefaultServlet{


	private static final long serialVersionUID = 2172189957056566935L;
	
	private String logo;
	
	private AuctionContext auctionContext = null;
	
	public LoginServlet(AuctionContext ac) {
		auctionContext = ac;
	}
	
	public void init(){
		logo = "Hello GHouan!";
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//set response content type
		response.setContentType("text/html");
		//get requester info
		String username = request.getParameter("name");
		System.out.println("RequesterName:"+username);
		String userip = request.getParameter("ip");
		System.out.println("RequesterIP:"+userip);
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
		out.println(auctionContext.generateXml());
		System.out.println(auctionContext.generateXml());
		//out.println("<h1>" + logo + "</h1>");
		//out.println("<h2>" + "xing" + "</h2>");

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
