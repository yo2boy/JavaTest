package net.Server;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import javax.imageio.ImageIO;

import net.Base.NetworkNode;

public class Server extends NetworkNode
{
	private int port;
	private static final String image = "image.jpg";
	private static byte[] byteImage;
	public static FileInputStream fis = null;
	HashMap records;
	static int serverNum;

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
			System.out.println("Something happened");
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
				String message = stdIn.readLine();
				if(message != null){
					int messageType = Integer.parseInt(message);
					switch(messageType)
					{
					case GET_DHT_IP: collectIP(stdIn.readLine(),stdIn.readLine());break;
					case FILE_UPDATE: records.put(stdIn.readLine(),stdIn.readLine());break;
					case REMOVE_CLIENT: removeClient(stdIn.readLine());break;
					case FILE_TRANSFER: acceptFile(Integer.parseInt(stdIn.readLine()), stdIn.readLine(), stdIn.readLine());break;
					default: break;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void acceptFile(int length, String fileName, String IP) throws IOException{
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.write("ready");
			writer.newLine();
			writer.flush();
			byte[] data = new byte[length];
			int bytesRead;
			int current = 0;
			InputStream is = socket.getInputStream();
			File f = new File(System.getProperty("user.home"),"images");
			f.mkdir();
			FileOutputStream fos = new FileOutputStream(f.getAbsolutePath()+"/"+fileName);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			for(int i = 0; i< length; i++){
				data[i] = (byte) is.read();
				bos.write(data[i]);
			}
			bos.flush();
			bos.close();
			System.out.println("Success!");
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

	public void connect() throws IOException{
		String hostname = "10.16.153.219";
		int serverPort = 9994;
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
		writer.write("" + getIP()); //Server's IP
		writer.flush();
		writer.close();
	}

	public static void main(String[] args) {
		if(args.length > 0) {
			serverNum = Integer.parseInt(args[0]);
			System.out.println("I am server " + args[0]);
		}
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
