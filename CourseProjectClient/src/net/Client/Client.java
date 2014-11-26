package net.Client;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import net.Base.NetworkNode;

public class Client extends NetworkNode
{	
    private String hostname;
    private int port;
    static FrameHandlerClient fhc;
    static FileHandler fh;
    Socket socketClient;
    static String[] serverIPs = new String[4];
    static int[] serverPorts = new int[4];
    public static Client client;
    
    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void connect() throws UnknownHostException, IOException {
        System.out.println("Attempting to connect to " + hostname + ":" + port);
        socketClient = new Socket(hostname, port);
        System.out.println("Connection Established");
        //stupid comment
    }
    
    public void connect(String IP, int customPort) throws UnknownHostException, IOException {
        System.out.println("Attempting to connect to " + IP + ":" + customPort);
        socketClient = new Socket(IP, customPort);
        System.out.println("Connection Established");
        //stupid comment
    }

    public String readResponse() throws IOException {
        String userInput;
        String msg = "";
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));

        System.out.println("Response from server:");
        while ((userInput = stdIn.readLine()) != null) {
            msg += userInput;
        }
        stdIn.close();
        return msg;
    }

    public void init(){
        //put something here
    }
    
    public static int getServerNumberFromFileName(String fileName){
    	//hash algorithm
    	int sum = 0;
    	byte[] name = fileName.getBytes();
    	for(int i = 0; i < name.length; i++){
    		sum += name[i];
    	}
    	return sum%4 + 1;
    }
    
    public void updateServersOfMapping(String fileName){
    	//something goes here. Used to update servers with a list of files that this client could transfer
    	int serverNum = getServerNumberFromFileName(fileName);
    	try {
			connect(serverIPs[serverNum],serverPorts[serverNum]);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
			writer.write(FILE_UPDATE);
			writer.newLine();
			writer.write(fileName);
			writer.newLine();
			writer.write(client.getIP());
			writer.newLine();

		} catch (UnknownHostException e) {

			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
    }
    
    public static void main(String arg[]){
        //Creating a SocketClient object
    	fh = new FileHandler();
    	fhc = new FrameHandlerClient(fh);

    	Frame frame = fhc.getFrame();
    	
        client = new Client ("localhost",9990);
        String ipOfServer1 = "";
        try {
            //trying to establish connection to the server
            client.connect();

            //if successful, read response from server
            ipOfServer1 = client.readResponse();
            System.out.println(ipOfServer1);

        } catch (UnknownHostException e) {
            System.err.println("Host unknown. Cannot establish connection");
        } catch (IOException e) {
            System.err.println("Cannot establish connection. Server may not be up."+e.getMessage());
        }
    }
}
