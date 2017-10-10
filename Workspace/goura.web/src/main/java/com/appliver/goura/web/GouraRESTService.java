package com.appliver.goura.web;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import com.sun.jersey.multipart.FormDataParam;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appliver.goura.web.TestCase;

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.jersey.core.header.FormDataContentDisposition;

@Path("/")
public class GouraRESTService {

	public static CacheManager cm;
	private static HubStarter hub;
	
	public static CacheManager getCacheManager(){
		if(cm == null){
			cm = CacheManager.getInstance();
			cm.addCache("testData");
		}
	
		return cm;
	}
	
	public void putData(String key, Object data){		
		getCacheManager().getCache("testData").put(new Element(key, data));
	}
	
	public static Object getData(String key){
		return cm.getCache("testData").get(key);
	}
	
	class Shape implements Comparable {
		public Shape(int key, String category, String text) {
			Key = key;
			Category = category;
			Text= text;
		}
		public int Key;
		public int Index;
		public String Category;
		public String Text;
		public String Preview;
		
		@Override
		public int compareTo(Object o) {
			 return  this.Index - ((Shape)o).Index;
		}
	}
	
	public ArrayList<Shape> getShapesFromJSON(JSONArray nodeDataArray, JSONArray linkDataArray) throws Exception{
		
		 
		 LinkedHashMap<String, Shape> shapesMap = new LinkedHashMap<>();
		 
		 ArrayList<Shape> shapes = new ArrayList<Shape>();
		 
		 int currentShape = 0;
		 String startKey = "";
		 String EndKey = "";
		 while(currentShape < nodeDataArray.length()){
			 JSONObject nodeData = nodeDataArray.getJSONObject(currentShape);
			 String category =  nodeData.getString("category");
			 String text =  nodeData.getString("text");
			 int key =  nodeData.getInt("key");
			 String keyStr =  nodeData.getString("key");
			 
			 Shape s = new Shape(key, category, text);			 
			 shapesMap.put(keyStr, s);
			 shapes.add(s);
			 
			 if(category.equalsIgnoreCase("Start")){
				 if(startKey.isEmpty()){
					 startKey = keyStr;
				 }
				 else{
					 throw new Exception("more than one start shapes present in test case");
				 }
			 }
			 
			 if(category.equalsIgnoreCase("End")){
				 EndKey = keyStr;
			 }
			 
			 currentShape++;
		 }
		 
		 if(startKey.isEmpty()){
			 throw new Exception("No START shape present in test case");
		 }
		 
		 if(EndKey.isEmpty()){
			 throw new Exception("No END shape present in test case");
		 }
		 
		 if(linkDataArray.length() == 0){
			 throw new Exception("No CONNECTORS are present in test case");
		 }
		 
		 int currentLink = 0;
		 String nextShape = "";
		 int currentIndex = 1;
		 boolean isFoundLink = false;
		 while(currentLink < linkDataArray.length()){
			 isFoundLink = false;
			 JSONObject linkData = linkDataArray.getJSONObject(currentLink);
			 
			 String from = linkData.getString("from");
			 String to= linkData.getString("to");
			 
			 if(from.equals(startKey)){
				 shapesMap.get(from).Index = currentIndex;
				 currentIndex++;
				 startKey = "";
				 nextShape = to;
				 currentLink = 0;	
				 isFoundLink= true;
				 continue;
			 }
			 else if(from.equals(nextShape))
			 {
				 shapesMap.get(from).Index  = currentIndex;
				 currentIndex++;
				 currentLink = 0;
				 nextShape = to;
				 isFoundLink = true;
				 continue;
			 }			
			 
			 currentLink++;
		 }
		 
		 if(!isFoundLink){
			 shapesMap.get(nextShape).Index  = currentIndex;
		 }
		 
		 Collections.sort(shapes);
		 return shapes;
	}
	
	
	private static JSONObject getNodeData(JSONArray nodeDataArray, String key) throws JSONException{
		for(int i=0; i< nodeDataArray.length(); i++){
			JSONObject node = nodeDataArray.getJSONObject(i);
			if(node.getString("key") != null && node.getString("key").equalsIgnoreCase(key)){
				return node;
			}
		}
		return null;
	}
	
	@POST
	@Path("/CheckForHub")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public Response CheckForHub(String testCaseModel) throws Exception {
		JsonObject returnData = new JsonObject();
		
		if(hub == null){
			returnData.addProperty("started", false);
		}
		else{
			returnData.addProperty("started", true);
			returnData.addProperty("port", hub.getPort());
			returnData.addProperty("console", hub.getConsoleURL());
			returnData.addProperty("register", hub.getRegistrationURL());
		}
		
		return Response.ok( returnData.toString() ).build();
	}
	
