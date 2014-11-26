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
			Client.client.updateServersOfMapping(fileName);
		}
	}
	
	public String getFilePath(String fileName){
		if (locations != null && locations.containsKey(fileName)){
			return (String)(locations.get(fileName));
		}
		return "file not found";
	}
}
