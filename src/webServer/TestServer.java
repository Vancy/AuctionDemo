package webServer;
import javax.servlet.Servlet;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

public class TestServer {
	private Server server = null;
	private WebAppContext webappContextHandler = null;
	private ServletContextHandler servletContextHandler = null;
	public TestServer() {
		server = new Server(8080);
		webappContextHandler = new WebAppContext();
        webappContextHandler.setDescriptor("webapp/WEB-INF/web.xml");
        webappContextHandler.setResourceBase("./");
        webappContextHandler.setContextPath("/");
		servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContextHandler.setContextPath("/servlet");
		
	    ContextHandlerCollection contexts = new ContextHandlerCollection();
	    contexts.setHandlers(new Handler[] { webappContextHandler, servletContextHandler });
		server.setHandler(contexts);
	}
	
	public void start() throws Exception {
		server.start();
	}
	
	public void stop() throws Exception {
		server.stop();
		server.join();
	}
	
	public boolean isStarted() {
		return server.isStarted();
	}
	
	public boolean isStopped() {
		return server.isStopped();
	}
	public void servletAdd(DefaultServlet servlet, String path) {
		servletContextHandler.addServlet(new ServletHolder(servlet),path);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestServer server = new TestServer();
		// Servlet adding
		server.servletAdd(new LoginServlet(), "/login");
		
		try {
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
