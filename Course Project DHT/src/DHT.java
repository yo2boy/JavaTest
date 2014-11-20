import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class DHT
{

	private ServerSocket serverDHT;
    private int port;
    
    public DHT(int port) {
        this.port = port;
    }
    
    public void start() throws IOException {
        System.out.println("Starting the socket server at port:" + port);
        serverDHT = new ServerSocket(port);
        
        //Listen for clients. Block till one connects
        
        System.out.println("Waiting for clients...");
        Socket client = serverDHT.accept();
        
        //A client has connected to this server. Send welcome message
        //sendWelcomeMessage(client);
        BufferedReader in;
        in = new BufferedReader(new InputStreamReader(
                client.getInputStream()));
        String inputLine, outputLine;

        inputLine = in.readLine();
        System.out.println(inputLine);
    }
    
	public String getIP() throws UnknownHostException{
    	InetAddress ip = InetAddress.getLocalHost();
    	return bytesToIP(ip.getAddress());
    }
    
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    
    private static String bytesToIP(byte[] bytes) {
        String IP = "";
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            IP += Integer.parseInt(new String(new char[]{hexArray[v >>> 4],hexArray[v & 0x0F]})+"",16);
            IP += ".";
        }
        return IP.substring(0, IP.length() - 1);
    }
    
    public static void main(String[] args) {
        // Setting a default port number.
        int portNumber = 9991;
        
        try {
            // initializing the Socket Server
            DHT socketDHT = new DHT(portNumber);
            
            socketDHT.start();
            
            } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
