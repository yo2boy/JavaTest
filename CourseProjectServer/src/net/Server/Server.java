package net.Server;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import javax.imageio.ImageIO;
//import common.ByteArrayConversion;

import net.Base.NetworkNode;

public class Server extends NetworkNode
{
	private int port;
	private static final String image = "image.jpg";
	private static byte[] byteImage;
	public static FileInputStream fis = null;
	HashMap records;

	public Server(int port)
	{
		this.port = port;
		records = new HashMap();
	}

	public void start() throws IOException
	{
		try
		{
			connect();
		}
		catch(Exception e)
		{
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
				//A client has connected to this server. Interpret message type
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				int messageType = Integer.getInteger(stdIn.readLine()).intValue();
				switch(messageType)
				{
					case GET_DHT_IP: collectIP(stdIn.readLine(),stdIn.readLine());break;
					case FILE_UPDATE: records.put(stdIn.readLine(),stdIn.readLine());break;
					case REMOVE_CLIENT: removeClient(stdIn.readLine());break;
					default: break;
				}
				stdIn.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//Complete method
	public void removeClient(String IP){
		Collection v = records.values();
		while(v.contains(IP))
		{
			v.remove(IP);
		}
	}

	//Incomplete method
	public void collectIP(String clientIP, String serverIPs) throws UnknownHostException{
		if(!serverIPs.contains(this.getIP())){
			serverIPs += this.getIP() +";";
			//send to next DHT server in ring
		}
		else
		{
			//We've collected all of the IPs, we can send it to the client now.
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
		//writer.write("Hello. You are connected to a Simple Socket Server. What is your name?\n");
		writer.write("" + getIP()); //Server's IP
		writer.flush();
		writer.close();
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

			ServerSocket serverSocket = new ServerSocket(portNumber_C);

			Socket socket;

			socket = serverSocket.accept();

			while (true) {
				File file = new File ("/" + image);
				BufferedImage bufferedImage = ImageIO.read(file);

				byteImage = new byte[(int) file.length()];

				fis = new FileInputStream(file);
				fis.read(byteImage);
				fis.close();

				//byteImage = ByteArrayConversion.toByteArray(bufferedImage);

				System.out.println(byteImage.toString());

				OutputStream os = socket.getOutputStream();

				ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(byteImage);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
