package com.example.locationproject.datamodel;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class LongLatModel implements Serializable {
	String name;
	String latitude;
	String longitude;
	
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		
		this.latitude = latitude;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public LongLatModel(String jsonAdd,String name){
		try {
			this.name = name;
			JSONObject obj = new JSONObject(jsonAdd);
			JSONArray res = obj.getJSONArray("results");
			JSONObject arrayElement = res.getJSONObject(0);
			latitude = arrayElement.getJSONObject("geometry").getJSONObject("location").getString("lat");
			longitude = arrayElement.getJSONObject("geometry").getJSONObject("location").getString("lng");
			Log.d("LatlongModel","lat="+latitude+" longitude="+longitude);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
