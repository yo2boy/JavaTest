package net.Client;
import java.util.HashMap;


public class FileHandler {
	HashMap locations;
	
	public FileHandler(){
		locations  = new HashMap();
	}
	public void addMapping (String fileName, String filePath){
		if (locations != null && !locations.containsKey(fileName)){
			locations.put(fileName, filePath);
			try{
			Client.client.updateServersOfMapping(fileName);
			}
			catch(Exception e){
				System.out.println("Unable to send!");
			}
			System.out.println("sent to server");
		}
	}
	
	public String getFilePath(String fileName){
		if (locations != null && locations.containsKey(fileName)){
			return (String)(locations.get(fileName));
		}
		return "file not found";
	}
}
