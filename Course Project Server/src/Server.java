
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
		ServerThread thread = new ServerThread(port);
		thread.start();
	}

	class ServerThread extends Thread
	{
		private ServerSocket serverSocket;
		int port;
		public ServerThread(int port) throws IOException{
			this.port = port;
			serverSocket = new ServerSocket(port);
		}
		public void run(){
			//Listen for clients. Block till one connects

			System.out.println("Waiting for clients...");

			while(true){
				Socket client;
				try {
					client = serverSocket.accept();
					if(client != null){
						SocketThread thread = new SocketThread(client);
						thread.start();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	class SocketThread extends Thread
	{
		Socket socket;
		public SocketThread(Socket s){
			this.socket = s;
		}
		public void run() {
			try {
				//A client has connected to this server. Send welcome message
				sendWelcomeMessage(socket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	public void connect() throws UnknownHostException, IOException{
		String hostname = "localhost";
		int serverPort = 9991;
		System.out.println("Attempting to connect to "+hostname+":"+serverPort);
		Socket serverClient = new Socket(hostname, serverPort);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(serverClient.getOutputStream()));
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
		// Setting a default port number for clients.
		int portNumber_C = 9990;
		// Setting a default port number for servers in the DHT.
		int portNumber_S = 9991;

		try {
			// initializing the Socket Server for the client
			Server socketServer_C = new Server(portNumber_C);

			// initializing the Socket Server for the server
			Server socketServer_S = new Server(portNumber_S);

			socketServer_S.start();
			socketServer_C.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
