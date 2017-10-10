package com.goura.testrunner;

import static org.hamcrest.CoreMatchers.nullValue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.grid.internal.utils.configuration.GridHubConfiguration;
import org.openqa.grid.internal.utils.configuration.GridNodeConfiguration;
import org.openqa.grid.internal.utils.configuration.StandaloneConfiguration;
import org.openqa.grid.selenium.GridLauncherV3;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.server.log.LoggingOptions;
import org.openqa.selenium.remote.server.log.TerseFormatter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.bonigarcia.wdm.BrowserManager;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import io.github.bonigarcia.wdm.InternetExplorerDriverManager;





public class App {
	
	static String command="";
	
	class Starter extends Thread{

	    @Override
	    public void run() {
	    	
	        
	        Thread gridStarter = null;
	        
	        
	        try {
            	sendMessage("Starting Selenium Node..");
    	    	sendMessage("--------------------------------------");
    	    	Scanner scanner = new Scanner(System.in);
    	    	gridStarter = new GridStarter();
            	gridStarter.start();
    	    	while(true){	        	
    	    		command= scanner.nextLine();
    	            switch(command.trim()){
    	            case"Start" : 
    	            	
    	            	break;
    	            case"Stop" : 
    	            	System.exit(0);       		
    	            	return;
    	            default:
    	            	System.out.println("Command Not found..");
    	            	break;
    	            }
    	        }
            } catch (IOException e) {	
            	try {
					sendMessage(e.getMessage());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
				System.exit(0);  
			}
	        
	        
	    }

	}
	
    class GridStarter extends Thread{

        @Override
        public void run() {
        	try {
        		
        		Thread t = new Thread(){
        			public void run()
        	        {
        				try {
							Runner("");
						} catch (Exception e) {							
							e.printStackTrace();
						}
        	        }
        		};
        		t.start();;
        		while(!this.isInterrupted()){
        			Thread.sleep(1000);
        		}        		

            } catch (Exception e) {
            	
                e.printStackTrace();
            }

        }
        
        private void Runner(String configFile) throws Exception{
    		
        	JsonObject firefoxCapabilities = new JsonObject();
    		firefoxCapabilities.addProperty("browserName", "firefox");
    		firefoxCapabilities.addProperty("maxInstances", 5);
    		firefoxCapabilities.addProperty("seleniumProtocol", "WebDriver");
    		
    		JsonObject chromeCapabilities = new JsonObject();
    		chromeCapabilities.addProperty("browserName", "chrome");
    		chromeCapabilities.addProperty("maxInstances", 5);
    		chromeCapabilities.addProperty("seleniumProtocol", "WebDriver");
    		
    		
    		JsonArray capabilities = new JsonArray();
    		capabilities.add(chromeCapabilities);
    		capabilities.add(firefoxCapabilities);
    		
    		JsonObject config = new JsonObject();		
    		
    		config.addProperty("port", 5561);
    		config.addProperty("role", "node");
    		config.addProperty("hub", "http://localhost:4444/grid/register/");
    		config.add("capabilities", capabilities);
    		
    		System.out.println("Using node configuration : " + config.toString());		
    		
    		IStarter starter = null;
    		
    		StandaloneConfiguration sonfig = new StandaloneConfiguration();
    		//sonfig.debug = true;
    		//sonfig.log = "log.txt";
    		
    		configureLogging(sonfig);
    		
    		if(config.has("role") && config.get("role").getAsString().equals("hub")){
    			
    			sonfig  = GridHubConfiguration.loadFromJSON(config);
    			
    			starter = new HubStarter(sonfig);
    			
    		}
    		else if(config.has("role") && config.get("role").getAsString().equals("node")){
    			
    			sonfig = GridNodeConfiguration.loadFromJSON(config);
    			starter = new NodeStarter(sonfig);
    		}
    		else{
    			
    		}
    		
    		try{
    			if(starter != null)
    				starter.start();
    		}
    		catch(Exception e){
    			
    		}
    		finally{
    			//starter.stop();
    		}
    	}

    }

	
	

