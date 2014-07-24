package webServer;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import dataRepresentation.AuctionContext;

public class WebServer {
	private Server server = null;
	private WebAppContext webappContextHandler = null;
	private ServletContextHandler servletContextHandler = null;
	private AuctionContext auctionContext = null;
	
	public WebServer(AuctionContext ac) {
		server = new Server(8080);
		auctionContext = ac;
		webappContextHandler = new WebAppContext();
        webappContextHandler.setResourceBase("webapps/pages");
        webappContextHandler.setDescriptor("webapp/WEB-INF/web.xml");

        webappContextHandler.setContextPath("/");
		servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContextHandler.setContextPath("/WEB-INF");
		this.addServlets();
	    ContextHandlerCollection contexts = new ContextHandlerCollection();
	    contexts.setHandlers(new Handler[] { webappContextHandler, servletContextHandler });
		server.setHandler(contexts);
	}
	
	private void addServlets() {
		servletContextHandler.addServlet(new ServletHolder(new LoginServlet(auctionContext)),"/login.xml");
		servletContextHandler.addServlet(new ServletHolder(new BidServlet(auctionContext)),"/bid.xml");
	}
	
	public void start() {
		try {
			server.start();
		} catch (Exception e) {
			System.err.println("Web Server starting failed!");
			e.printStackTrace();
		}
	}
	
	public void stop() throws Exception {
		server.stop();
		server.join();
		System.err.println("Web Server closed!");
	}
	
	public boolean isStarted() {
		return server.isStarted();
	}
	
	public boolean isStopped() {
		return server.isStopped();
	}
}
