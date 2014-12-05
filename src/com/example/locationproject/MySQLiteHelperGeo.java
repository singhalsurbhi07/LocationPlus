package com.example.locationproject;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;

import com.google.android.gms.internal.cu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelperGeo extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "LocationDB.db";

	public static final String TBL_LOCATION = "LOCATION_DATA";
	public static String COLUMN_ID = "TIMESTAMP";
	public static final String COLUMN_LATITUDE = "LATITUDE";
	public static final String COLUMN_LONGITUDE = "LONGITUDE";
	public static final String COLUMN_ADDRESS = "ADDRESS"; 

	private static final String TABLE_LOCATION_CREATE = "CREATE TABLE  IF NOT EXISTS " + TBL_LOCATION 
			+ " ( " + COLUMN_ID + " DATETIME PRIMARY KEY NOT NULL, " 
			+ COLUMN_LATITUDE + " REAL DEFAULT 0.0 , " 
			+ COLUMN_LONGITUDE + " REAL DEFAULT 0.0 , " 
			+ COLUMN_ADDRESS + " TEXT DEFAULT NULL );"	;

	public MySQLiteHelperGeo(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_LOCATION_CREATE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelperGeo.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION_CREATE);
		onCreate(db);		
	}

	public long addLocationRow(double latitude, double longitude, String address) throws JSONException {

		// Open database connection
		SQLiteDatabase db = this.getWritableDatabase();
		// Define values for each field
		ContentValues values = new ContentValues();

		values.put(COLUMN_ID, getCurrentTimeStamp());
		values.put(COLUMN_LATITUDE, latitude);
		values.put(COLUMN_LONGITUDE, longitude);
		values.put(COLUMN_ADDRESS, address); 

		// Insert Row
		long rowId=db.insert(TBL_LOCATION, null, values);
		db.close(); 
		return rowId;
	}
	
	public Map<String, Integer> getKAddressList(String queryStr) {

		SQLiteDatabase db = this.getReadableDatabase();
		Map<String, Integer> addressMap = new HashMap<String, Integer>();
		System.out.println("I came here 1");
		

		Cursor cursor = db.rawQuery(queryStr, null);
		System.out.println("I came here 2");

		System.out.println(cursor.moveToFirst());
		System.out.println("I came here 3");

		if(cursor.getCount() == 0) {
			return addressMap;
		}
		
		while(!cursor.isAfterLast()) {
			System.out.println("I came here 4");
			addressMap.put(cursor.getString(0), cursor.getInt(1));
			System.out.println("I came here 5");
			cursor.moveToNext();
		}
	
		cursor.close();
		System.out.println("I came here 6");


		if(db.isOpen()) 
			db.close();
		System.out.println("I came here 7");

		return addressMap;
	}
	
	public static String getCurrentTimeStamp(){
		return Long.toString(new Date().getTime());
	}
	
	static final String[] cities = {"San Jose", "San Francisco", "Mountain View", "Reno", 
		"Fremont", "San Diego", "Los Angeles", 
		"Camden", "Berkley", "Palo Alto", "Sunnyvale", "Redwood City", "Menlo Park", "Los Altos",  
		"Sunnyvale", "San Bruno", "San Mateo", "Los Gatos", "Saratoga", "Santa Clara", "Cupertino", "Milbrae"};



	private double latlong() {

		Random r = new Random();
		double randomValue = 1.1 + (300.0 - 1.1) * r.nextDouble();
		return randomValue;
	}

	private String timeGen() {

		long offset = Timestamp.valueOf("2014-11-01 00:00:00").getTime();
		long end = Timestamp.valueOf("2014-12-04 00:00:00").getTime();
		long diff = end - offset + 1;

		Timestamp rand = new Timestamp(offset + (long)(Math.random() * diff));
		return rand.toString();
	}

	public void insertRows() {
		long startTime = System.currentTimeMillis();

		for (int i = 0; i < 1000; ++i) {
			String insertRow = " Insert into LOCATION_DATA values('" + timeGen() + "', " + latlong() + ", " + latlong() 
					+ ", '" + cities[i % cities.length] + "' );";
			addLocationRow(timeGen(), latlong(), latlong(), cities[i % cities.length]);

		}
	}
	
	public void addLocationRow(String timestamp, double lat, double longitude, String city ) {

		// Open database connection
		SQLiteDatabase db = this.getWritableDatabase();
		// Define values for each field
		ContentValues values = new ContentValues();

		values.put(COLUMN_ID, timestamp);
		values.put(COLUMN_LATITUDE, lat);
		values.put(COLUMN_LONGITUDE, longitude);
		values.put(COLUMN_ADDRESS, city); 

		// Insert Row
		long rowId=db.insert(TBL_LOCATION, null, values);

		db.close(); 
	}
}