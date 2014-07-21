package webServer;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;
public class BidServlet extends DefaultServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2483175565395222277L;

	public void init(){

	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		hasCookie(request.getCookies());
		
		
		//set response content type
		response.setContentType("text/html");
		//get requester info
		String username = request.getParameter("name");
		System.out.println("RequesterName:"+username);
		String userip = request.getParameter("ip");
		System.out.println("RequesterIP:"+userip);
		//send cookie
		Cookie nameCookie = new Cookie("name",username+userip);
		nameCookie.setMaxAge(5);
		response.addCookie(nameCookie);
		// Actual logic goes here.
		PrintWriter out = response.getWriter();
		out.println("<h2>" + "xing" + "</h2>");

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
