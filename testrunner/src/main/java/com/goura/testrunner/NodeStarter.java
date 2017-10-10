package com.goura.testrunner;

import java.util.logging.Logger;

import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.utils.SelfRegisteringRemote;
import org.openqa.grid.internal.utils.configuration.GridNodeConfiguration;
import org.openqa.grid.internal.utils.configuration.StandaloneConfiguration;
import org.openqa.grid.selenium.GridLauncherV3;
import org.openqa.selenium.remote.server.SeleniumServer;

import com.google.gson.JsonObject;

public class NodeStarter implements AutoCloseable, IStarter {

	 private static final Logger log = Logger.getLogger(NodeStarter.class.getName());

	 protected StandaloneConfiguration configuration;
	 SelfRegisteringRemote remote;
	 
	public NodeStarter(StandaloneConfiguration sonfig){
		configuration = sonfig;
	}
	
	public void start() throws Exception{
		log.info("Launching a Selenium Grid node");
        RegistrationRequest c =  RegistrationRequest.build((GridNodeConfiguration) configuration);
        remote = new SelfRegisteringRemote(c);
        remote.setRemoteServer(new SeleniumServer(c.getConfiguration()));
        remote.startRemoteServer();
        log.info("Selenium Grid node is up and ready to register to the hub");
        remote.startRegistrationProcess();
	}
	
	public void stop(){
		remote.stopRemoteServer();
		log.info("Selenium Grid node is stopped");
	}
	
	public void close() throws Exception {
		if(remote != null)
			remote.stopRemoteServer();
	}
}
