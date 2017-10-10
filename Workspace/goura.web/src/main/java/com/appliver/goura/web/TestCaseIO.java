package com.appliver.goura.web;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.google.common.io.Files;

public class TestCaseIO {
	
	public static final String PROPERTIES_FILE = "config.properties";
	private static Properties properties = null;

	public static  String getProperty(String propertyName){
		
		if(properties == null){
			readProperties();
		}
		
		return properties.getProperty(propertyName);
	}
	
	private static Properties readProperties() {
	    InputStream inputStream = GouraRESTService.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
	    if (inputStream != null) {
	        try {
	        	properties = new Properties();
	            properties.load(inputStream);
	        } catch (IOException e) {	            
	            e.printStackTrace();
	        }
	    }
	    return properties;
	}
	
	public static String getWorkingPath() throws IOException{
		String WorkingPath=	getProperty("Goura.WorkingDirPath");
		
		if(WorkingPath == null){
			throw new IOException("Goura.WorkingDirPath not found");
		}
		
		return WorkingPath;
	}
	
	public static String GetTestCasesPath() throws IOException{
		String WorkingPath=	getProperty("Goura.WorkingDirPath");
		
		if(WorkingPath == null){
			throw new IOException("Goura.WorkingDirPath not found");
		}
		
		File file = new File(WorkingPath + File.separator + "TestCases");
		
		if(!file.isDirectory()){
			file.mkdirs();
		}
		
		
		return file.getAbsolutePath();
	}
	
	

	public static void SaveTestCaseToWorkingPath(String name, String testCase) throws IOException{
		
		name = makeJSONFileName(name);		
		File testCaseFile = new File(GetTestCasesPath(), name);
		saveString(testCaseFile, testCase);
	}
	

	public static List<String> getTestCases() throws IOException{
		
		List<String> testcases = new ArrayList<>();
		
		File testCaseFile = new File(GetTestCasesPath());
		
		File[] files = testCaseFile.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				
				if(FilenameUtils.getExtension(name).toLowerCase().equals("json")){
					return true;
				}
				return false;
			}
		});
		
		if(files != null){
			for(File file : files){
				testcases.add(FilenameUtils.getBaseName((file.getName())));
			}
		}
		
		return testcases;
	}
	
	public static String GetScreenShotPath() throws IOException{
		String WorkingPath=	getProperty("Goura.WorkingDirPath");
		
		if(WorkingPath == null){
			throw new IOException("Goura.WorkingDirPath not found");
		}
		
		File file = FileUtils.getFile(WorkingPath, "ScreenShot");
		
		if(!file.isDirectory()){
			file.mkdirs();
		}
		
		return file.getAbsolutePath();
	}
	
	public static String makeJSONFileName(String fileName){
		String name = fileName;
		
		if(!name.toLowerCase().endsWith(".json")){
			name += ".json";
		}
		
		return name;
	}
	
	public static void saveString(File dest, String textContent) throws IOException{

		if(dest == null) return;
		
		if(!dest.getParentFile().isDirectory()){
			dest.getParentFile().mkdirs();
		}
		
		byte[] data = textContent.getBytes();
		Files.write(data, dest);		
	}
	
	
}
