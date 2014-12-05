package com.example.locationproject.datamodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Response implements Serializable {
	String deviceName;
	Map<String,Integer> addressMap = new HashMap<>();
	
	
	public Response(String deviceName, Map<String,Integer> resultVal){
		this.deviceName = deviceName;
		this.addressMap= resultVal;
		
	}


	public String getDeviceName() {
		return deviceName;
	}


	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}


	public Map<String, Integer> getAddressMap() {
		return addressMap;
	}


	public void setAddressMap(Map<String, Integer> addressMap) {
		this.addressMap = addressMap;
	}
	
	
}
