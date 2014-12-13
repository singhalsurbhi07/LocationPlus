package com.example.locationproject.utils;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;

public class AddressConverter {
	private static final String URL = "http://maps.googleapis.com/maps/api/geocode/json";
	public static String convertToLongLat(String address){
		String theString = null;
		try {
			URL url = new URL(URL + "?address="
				    + URLEncoder.encode(address+" CA", "UTF-8") + "&sensor=false");
			URLConnection conn;
			conn = url.openConnection();
			InputStream in = conn.getInputStream() ;
			theString = IOUtils.toString(in, "UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return theString;
	}
}