	@POST
	@Path("/GetLogs")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public Response GetLogs(String testCaseModel) throws Exception {
		JsonArray returnData = new JsonArray();
		
		if(hub != null){
			
			for(String log : hub.readLogs()){
				returnData.add(log);
			}			
		}
		
		return Response.ok( returnData.toString() ).build();
	}
	
	@POST
	@Path("/StartHub")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public Response StartHub(String testCaseModel) throws Exception {
		
		JsonObject returnData = new JsonObject();
		
		if(hub == null){
			hub = new HubStarter();
		}
		returnData.addProperty("started", true);
		returnData.addProperty("port", hub.getPort());
		returnData.addProperty("console", hub.getConsoleURL());
		returnData.addProperty("register", hub.getRegistrationURL());
		return Response.ok( returnData.toString() ).build();
	}
	
	@POST
	@Path("/StopHub")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public Response StopHub(String testCaseModel) throws Exception {
		JsonObject returnData = new JsonObject();
		if(hub != null){
			hub.stop();
			hub = null;
		}
		returnData.addProperty("started", false);
		return Response.ok( returnData.toString() ).build();
	}
	
	@POST
	@Path("/RunTestCase")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public Response RunTestCase(String testCaseModel) throws Exception {
		
		testCaseModel = testCaseModel.replace("testCaseModel=", "");
		
		
		JSONObject testCase = new JSONObject(testCaseModel);
		
		 JSONArray nodeDataArray =  testCase.getJSONArray("nodeDataArray");
		 JSONArray linkDataArray =  testCase.getJSONArray("linkDataArray");
		
		ArrayList<Shape> shapes =  getShapesFromJSON(nodeDataArray, linkDataArray);
		Map<String, Object> parameters = new HashMap<>();
		getTestCaseName(shapes, parameters);
		String modelName =(String) parameters.get("TestCaseName");
		String Remote = (String) parameters.get("Remote");
		String browser = (String) parameters.get("Browser");
		
		TestCase Testing = new TestCase(TestCaseIO.GetScreenShotPath(), modelName);
		
		try{
			int currentShape = 0;
			while(currentShape < shapes.size()){
				Shape shape = shapes.get(currentShape);
				currentShape++;
							 
				switch(shape.Category){
				case "Start":
					
					if(!Testing.isStarted()){
						Testing.stepUp(browser, Remote);
					}
					else{
						Testing.exit();
						 return Response.ok("More than one Start shape present.").build();
					}
					break;
				case "Step":
					
					if(shape.Text == null){
						break;
					}
					
					String command = "";
					String data = "";
					
					String[] commands =  shape.Text .split("=>");
					if(commands.length > 1){
						command = commands[0].trim();
						data = commands[1].trim();
					}
					else{
						command = commands[0].trim();
					}
					
					Map<String, Object>  results= new LinkedHashMap<>();
					
					Testing.run(shape.Key, command, data, results);
					
					if(results.containsKey("Preview")){
						shape.Preview = (String) results.get("Preview");
						JSONObject node =  getNodeData(nodeDataArray, Integer.toString(shape.Key));
						if(node != null){
							
							node.remove("preview");
							node.put("preview", shape.Preview) ;
						}
					}
					break;
				case "End":
					Testing.exit();	
					Testing.SaveTestCase(testCase.toString());
					break;
				}
			 }
			
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		finally{
			try{
				
				if(Testing.isStarted()){
					Testing.exit();
				}
			}
			catch(Exception e){
				e.printStackTrace();
				throw e;
			}
		}
		
		 return Response.ok(testCase.toString()).build(); 
	}
	
	private static byte[] getImageAsBytes(String path) throws IOException{
		File file = FileUtils.getFile(TestCaseIO.GetScreenShotPath(), path);
		if(file.exists() && file.isFile()){
			return FileUtils.readFileToByteArray(file);
		}		
		return null;
	}
	
	@GET
	@Path("/FindScreenShot")
	@Consumes({ "image/jpg", "image/png" })
	@Produces("image/png")
	public Response FindScreenShot(@QueryParam("path")String path) throws Exception {
				
		if(path.contains("~")){
			path = path.replaceAll("~", "\\\\");
		}
		
		byte[] imageBytes = getImageAsBytes(path);
		
//		response.setContentType("image/jpeg");
//		response.setContentLength(imageBytes.length);
//
//		response.getOutputStream().write(imageBytes);
		return Response.ok(imageBytes).build();
	}
	
	@POST
	@Path("/SaveTestCase")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public Response SaveTestCase(String testCaseModel) throws Exception {
		
		testCaseModel = testCaseModel.replace("testCaseModel=", "");
		
		final String model = testCaseModel;
		System.out.println("Data Received: " + testCaseModel); 	
		

		JSONObject testCase = new JSONObject(testCaseModel);
		
		 JSONArray nodeDataArray =  testCase.getJSONArray("nodeDataArray");
		 JSONArray linkDataArray =  testCase.getJSONArray("linkDataArray");
		
		ArrayList<Shape> shapes =  getShapesFromJSON(nodeDataArray, linkDataArray);
		Map<String, Object> parameters = new HashMap<>();
		getTestCaseName(shapes, parameters);
		String modelName =(String) parameters.get("TestCaseName");
		TestCaseIO.SaveTestCaseToWorkingPath(modelName, testCaseModel);
		
		 StreamingOutput fileStream =  new StreamingOutput() 
	        {
	            @Override
	            public void write(java.io.OutputStream output) throws IOException 
	            {
	            	  byte[] data = IOUtils.toByteArray(model);
	                    output.write(data);
	                    output.flush();
	            }
	        };
	        return Response
	                .ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
	                .header("content-disposition","attachment; filename = "+ modelName )
	                .build();
	}
	
	private void getTestCaseName(ArrayList<Shape> shapes, Map<String, Object> parameters){
		String modelName = "New Test Case.json";
		String Remote = "";
		String browser = "Chrome";
		for(int i =0; i < shapes.size(); i++){					
			
			String Category = shapes.get(i).Category;
			String Text = shapes.get(i).Text;
			
			if(!Category.equalsIgnoreCase("Comment")){
				continue;
			}
			
			String[] arr = Text.split("=>");
			String processpor = "";
			String data = "";
			
			if(arr.length > 1){
				processpor = arr[0].trim();
				data = arr[1].trim();
			}
			
			switch(processpor){
			case "Name":
				modelName = data;
				break;
			case "Remote":
				Remote = data;
				break;
			case "Browser":
				browser = data;
				break;
			}
		}
		
		if(modelName.isEmpty()){
			 modelName = "New Test Case.json";
		}
		else if (!modelName.contains(".json")){
			modelName = modelName + ".json";
		}
		
		parameters.put("TestCaseName", modelName);
		parameters.put("Remote", Remote);
		parameters.put("Browser", browser);
	}
	
	
	@POST
	@Path("/UploadRead")
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	@Produces({MediaType.TEXT_PLAIN})
	public Response UploadRead(@FormDataParam("inputUploadFile") InputStream uploadedInputStream)  throws JSONException
	{
		
		 String newString = "";
		

		try {
			byte[] bytes;
	        bytes = readInputStreamToByteArray(uploadedInputStream);
	        uploadedInputStream.close();
            newString = new String(bytes);
       
		} catch (IOException e) {

			e.printStackTrace();
		}
		
	        return Response
	                .ok(newString)
	                .build();
	}
	
	@POST
	@Path("/GetTestcases")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response GetTestcases()  throws IOException{
		
		JSONArray arr = new JSONArray();
		
		for(String test : TestCaseIO.getTestCases()){
			arr.put(test);
		}
		return Response.ok(arr.toString()).build();
	}
	
	
	@POST
	@Path("/GetTestcase")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response GetTestcase(String testcase)  throws IOException, JSONException{
		
		JSONObject obj = new JSONObject(testcase);
		
		return Response.ok(getTestCase( obj.getString("testcase"))).build();
	}
	
	public static String getTestCase(String testcase) throws IOException{
		File testCaseFile = new File(TestCaseIO.GetTestCasesPath(), testcase + ".json");
		
		if(testCaseFile.exists()){
			String content = FileUtils.readFileToString(testCaseFile);
			return content;
		}
		
		return null;
	}
	
	
	
	
	
	public static byte[] readInputStreamToByteArray(InputStream inputStream) {
	    if (inputStream == null) {
	        // normally, the caller should check for null after getting the InputStream object from a resource
	        //throw new FileProcessingException("Cannot read from InputStream that is NULL. The resource requested by the caller may not exist or was not looked up correctly.");
	    }
	    byte [] x = null;
	    try {
	        x =  IOUtils.toByteArray(inputStream);
	    } catch (IOException e) {
	    	try {
				inputStream.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        //throw new FileProcessingException("Error reading input stream.", e);
	    } finally {
	       // closeStream(inputStream);
	    }
	    return x;
	}
}
