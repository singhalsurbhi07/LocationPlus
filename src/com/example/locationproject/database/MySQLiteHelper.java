package com.example.locationproject.database;



import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

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

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_LOCATION_CREATE);

	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION_CREATE);
		onCreate(db);		
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

	public void getAddressList() {

		SQLiteDatabase db = this.getReadableDatabase();
		HashMap<Integer, String> addressMap = new HashMap<Integer, String>();

		String queryStr = " SELECT COUNT(ADDRESS) AS CNT, ADDRESS FROM LOCATION_DATA GROUP BY ADDRESS ORDER BY CNT DESC LIMIT 2 ";

		//SELECT COUNT(ADDRESS) AS CNT, ADDRESS FROM LOCATION_DATA GROUP BY ADDRESS ORDER BY CNT DESC LIMIT 2;
		//SELECT ADDRESS, COUNT(ADDRESS) AS CNT FROM LOCATION_DATA GROUP BY ADDRESS ORDER BY CNT DESC LIMIT 2;

		Cursor cursor = db.rawQuery(queryStr, null);

		cursor.moveToFirst();

		while (!cursor.isAfterLast() ) {
			addressMap.put(cursor.getInt(0), cursor.getString(1));
			cursor.moveToNext();
		}
		cursor.close();

		if(db.isOpen()) 
			db.close();

	}

	public static String getCurrentTimeStamp(){
		DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS");
		Calendar cal = Calendar.getInstance();

		return dateFormat.format(cal.getTime()).toString();
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

}
