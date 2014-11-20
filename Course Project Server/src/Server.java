
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server 
{
	private ServerSocket serverSocket;
	private int port;

	public Server(int port) {
		this.port = port;
	}

	public void start() throws IOException {
		try{
			connect();
		}
		catch(Exception e){
			System.out.println("DHT likely offline!");
		}
		System.out.println("Starting the socket server at port:" + port);
		serverSocket = new ServerSocket(port);

		//Listen for clients. Block till one connects

		System.out.println("Waiting for clients...");
		Socket client = serverSocket.accept();

		//A client has connected to this server. Send welcome message
		sendWelcomeMessage(client);
	}

	public void connect() throws UnknownHostException, IOException{
		String hostname = "localhost";
		int DHTport = 9991;
		System.out.println("Attempting to connect to "+hostname+":"+DHTport);
		Socket DHTclient = new Socket(hostname,DHTport);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(DHTclient.getOutputStream()));
		writer.write(getIP());
		writer.flush();
		writer.close();
		System.out.println("Connection Established");
	}

	private void sendWelcomeMessage(Socket client) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		writer.write("Hello. You are connected to a Simple Socket Server. What is your name?\n");
		writer.write("IP is " + getIP());
		writer.flush();
		writer.close();
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
		int portNumber = 9990;

		try {
			// initializing the Socket Server
			Server socketServer = new Server(portNumber);

			socketServer.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
