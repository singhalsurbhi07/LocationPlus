package com.example.locationproject.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.DropBoxManager.Entry;
import android.os.Environment;
import android.util.Log;

import com.example.locationproject.MySQLiteHelperGeo;
import com.example.locationproject.StaticReferenceDB;
import com.example.locationproject.datamodel.Response;

public class ExternalStorageUtil extends Activity  {

	private static String TAG = "ExternalStorageUtil";
	private static SharedPreferences sharedpreferences;
	//MySQLiteHelperGeo sqlHelper = new MySQLiteHelperGeo(this);
	//private static String responseDataType;

	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) ||
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}
	
//	

	
	public  void writeRequestToDownloads(String query,String deviceName){

		if(isExternalStorageWritable()){

			// Find the root of the external storage.
			// See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

			//File root = android.os.Environment.getExternalStoragePublicDirectory(Environment.getExternalStorageDirectory(),"healthplus");
			JSONObject obj = new JSONObject();
			//obj.put("deviceName", );
			try {
				//obj.put("type", "request");
				obj.put("query", query);
				obj.put("deviceType", deviceName);
				//obj.put("Type", "Request");
				//obj.put("userName",userName );
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			File file = new File(Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOWNLOADS), "location");
			if (!file.mkdirs()) {
				Log.d("External Storage dir ", "Directory not created");
			}
			Log.d("Externalstorage root=",file.getAbsolutePath());
			
			File reqfile = new File(file, "locationRequest.json");
			Log.d("ExternalStorage writeFile requestFileName",reqfile.getAbsolutePath());
			Log.d("ExternalStorage writeFile",file.getAbsolutePath());

			try {
				FileOutputStream f = new FileOutputStream(reqfile);
				PrintWriter pw = new PrintWriter(f);
				//pw.println(query);
				pw.write(obj.toString());
				pw.flush();
				pw.close();
				f.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				//Log.i(TAG, "******* File not found. Did you" +
				// " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
			} catch (IOException e) {
				e.printStackTrace();
			}   
		}else{
			Log.d("ExternalStorage writeFile","SD card not writable");
		}
		//tv.append("\n\nFile written to "+file);
	}

	/** Method to read in a text file placed in the res/raw directory of the application. The
	  method reads in all lines of the file sequentially. */

	public void writeResponseFileToDownloads(Map<String,Integer> resultVal,String deviceName) {
		JSONArray l = new JSONArray();
		for(java.util.Map.Entry<String, Integer> e : resultVal.entrySet()){
			Log.d(TAG,e.getKey()+"-->"+e.getKey()+" " +e.getValue());
			JSONObject tempObj = new JSONObject();
			try {
				tempObj.put("address", e.getKey());
				tempObj.put("frequency", e.getValue());
				l.put(tempObj);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		}
		Log.d(TAG,l.toString());

		if(isExternalStorageWritable()){

			JSONObject obj = new JSONObject();
			try {
				obj.put("deviceName", deviceName);
				obj.put("result", l);
				Log.d(TAG,obj.getString(obj.toString()));
				
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			Log.d(TAG+"finalObject",obj.toString());
			/*File file = new File(Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOWNLOADS), "healthplus");
			if (!file.mkdirs()) {
				Log.d("External Storage Directory ", "Directory not created");
			}
			Log.d("External Storage Root=",file.getAbsolutePath());*/

			File file = new File(Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOWNLOADS), "location");
			if (!file.mkdirs()) {
				Log.d("External Storage dir ", "Directory not created");
			}
			//Log.d("Externalstorage root=",file.getAbsolutePath());
			File resfile = new File(file, "locationResponse.txt");
			Log.d("ExternalStorage writeFile requestFileName",resfile.getAbsolutePath());
			Log.d("ExternalStorage writeFile",file.getAbsolutePath());


			try {
				FileOutputStream f = new FileOutputStream(resfile);
				PrintWriter pw = new PrintWriter(f);

				pw.write(obj.toString());
				pw.flush();
				pw.close();
				f.close();
				Log.d("ExternalStorage","sending file to master");


			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}   
		}else{
			Log.d("ExternalStorage writeFile","SD card not writable");
		}
	}


	public void readRequestFromSharedWiFi(String path, String deviceName) throws IOException, JSONException{

		if(isExternalStorageReadable()) {
			try {

				String requestPattern = "(.*)locationRequest(.*)";

				Pattern req  = Pattern.compile(requestPattern);

				Log.d("ExternalStorageUtil reqdRequestFromSharedWifi", path);
				Log.d("ExternalStorageUtil reqdRequestFromSharedWifi", " Request file path matching pattern" + req.matcher(path));

				if(req.matcher(path) != null) {

					BufferedReader reader = new BufferedReader(new FileReader(path));
					String line;
					StringBuilder outputString= new StringBuilder();
					Log.d("ReadFile" , " Reached Read File method!!");
					while((line = reader.readLine())!= null){
						outputString.append(line);
					}
					//Read from REQUEST JSON Object

					JSONObject jsonObj = new JSONObject(outputString.toString());
					Log.d("ExternalStorage read", jsonObj.getString("query"));

					//SQLiteDatabase db = StaticReferenceDB.sqlHelper.getReadableDatabase();
					

					Map<String,Integer> result =  StaticReferenceDB.sqlHelper.getKAddressList(jsonObj.getString("query"));
					File file = new File(path);
					boolean deleted = file.delete();
					Log.d("File", "Request file deleted!!");
					Log.d("ExternalStorage read", " Result :" + result);

					writeResponseFileToDownloads(result,deviceName);
				}

			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}			
		}
		else{
			Log.d("ExternalStorage write Response File ", " SD card not readable");
		}
	}


	//Read response file from Downloads folder
	public Response readResponFromSharedWiFi(String path) {
		Response userRes = null;
		if(isExternalStorageReadable()) {
			try {
				String responsePattern = "(.*)response(.*)";
				Pattern resp  = Pattern.compile(responsePattern);

				Log.d("readFile", path);
				Log.d(" readFile", " Response file path matching pattern" + resp.matcher(path));

				if(resp.matcher(path) != null) {

					BufferedReader respReader = new BufferedReader(new FileReader(path));
					String line;
					StringBuilder respString= new StringBuilder();
					Log.d("ReadFile" , " Reached Read File method!!");
					while((line = respReader.readLine())!= null){
						respString.append(line);
					}

					JSONObject respJSONObj = new JSONObject(respString.toString());
					JSONArray addressArray = respJSONObj.getJSONArray("result");
					Log.d(TAG,addressArray.toString());
					Map<String,Integer> resultAddressMap = createHashMap(addressArray);
					Log.d(TAG,"addressArrar = "+addressArray.toString());
					userRes = new Response(respJSONObj.getString("deviceName"),resultAddressMap);
					

					//Log.d(TAG , "Response " + respJSONObj.getString("userName"));
					//Log.d("readFile ", " Response " + respJSONObj.getString("type"));
					Log.d(TAG, " Response " + respJSONObj.getString("result"));
				
					//responseMap = new HashMap<>();

					//responseMap.put("userName", respJSONObj.getString("userName"));
					//responseMap.put("result", respJSONObj.getString("result"));
					//responseDataType = respJSONObj.getString("dataType");
					//responseMap.put(respJSONObj.getString("userName"),respJSONObj.getString("result"));

					//File file = new File(path);
					//boolean deleted = file.delete();
				}

			} catch (IOException e) {

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return userRes;
	}

	private Map<String, Integer> createHashMap(JSONArray addressArray) {
		Map<String,Integer> result = new HashMap<>();
		for(int i=0;i<addressArray.length();i++){
			try {
				JSONObject tempObj = addressArray.getJSONObject(i);
				result.put(tempObj.getString("address"),Integer.parseInt(tempObj.getString("frequency")));
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return result;
	}
}