package com.csc.ir.testing;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.ChromeDriverManager;

public class ChromeTest {
	private WebDriver driver;

	@BeforeClass
	public static void setupClass() {
		ChromeDriverManager.getInstance().setup();
	}

	@Before
	public void setupTest() {
		driver = new ChromeDriver();
	}

	@After
	public void teardown() {
		if (driver != null) {
			driver.quit();
		}
	}

	@Test
	public void test() throws InterruptedException, IOException {
		
		driver.get("http://localhost:4080/ir-webmain/JITRules/flowchart.html?GUID=1234&ENVID=SP15");
		driver.manage().window().maximize();
		wait(10);		
		TakeScreenShot("JIT Analysis.jpg");
		
		
		executeJavaScript("$('#DesignMode').click();");
		wait(10);
		TakeScreenShot("JIT Designer.jpg");
		
		executeJavaScript("$('#AddModel').click();");		
		wait(10);
		TakeScreenShot("JIT Designer - Add New Model.jpg");
		
		executeJavaScript("$('#ModelName').val('TestModel');");
		wait(10);
		TakeScreenShot("JIT Designer - Add New Model - TestModel.jpg");
		
		executeJavaScript("$('#AddModelOK').click();");
		wait(10);
		TakeScreenShot("JIT Designer - TestModel.jpg");
		
		
		executeJavaScript("$('#accordion1').find( 'a' )[0].click()");
		wait(10);
		
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.or(
		    ExpectedConditions.visibilityOfElementLocated(By.id("myPalette1")),
		    ExpectedConditions.visibilityOfElementLocated(By.id("myDiagram"))
		));
		TakeScreenShot("JIT Designer - STANDARD LOGICAL SHAPES.jpg");
		
		WebElement paletteCanvas = driver.findElement(By.id("myPalette1"));
		WebElement flowCanvas = driver.findElement(By.id("myDiagram"));
		addShape(paletteCanvas, 195, 90, flowCanvas, 565 ,150);
		wait(10);
		wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.or(
		    ExpectedConditions.visibilityOfElementLocated(By.id("Field")),
		    ExpectedConditions.visibilityOfElementLocated(By.id("Formula")),
		    ExpectedConditions.visibilityOfElementLocated(By.id("CalculateOk"))
		));
		executeJavaScript("$('#Field').val('Variable1');");
		executeJavaScript("$('#Formula').val('Test');");
		TakeScreenShot("JIT Designer - Calculate Shape.jpg");
		executeJavaScript("$('#CalculateOk').click();");
		
		
		
		paletteCanvas = driver.findElement(By.id("myPalette1"));
		flowCanvas = driver.findElement(By.id("myDiagram"));
		addShape(paletteCanvas, 40, 90, flowCanvas, 565 ,150);
		wait(10);
		TakeScreenShot("JIT Designer - Add Decision Shape.jpg");
		
		
	}
	
	public void addShape(WebElement paletteCanvas, int palette_node_x, int palette_node_y, 
		WebElement flowCanvas, int diagramOffsetX, int diagramOffsetY){
		Actions actions = new Actions(driver);
		actions.moveToElement(paletteCanvas, palette_node_x, palette_node_y);
		actions.clickAndHold();
		//Dragging selected node a little bit to make it work. 
	    actions.moveToElement(paletteCanvas, palette_node_x, palette_node_y+50 );
	    //Now perform the actual move
	    actions.moveToElement(flowCanvas, diagramOffsetX , diagramOffsetY);
	    actions.release();
	    actions.perform();
	}
	
	public void wait(int time){
		driver.manage().timeouts().implicitlyWait(time, TimeUnit.SECONDS);
		waitForJStoLoad();
	}
	
	public Object executeJavaScript(String code){
		JavascriptExecutor js = (JavascriptExecutor) driver; 
		return js.executeScript(code, 1000*60);
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
	
	public void TakeScreenShot(String fileName) throws IOException{
		File src= ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		File newFile = new File(fileName);
		FileUtils.copyFile(src, newFile);
	}
}
