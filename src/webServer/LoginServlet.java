package webServer;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;
public class LoginServlet extends DefaultServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2172189957056566935L;
	private String logo;
	
	public void init(){
		logo = "Hello GHouan!";
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set response content type
		response.setContentType("text/html");
		StringBuffer in = request.getRequestURL();
		System.out.println("RequesterName:"+request.getParameter("name"));
		String queryString = request.getQueryString();
		System.out.println("query:"+queryString);
		System.out.println("URL:"+in);
		// Actual logic goes here.
		PrintWriter out = response.getWriter();
		out.println("<h1>" + logo + "</h1>");
		out.println("<h2>" + "xing" + "</h2>");
		
		Cookie nameCookie = new Cookie("name",request.getParameter("name")+request.getParameter("ip"));
		nameCookie.setMaxAge(60*60);
		response.addCookie(nameCookie);
	}

	public void destroy() {
		// do nothing.
	}
}
