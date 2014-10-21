package webServer;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;

import dataRepresentation.AuctionEnvironment;

public class UpdateServlet extends DefaultServlet{

	private static final long serialVersionUID = 2172189957056566935L;
	

	private AuctionEnvironment auctionEnvironment = null;
	
	public UpdateServlet(AuctionEnvironment ae) {
		this.auctionEnvironment = ae;
	}
	
	public void init(){
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		out.println(this.auctionEnvironment.context.generateJson());

	}
	
}
