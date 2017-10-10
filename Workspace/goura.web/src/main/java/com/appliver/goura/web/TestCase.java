package com.appliver.goura.web;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.io.Files;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;

public class TestCase {

	
	public TestCase(String path, String testCaseName){
		ScreenShotLocation = path;
		TestCaseName = testCaseName;
		SimpleDateFormat fomat = new SimpleDateFormat("dd-M-yyyy_hh-mm-ss");
		RuntimeFolder = fomat.format(new Date());
		
		if(TestCaseName.endsWith(".json")){
			TestCaseName = TestCaseName.replace(".json", "");
		}
		
	}
	
	private String ScreenShotLocation = "";
	private String  TestCaseName = "";
	String RuntimeFolder ="";
	private WebDriver driver;
	private  boolean isStarted = false;
	
	public void stepUp(String browser, String Remote) throws TestCaseException, MalformedURLException{
		
		if(Remote != null && Remote.contains("http")){
			
			Capabilities c = DesiredCapabilities.chrome();
			driver = new RemoteWebDriver(new URL(Remote +  "/wd/hub"), c);
			
		}
		else{
			if(browser.equalsIgnoreCase("Chrome")){
				ChromeDriverManager.getInstance().setup();
				driver = new ChromeDriver();
				isStarted = true;
			}
			else if(browser.equalsIgnoreCase("Firefox")){
				FirefoxDriverManager.getInstance().setup();
				driver = new FirefoxDriver();
				isStarted = true;
			}
			else{
				throw new TestCaseException();
			}
		}
	}

	public boolean isStarted() {
		return isStarted;
	}

	
	public class TestCaseException extends Exception {
		
	}


	public void exit() {
		isStarted = false;
		if(driver != null){
			driver.quit();
		}
	}

	public void run(int key, String command, String data, Map<String, Object> results) throws NumberFormatException, InterruptedException, IOException {
			
		String[] datas =  data.split("<=");
		
		if(datas[0].contains("(decode)") && datas.length > 1){
			datas[0] = datas[0].replace("(decode)", decode(datas[1]));
		}
		
		boolean TakeSceenShot = false;
		
		switch(command.toLowerCase()){
		case "get":
			driver.get(datas[0]);
			TakeSceenShot = true;
			break;
		case "maximize":
			driver.manage().window().maximize();
			break;
		case "click":
			driver.findElement(By.id(datas[0])).click();
			TakeSceenShot = true;
			break;
		case "enter":
			driver.findElement(By.id(datas[0])).sendKeys(datas[1]);
			TakeSceenShot = true;
			break;
		case "javascript":			
			executeJavaScript(datas[0]);
			TakeSceenShot = true;
			break;
		case "wait":			
			Thread.sleep(Integer.parseInt(datas[0]) * 1000);
			break;
		case "waitforvisibility":	
			String[] ids =  datas[0].split(",");
			WebDriverWait wait = new WebDriverWait(driver, 10);
			for(String id: ids){
				wait.until(ExpectedConditions.or(ExpectedConditions.visibilityOfElementLocated(By.id(id))));
			}
			break;
		case "dragdrop":	
			String[] drops =  datas[0].split(",");
			DragDrop(drops[0], Integer.parseInt(drops[1]), Integer.parseInt(drops[2]), 
					drops[3], Integer.parseInt(drops[4]), Integer.parseInt(drops[5]));
			TakeSceenShot = true;
			break;
		}
		wait(10, (Integer.toString(key)), TakeSceenShot , results);
	}
	
	private String decode(String encoded) {		; 
		return new String(Base64.getDecoder().decode(encoded));
	}

	public void DragDrop(String from, int fromElem_x, int fromElem_y, 
			String to, int toElemX, int toElemY){
		
		WebElement fromElem = driver.findElement(By.id(from));
		WebElement toElem = driver.findElement(By.id(to));
		
		
			Actions actions = new Actions(driver);
			actions.moveToElement(fromElem, fromElem_x, fromElem_y);
			actions.clickAndHold();
			//Dragging selected node a little bit to make it work. 
		    actions.moveToElement(fromElem, fromElem_x, fromElem_y+50 );
		    //Now perform the actual move
		    actions.moveToElement(toElem, toElemX , toElemY);
		    actions.release();
		    actions.perform();
		}
	
	public void wait(int time, String key, boolean takeSceenShot, Map<String, Object> results) throws IOException{
		driver.manage().timeouts().implicitlyWait(time, TimeUnit.SECONDS);
		waitForJStoLoad();
		
		if(takeSceenShot)
			TakeScreenShot( TestCaseName + key, results);
	}
	
	public boolean waitForJStoLoad() {

	    WebDriverWait wait = new WebDriverWait(driver, 30);

	    // wait for jQuery to load
	    ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
	      @Override
	      public Boolean apply(WebDriver driver) {
	        try {
	          return ((Long)executeJavaScript("return jQuery.active") == 0);
	        }
	        catch (Exception e) {
	          return true;
	        }
	      }
	    };

	    // wait for Javascript to load
	    ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
	      @Override
	      public Boolean apply(WebDriver driver) {
	        return executeJavaScript("return document.readyState")
	            .toString().equals("complete");
	      }
	    };

	  return wait.until(jQueryLoad) && wait.until(jsLoad);
	}
	
	public Object executeJavaScript(String code){
		JavascriptExecutor js = (JavascriptExecutor) driver; 
		return js.executeScript(code, 1000*60);
	}
	
	
	public void TakeScreenShot(String fileName,  Map<String, Object> results) throws IOException{
		
		File ScreenShotFolder = new File(ScreenShotLocation);
		
		File ScreenShotTestFolder = FileUtils.getFile(ScreenShotLocation, TestCaseName, RuntimeFolder);
		
		if(!ScreenShotTestFolder.isDirectory()){
			ScreenShotTestFolder.mkdirs();
		}
		
		if(!fileName.endsWith(".jpg")){
			fileName += ".jpg";
		}
		
		File src= ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		File newFile = new File(ScreenShotTestFolder, fileName);
		FileUtils.copyFile(src, newFile);
		
		String PreviewFile =  newFile.getAbsolutePath().replace(ScreenShotFolder.getAbsolutePath(), "");
		PreviewFile = PreviewFile.replaceAll("\\\\", "~");
		
		results.put("Preview", PreviewFile);
	}
	
	public void SaveTestCase(String testCase) throws IOException{	
		
		String name = TestCaseIO.makeJSONFileName(TestCaseName+ "__" + RuntimeFolder);		
		
		File testCaseFile = FileUtils.getFile(ScreenShotLocation, TestCaseName, name);
		
		TestCaseIO.saveString(testCaseFile, testCase);
	}
}


