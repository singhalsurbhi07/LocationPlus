package com.example.locationproject;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

public class LocationService extends IntentService {
	private LocationManager locationManager;
	private String provider;
	//private LocationListener listener;

	public LocationService(String name) {
		super(name);
	}

	public LocationService() {
		super("LocationService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the locatioin provider -> use
		// default
		Criteria criteria = new Criteria();
		while(true) {
			provider = locationManager.getBestProvider(criteria, false);
			System.out.println("Provider " + provider + " was selected.");
			Location location = locationManager.getLastKnownLocation(provider);
			//listener = new Listener(getApplicationContext());

			// Initialize the location fields
			if (location != null) {
				System.out.println("Provider " + provider + " has been selected.");
				System.out.println(location.getLatitude() + " " + location.getLongitude());
				new AddressFinder(getApplicationContext()).saveAddress(location);
				//listener.onLocationChanged(location);
			} else {
				//Do nothing
			}
			try {
				Thread.sleep(15*60*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//Requesting updates
		//locationManager.requestLocationUpdates(provider, 400, 1, listener);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//locationManager.removeUpdates(listener);
	}
}
