package edu.uiuc.viz.server;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



public class VizServer {

	public static void main(String[] args) throws Exception {

		Server server = new Server(8080);
		
		//ServletContextHandler handler = new ServletContextHandler(server, "/");
		//handler.addServlet(Servlet.class, "/");
		WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar("viz.war");
        webapp.setParentLoaderPriority(true);
        webapp.setServer(server);
        webapp.setClassLoader(ClassLoader.getSystemClassLoader());
        webapp.getSessionHandler().getSessionManager()
				.setMaxInactiveInterval(10);
		server.setHandler(webapp);	
        
		server.start();
		server.join();
	}

}