	public static void main(String[] args) throws Exception{
		
		String workingDir = System.getProperty("user.dir");
		
		Map<String, String> properties = new HashMap<>();
		if(args != null){
			System.out.println("Below list arguments are passed: ");
			for(String arg : args){
				System.out.println(arg);
				
				if(arg.contains("=")){
					String[] arr =  arg.split("=");
					if(arr.length > 1){
						properties.put(arr[0].trim(), arr[1].trim());
					}
				}
			}
		}		
		
		System.out.println("Working Directory = " + workingDir);
		
		setDriverProperty("webdriver.chrome.driver", properties, workingDir, "Chrome Driver", "chromedriver.exe");
		setDriverProperty("webdriver.gecko.driver", properties, workingDir, "Firefox Driver", "geckodriver.exe");
		
		new App().start();
		
	}
	
	private void start() {

		Starter starter = new Starter();
		starter.start();
		
    }
	
	
	  private static void configureLogging(StandaloneConfiguration configuration) {
		    Level logLevel =
		        configuration.debug
		        ? Level.FINE
		        : LoggingOptions.getDefaultLogLevel();
		    if (logLevel == null) {
		      logLevel = Level.INFO;
		    }
		    Logger.getLogger("").setLevel(logLevel);
		    Logger.getLogger("org.openqa.jetty").setLevel(Level.WARNING);

		    String logFilename =
		        configuration.log != null
		        ? configuration.log
		        : LoggingOptions.getDefaultLogOutFile();
		    if (logFilename != null) {
		      for (Handler handler : Logger.getLogger("").getHandlers()) {
		        if (handler instanceof ConsoleHandler) {
		          Logger.getLogger("").removeHandler(handler);
		        }
		      }
		      try {
		        Handler logFile = new FileHandler(new File(logFilename).getAbsolutePath(), true);
		        logFile.setFormatter(new TerseFormatter());
		        logFile.setLevel(logLevel);
		        Logger.getLogger("").addHandler(logFile);
		      } catch (IOException e) {
		        throw new RuntimeException(e);
		      }
		    } else {
		      for (Handler handler : Logger.getLogger("").getHandlers()) {
		        if (handler instanceof ConsoleHandler) {
		          handler.setLevel(logLevel);
		          handler.setFormatter(new TerseFormatter());
		        }
		      }
		    }
		  }
	
	private static void setDriverProperty(String driverConfigName, Map<String, String>  properties, String workingDir, String DriverName, String DriverEXEName){
		String chromeDriver = System.getProperty(driverConfigName);
		if(chromeDriver == null || chromeDriver.isEmpty()){
			if(properties.containsKey(driverConfigName)){
				chromeDriver = properties.get(driverConfigName);
				File driverFile = new File(chromeDriver);
				if(driverFile.exists() && driverFile.isFile()){
					chromeDriver = driverFile.getAbsolutePath();
				}
				else{
					chromeDriver = (new File(workingDir ,  chromeDriver)).getAbsolutePath();
					driverFile = new File(chromeDriver);
					if(driverFile.exists() && driverFile.isFile()){
						chromeDriver = driverFile.getAbsolutePath();
					}
					else{						
						chromeDriver = "";
									
					}
				}
			}
			else{
				File driverFile = new File(workingDir ,  DriverEXEName);
				if(driverFile.exists() && driverFile.isFile()){
					chromeDriver = driverFile.getAbsolutePath();
				}
				else{
					chromeDriver = "";
				}
			}
		}
		
		if(chromeDriver.isEmpty()){
			switch(driverConfigName){
			case "webdriver.chrome.driver":
				ChromeDriverManager.getInstance().setup();	
			break;
			case "webdriver.gecko.driver":
				FirefoxDriverManager.getInstance().setup();	
			break;
			case "webdriver.ie.driver":
				InternetExplorerDriverManager.getInstance().setup();	
			break;
			
			}	
		}
		else{

			System.out.println(DriverName + " path = " + chromeDriver);
			System.setProperty(driverConfigName, chromeDriver);
		}
	}
	
		  private void sendMessage(String message) throws IOException {
		    System.out.print(message);
		    //System.out.write(message.getBytes("UTF-8"));
		    //System.out.flush();		    
		  }

		  public byte[] getBytes(int length) {
		    byte[] bytes = new byte[4];
		    bytes[0] = (byte) (length & 0xFF);
		    bytes[1] = (byte) ((length >> 8) & 0xFF);
		    bytes[2] = (byte) ((length >> 16) & 0xFF);
		    bytes[3] = (byte) ((length >> 24) & 0xFF);
		    return bytes;
		  }

}
