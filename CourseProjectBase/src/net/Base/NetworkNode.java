package net.Base;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkNode {
	protected static final int GET_DHT_IP = 0;
	protected static final int FILE_UPDATE = 1;
	protected static final int REMOVE_CLIENT = 2;
	
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
}
