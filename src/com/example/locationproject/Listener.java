package com.example.locationproject;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

public class Listener implements LocationListener {
	private Context context;
	Listener(Context context) {
		this.context = context;
	}
	@Override
	public void onLocationChanged(Location location) {
		int lat = (int) (location.getLatitude());
		int lng = (int) (location.getLongitude());
		//save latlong to DB here
		new AddressFinder(this.context).saveAddress(location);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(context, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this.context, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}
}
