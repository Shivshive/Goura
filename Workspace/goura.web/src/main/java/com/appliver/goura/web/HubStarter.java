package com.appliver.goura.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.internal.utils.HtmlRenderer;
import org.openqa.grid.internal.utils.configuration.GridHubConfiguration;
import org.openqa.grid.web.Hub;
import org.openqa.grid.web.servlet.beta.WebProxyHtmlRendererBeta;
import org.openqa.selenium.remote.server.log.TerseFormatter;

public class HubStarter implements AutoCloseable  {

	private Hub hub ;
	private File logFile;
	Handler  handler = null;
	
	public HubStarter() throws Exception{
		doLogging();
		
		GridHubConfiguration config = new GridHubConfiguration();
		//config = readConfig(args, config, config.hubConfig);
		config.port = getPort();
		//config.capabilityMatcher = new JenkinsCapabilityMatcher(Channel.current(), config.capabilityMatcher);
		
		hub= new Hub(config);
        hub.start();
	}
	
	public static int getPort(){
		return 4444;
	}
	
	
	public void start() throws Exception{
		if(hub != null)
			hub.start();
	}

	public void stop() throws Exception{
		hub.stop();
	}
	
	public List<String> getNodeDetails(){
		List<String> nodes = new ArrayList<>();
		for (RemoteProxy proxy : hub.getRegistry().getAllProxies()) {
		    // HtmlRenderer beta = new WebProxyHtmlRendererBeta(proxy);
		     // nodes.add(beta.renderSummary());
			nodes.add(proxy.getId());
		}
		return nodes;
	}
	
	public String getRegistrationURL(){
		return hub.getRegistrationURL().toString();
	}
	
	public String getConsoleURL(){
		return hub.getConsoleURL().toString();
	}
	
	@Override
	public void close() throws Exception {
		if(hub != null)
			hub.stop();
	}
	
	public List<String> readLogs(){
		ArrayList<String> logs = new ArrayList<String>();
		try{
			   FileInputStream fstream = new FileInputStream(logFile);
			   BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			   String strLine;
			   /* read log line by line */
			   while ((strLine = br.readLine()) != null)   {
			     /* parse strLine to obtain what you want */
				   logs.add(strLine);
			   }
			   br.close();
			} catch (Exception e) {
			     System.err.println("Error: " + e.getMessage());
			}
		return logs;
	}
	
	public void doLogging() throws IOException{
		
		String workingPath = TestCaseIO.getWorkingPath();
		SimpleDateFormat fomat = new SimpleDateFormat("dd-M-yyyy_hh-mm-ss");
		String RuntimeFolder = fomat.format(new Date());
		logFile  = FileUtils.getFile(workingPath, "Logs", "HubLogs-" + RuntimeFolder +".log" );
		
		
		
		Level logLevel = Level.INFO;
		Logger.getLogger("").setLevel(logLevel);
		Logger.getLogger("org.openqa.jetty").setLevel(Level.INFO);
		
//		System.setProperty("logFilename", logFile.getAbsolutePath());
//		org.apache.logging.log4j.core.LoggerContext ctx =
//			    (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
//			ctx.reconfigure();
		
		
		
		
		if(handler != null){			
			Logger.getLogger("").removeHandler(handler);			
			handler.flush();
			handler.close();
			handler = null;
		}
		handler = new FileHandler(logFile.getAbsolutePath(), true);
		handler.setFormatter(new TerseFormatter());
		handler.setLevel(logLevel);
        Logger.getLogger("").addHandler(handler);
	}
}
