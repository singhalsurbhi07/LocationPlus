package com.example.locationproject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;


public class AddressFinder {
	private Context context;
	
	public AddressFinder(Context context) {
		this.context = context;
	}
	
	
	private class GetAddressTask extends AsyncTask<Location, Void, String> {

		Context mContext;
		public GetAddressTask(Context context) {
			super();
			mContext = context;
		}

		@Override
		protected String doInBackground(Location... params) {
			Geocoder geocoder =
					new Geocoder(mContext, Locale.getDefault());
			// Get the current location from the input parameter list
			Location loc = params[0];
			// Create a list to contain the result address
			List<Address> addresses = null;
			try {
				/*
				 * Return 1 address.
				 */
				addresses = geocoder.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 1);
			} catch (IOException e1) {
				Log.e("LocationSampleActivity",
						"IO Exception in getFromLocation()");
				e1.printStackTrace();
				return ("No address found");
			} catch (IllegalArgumentException e2) {
				// Error message to post in the log
				String errorString = "Illegal arguments " +
						Double.toString(loc.getLatitude()) +
						" , " +
						Double.toString(loc.getLongitude()) +
						" passed to address service";
				Log.e("LocationSampleActivity", errorString);
				e2.printStackTrace();
				return errorString;
			}
			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {
				// Get the first address
				Address address = addresses.get(0);
				/*
				 * Format the first line of address (if available),
				 * city, and country name.
				 */
				/*
				String addressText = String.format(
						"%s, %s, %s",
						// If there's a street address, add it
						address.getMaxAddressLineIndex() > 0 ?
								address.getAddressLine(0) : "",
								// Locality is usually a city
								address.getLocality(),
								// The country of the address
								address.getCountryName());
								*/
				// Return the text
				return address.getLocality();
			} else {
				return "No address found";
			}
		}

		@Override
		protected void onPostExecute(String address) {
			// Set activity indicator visibility to "gone"
			// mAddress.setText(address);
			// save in DB here
			System.out.println(address);
			MySQLiteHelperGeo sqlLite = new MySQLiteHelperGeo(this.mContext);
			try {
				if(!address.equals("No address found") && sqlLite.addLocationRow(0, 0, address) != -1) {
					System.out.println("address saved");

					Toast.makeText(mContext, address + " Values Inserted", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void saveAddress(Location mLocation) {
		// Ensure that a Geocoder services is available
		if (Build.VERSION.SDK_INT >=
				Build.VERSION_CODES.GINGERBREAD
				&&
				Geocoder.isPresent()) {
			/*
			 * Reverse geocoding is long-running and synchronous.
			 * Run it on a background thread.
			 * Pass the current location to the background task.
			 * When the task finishes,
			 * onPostExecute() displays the address.
			 */
			(new GetAddressTask(this.context)).execute(mLocation);
		}
	}
}
