package com.example.locationproject.services;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.locationproject.utils.ExternalStorageUtil;


public class FileLocator extends IntentService {
	private String TAG = "FileLocator";
	SharedPreferences sharedpreferences;
	boolean isReqFound = false;
	
	//String resPattern = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ShareViaWifi/response??.txt";
	ExternalStorageUtil util = new ExternalStorageUtil();

	// Must create a default constructor
	public FileLocator() {
		// Used to name the worker thread, important only for debugging.
		super("test-service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		sharedpreferences = 
				getSharedPreferences("APP_PREF", Context.MODE_PRIVATE);
		String deviceName = sharedpreferences.getString("deviceName","master");
		Log.d("FileLocator deviceName",deviceName);
		// This describes what will happen when service is triggered
		String reqPath = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/locationRequest.json";
		
		//String resPattern = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ShareViaWifi/response??.json";

		Log.d("FileLocator req path",reqPath);
		while(!(isReqFound)){
			Log.d(TAG,"Inside while loop");

			
				Log.d(TAG,"isReqFound = false");

				if(traverse(reqPath)){
					isReqFound = true;
					//ExternalStorageUtil util = new ExternalStorageUtil();
					Log.d("FileLocatorService", "Now read file");
					try {
						util.readRequestFromSharedWiFi(reqPath,deviceName);
					} catch (IOException | JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String uri = "file://"+Environment.getExternalStoragePublicDirectory(
							Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/location/locationResponse.txt";
					//Context con = getApplicationContext();
					Log.d(TAG,"read req done now sending the response");
					stopSelf();
					Intent i  = new Intent();
					i.setAction(Intent.ACTION_SEND);
					i.putExtra(Intent.EXTRA_STREAM, Uri.parse(uri));
					i.setType("text/rtf");
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
					break;
				
			}else{
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		Log.d("FileLocator","need to stop service...");
		



		// Log.d("onHandleIntent", name);

	}

	private boolean  traverse(String filePathString){
		File f = new File(filePathString);
		if(f.exists() && !f.isDirectory()){
			return true;
		}
		return false;
		//		    if (dir.isDirectory()) {
		//		        String[] children = dir.list();
		//		        for (int i = 0; children != null && i < children.length; i++) {
		//		            traverse(new File(dir, children[i]));
		//		        }
		//		    }
		//		    if (dir.isFile()) {
		//		        if (dir.getName().endsWith(".json"))
		//		               {
		//		            System.out.println(dir.getAbsolutePath());//change it if needed
		//		            return (dir.getAbsolutePath());
		//		        }
		//		    }
		//		    return null;
		//		
	}

	
}