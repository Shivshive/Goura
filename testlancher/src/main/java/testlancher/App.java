package testlancher;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

import org.apache.commons.io.IOUtils;

public class App {

	public static void main(String[] args) throws IOException{
		while (true) {
            String _s = readMessage(System.in);
            sendMessage("Got Message " + _s);
        }
	}
	
	 private static String readMessage(InputStream in) throws IOException {
		 	//byte[] b = new byte[4];
		   // in.read(b);		    
		    
		    byte[] bytes = IOUtils.toByteArray(in);
		    
		   // char[] chars = text.toCharArray();
		   // int size = getInt(chars);		   

		   // if (size == 0) {
		   //   throw new InterruptedIOException("Blocked communication");
		   // }

		   // b = new byte[size];
		  //  in.read(b);
		    
		 //  int input = in.read();
		  //  while(input != -1){
		 //   	
		 //   }

		    return new String(bytes, "UTF-8");
		  }

		  private static void sendMessage(String message) throws IOException {
		    System.out.write(getBytes(message.length()));
		    System.out.write(message.getBytes("UTF-8"));
		    System.out.flush();		    
		  }

		  public static int getInt(char[] bytes) {
			    return  (bytes[3]<<24) & 0xff000000|
			            (bytes[2]<<16) & 0x00ff0000|
			            (bytes[1]<< 8) & 0x0000ff00|
			            (bytes[0]<< 0) & 0x000000ff;
		  }

		  public static byte[] getBytes(int length) {
		    byte[] bytes = new byte[4];
		    bytes[0] = (byte) (length & 0xFF);
		    bytes[1] = (byte) ((length >> 8) & 0xFF);
		    bytes[2] = (byte) ((length >> 16) & 0xFF);
		    bytes[3] = (byte) ((length >> 24) & 0xFF);
		    return bytes;
		  }

}
