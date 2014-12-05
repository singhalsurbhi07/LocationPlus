package com.example.locationproject.wifidirect;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.util.Log;

public class Utils {

	//private final static String p2pInt = "p2p-p2p0";
	private final static String p2pInt = "p2p-wlan0-0";
	private final static String p2pCInt = "wlan0";

	public static String getIPFromMac(String MAC) {
		/*
		 * method modified from:
		 * 
		 * http://www.flattermann.net/2011/02/android-howto-find-the-hardware-mac-address-of-a-remote-host/
		 * 
		 * */
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("/proc/net/arp"));
			String line;
			while ((line = br.readLine()) != null) {

				String[] splitted = line.split(" +");
				if (splitted != null && splitted.length >= 4) {
					// Basic sanity check
					String device = splitted[5];
					Log.d(WiFiDirectActivity.TAG, "device value Richa : " + device);
					if (device.matches(".*" +p2pCInt+ ".*")){
						Log.d(WiFiDirectActivity.TAG, "mac matches MAC ");
						String mac = splitted[3];
						Log.d(WiFiDirectActivity.TAG, "");
						Log.d(WiFiDirectActivity.TAG, "string mac : " + mac);
						Log.d(WiFiDirectActivity.TAG, "string MAC : " + MAC);
						if (mac.matches(MAC)) {
							Log.d(WiFiDirectActivity.TAG, "mac  matches MAC ");
							Log.d(WiFiDirectActivity.TAG, "Splitted[0] value Richa :" + splitted[0]);
							return splitted[0];
						}
					}
					
				}
				Log.d(WiFiDirectActivity.TAG, "MAC line Richa: " + line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	public static String getLocalIPAddress() {
		/*
		 * modified from:
		 * 
		 * http://thinkandroid.wordpress.com/2010/03/27/incorporating-socket-programming-into-your-applications/
		 * 
		 * */
		String ipAddrStrtotest = "";
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();

					String iface = intf.getName();
					Log.d(WiFiDirectActivity.TAG, "interface name getName() Richa: " + iface);
					if(iface.matches(".*" +p2pInt+ ".*")){
						if (inetAddress instanceof Inet4Address) { // fix for Galaxy Nexus. IPv4 is easy to use :-)
							Log.d(WiFiDirectActivity.TAG, "Yes IPV4 is okay!!! ");
							ipAddrStrtotest= getDottedDecimalIP(inetAddress.getAddress());
							Log.d(WiFiDirectActivity.TAG, "Yes IPV4return  is okay!!! " + ipAddrStrtotest);
							return getDottedDecimalIP(inetAddress.getAddress());
							
						}
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("AndroidNetworkAddressFactory", "getLocalIPAddress()", ex);
		} catch (NullPointerException ex) {
			Log.e("AndroidNetworkAddressFactory", "getLocalIPAddress()", ex);
		}
		return null;
	}

	private static String getDottedDecimalIP(byte[] ipAddr) {
		/*
		 * ripped from:
		 * 
		 * http://stackoverflow.com/questions/10053385/how -to-get-each-devices-ip-address-in-wifi-direct-scenario
		 * 
		 * */
		String ipAddrStr = "";
		for (int i=0; i<ipAddr.length; i++) {
			if (i > 0) {
				ipAddrStr += ".";
			}
			ipAddrStr += ipAddr[i]&0xFF;
		}
		Log.d(WiFiDirectActivity.TAG, "IP address returned in dotted decimal IP Richa: " + ipAddrStr);
		return ipAddrStr;
	}
}
