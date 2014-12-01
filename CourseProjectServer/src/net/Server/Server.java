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
	static int portNumber_C;
	static int clientPort;
	// Setting a default port number for servers in the DHT.
	static int portNumber_S;
	HashMap records;
	static String nextServerIP;
	static int nextServerPort;
	static Server socketServer;
	static HashMap dataPackage;

	public Server()
	{
		records = new HashMap();
		dataPackage = new HashMap();
	}

	public void start() throws IOException
	{
		ServerThread clientThread = new ServerThread(portNumber_C,this);
		clientThread.start();
		ServerThread serverThread = new ServerThread(portNumber_S,this);
		serverThread.start();
	}

	class ServerThread extends Thread
	{
		private ServerSocket serverSocket;
		Server theServer;
		int port;
		public ServerThread(int port, Server server) throws IOException{
			this.port = port;
			serverSocket = new ServerSocket(port);
			this.theServer = server;
		}
		public void run(){
			//Listen for clients. Block till one connects

			System.out.println("Waiting for clients...");

			while(true){
				Socket client;
				try {
					client = serverSocket.accept();
					if(client != null){
						SocketThread thread = new SocketThread(client,theServer);
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
		Server server;
		public SocketThread(Socket s, Server server){
			this.socket = s;
			this.server = server;
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
					case FILE_UPDATE: records.put(stdIn.readLine(),stdIn.readLine());dataPackage.put("updated", "1");break;
					case REMOVE_CLIENT: removeClient(stdIn.readLine());dataPackage.put("updated", "1");break;
					case FILE_TRANSFER: acceptFile(Integer.parseInt(stdIn.readLine()), stdIn.readLine(), stdIn.readLine());break;
					case REQUEST_FILE_LIST: updateFileList(stdIn.readLine());break;
					default: break;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void updateFileList(String requesterIP){
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				if(dataPackage.containsKey("updated") && dataPackage.get("updated").equals("1")){
					Object[] files = records.keySet().toArray();
					Object[] IPs = records.values().toArray();
					for(int i = 0; i < files.length; i++){
						//if(!IPs[i].equals(requesterIP)){
						writer.write(files[i].toString());
						writer.newLine();
						//}
					}
				}
				writer.write("null");
				writer.newLine();

				writer.flush();
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

		//Incomplete method
		public void collectIP(String clientIP, String serverIPs) throws UnknownHostException{
			BufferedWriter writer;
			boolean initFlag = serverIPs.equals("");
			if(!serverIPs.contains(server.getIP()+":"+portNumber_C)){
				serverIPs += (server.getIP()+ ":" + portNumber_C) + ";";
				//send to next DHT server in ring
				try {
					Socket s = connect(nextServerIP,nextServerPort);
					writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
					writer.write(GET_DHT_IP + "");
					writer.newLine();
					writer.write(clientIP);
					writer.newLine();
					writer.write(serverIPs);
					writer.newLine();
					writer.flush();
				}
				catch(Exception e){

				}
				if(initFlag){
					while(!dataPackage.containsKey("GET_DHT_IP")){try {
						sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					}
					try{
						//We've collected all of the IPs, we can send it to the client now.
						serverIPs = (String)dataPackage.remove("GET_DHT_IP");
						String remainingIPs = serverIPs;
						String[] ips = new String[4];
						writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
						for(int i = 0; i < 4; i++){
							int index = remainingIPs.indexOf(";");
							ips[i] = remainingIPs.substring(0, index);
							remainingIPs = remainingIPs.substring(index+1);
							writer.write(ips[i]);
							writer.newLine();
						}
						writer.flush();
					}
					catch(Exception e){

					}
				}
			}
			else
			{
				dataPackage.put("GET_DHT_IP", serverIPs);
				/*try{
					//We've collected all of the IPs, we can send it to the client now.
					System.out.println("writing to client");
					Socket s = connect(clientIP, portNumber_C);
					String remainingIPs = serverIPs;
					String[] ips = new String[4];
					writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
					for(int i = 0; i < 4; i++){
						int index = remainingIPs.indexOf(";");
						ips[i] = remainingIPs.substring(0, index);
						remainingIPs = remainingIPs.substring(index+1);
						writer.write(ips[i]);
						writer.newLine();
					}
					writer.flush();
					System.out.println("writen");
					System.out.println(ips[0]+", "+ips[1]+", "+ips[2]+", "+ips[3]);
					System.out.println(serverIPs);
				}
				catch(Exception e){

				}*/

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



	public Socket connect(String IP, int customPort) throws IOException {
		Socket serverClient = new Socket(IP, customPort);
		return serverClient;
	}

	//Args are theClientPort, myClientPort, myServerPort, nextServerIP, nextServerPort
	public static void main(String[] args) {
		if(args.length > 0) {
			portNumber_C = Integer.parseInt(args[0]);
			if(args.length > 1){
				portNumber_S = Integer.parseInt(args[1]);
				if(args.length > 2){
					nextServerIP = args[2];
					if(args.length > 3){
						nextServerPort = Integer.parseInt(args[3]);
					}
				}
			}
		}

		try {
			// initializing the Socket Server for the client
			socketServer = new Server();

			socketServer.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
