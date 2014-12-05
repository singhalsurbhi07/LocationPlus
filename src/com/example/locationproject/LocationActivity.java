package com.example.locationproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



import com.example.locationproject.services.ResponseFileService;
import com.example.locationproject.utils.ExternalStorageUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class LocationActivity extends Activity {
	private static String TAG = "LocationActivity";
	private ListView lvAddress;
	private List<String> addresses;
	private ArrayAdapter<String> addressAdapter;
	EditText k;
	SharedPreferences sharedpreferences;
	ExternalStorageUtil util;
	
	@Override
	protected void onResume() {
		super.onResume();
		onStartService();
		//getAddress();
	}
	
	private String getQuery(int kVal) {
		//addressAdapter.clear();
		MySQLiteHelperGeo db = new MySQLiteHelperGeo(this);

		String queryStr = "SELECT ADDRESS, COUNT(ADDRESS) AS CNT " +
				"FROM LOCATION_DATA " +
				"GROUP BY ADDRESS " +
				"ORDER BY CNT DESC " +
				"LIMIT "+k;
		return queryStr;
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		sharedpreferences = getSharedPreferences("APP_PREF", Context.MODE_PRIVATE);
		Editor editor = sharedpreferences.edit();
		Log.d(TAG,"Device Name = "+Build.MODEL);
		editor.putString("deviceName", Build.MODEL);
		onStartService();
		k = (EditText) findViewById(R.id.etKValue);
		util = new ExternalStorageUtil();
		//lvAddress = (ListView) findViewById(R.id.lvAddresses);

	}
	
	private void onStartService() {
		Intent intent = new Intent(this, LocationService.class);
		startService(intent);
	}
	
	public void getKTopLocations(View v){
		int kVal = Integer.parseInt(k.getText().toString());
		Log.d(TAG,"value of k = "+kVal);
		String query = getQuery(kVal);
		util.writeRequestToDownloads(query, sharedpreferences.getString("deviceName", "master"));
		String uri = "file://"+Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/location/locationRequest.json";
		Intent sendin  = new Intent();
		sendin.setAction(Intent.ACTION_SEND);
		sendin.putExtra(Intent.EXTRA_STREAM, Uri.parse(uri));
		sendin.setType("application/json");
		startActivityForResult(Intent.createChooser(sendin, "share file via"),0);
		Intent i = new Intent(this,ResponseFileService.class);
		startService(i);
		
		
	}
	
	public void onDataSync(MenuItem mi) {
	     // handle click here
		Log.d("UserActivity","OnSyncAction");
//		ApiCallHelper helper = new ApiCallHelper();
//		String date = DateUtil.getYesterdayDateString();
//		helper.getUserWaterData(date);
//		helper.getUserSleepData(date);
//		helper.getUserFoodData(date);
//		helper.getUserActivitiesData(date);
		MySQLiteHelperGeo sqlHelper = new MySQLiteHelperGeo(this.getApplicationContext());
		sqlHelper.insertRows();
		
		
		
		
	  }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_data_sync, menu);
		return true;
	}
	
	public void onWiFiConnect(MenuItem mi){
		Intent i = new Intent(this,com.example.locationproject.wifidirect.WiFiDirectActivity.class);
		startActivity(i);
	}
	
}
