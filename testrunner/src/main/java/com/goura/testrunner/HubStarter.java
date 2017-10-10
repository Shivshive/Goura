package com.goura.testrunner;

import java.util.logging.Logger;

import org.openqa.grid.internal.utils.configuration.GridHubConfiguration;
import org.openqa.grid.internal.utils.configuration.StandaloneConfiguration;
import org.openqa.grid.web.Hub;

import com.google.gson.JsonObject;

public class HubStarter implements AutoCloseable, IStarter  {
	 private static final Logger log = Logger.getLogger(HubStarter.class.getName());
	private Hub hub ;
	
	public HubStarter(StandaloneConfiguration config) throws Exception{
		
		hub= new Hub((GridHubConfiguration) config);        
	}
	
	
	public void start() throws Exception{
		if(hub != null){
			 log.info("Launching Selenium Grid hub");	          
			 hub.start();
	         log.info("Nodes should register to " + hub.getRegistrationURL());
	        log.info("Selenium Grid hub is up and running");
		}
	}

	public void stop() throws Exception{
		hub.stop();
	}

	public void close() throws Exception {
		if(hub != null)
			hub.stop();
	}
}
