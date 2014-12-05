/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.locationproject.wifidirect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.locationproject.R;
import com.example.locationproject.utils.ExternalStorageUtil;
import com.example.locationproject.wifidirect.DeviceListFragment.DeviceActionListener;


/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {

	public static final String IP_SERVER = "192.168.49.1";
	public static int PORT = 8888;
	private static boolean server_running = false;

	protected static final int CHOOSE_FILE_RESULT_CODE = 20;
	private View mContentView = null;
	private WifiP2pDevice device;
	private WifiP2pInfo info;
	ProgressDialog progressDialog = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mContentView = inflater.inflate(R.layout.device_detail, null);
		mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				WifiP2pConfig config = new WifiP2pConfig();
				config.deviceAddress = device.deviceAddress;
				config.wps.setup = WpsInfo.PBC;
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
						"Connecting to :" + device.deviceAddress, true, true
						//                        new DialogInterface.OnCancelListener() {
						//
						//                            @Override
						//                            public void onCancel(DialogInterface dialog) {
						//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
						//                            }
						//                        }
				);
				((DeviceActionListener) getActivity()).connect(config);

			}
		});

		mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						((DeviceActionListener) getActivity()).disconnect();
					}
				});

		mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// Allow user to pick an image from Gallery or other
						// registered apps
						//Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						//Log.d(WiFiDirectActivity.TAG, "Reached here Richa1");
						//intent.setType("image/*");
						//intent.setType("text/plain");
						//startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
						//Log.d(WiFiDirectActivity.TAG, "Reached here Richa2");
						sendFile();
						
					}
				});

		return mContentView;
	}

	
	public void sendFile() {

		String localIP = Utils.getLocalIPAddress();
		// Trick to find the ip in the file /proc/net/arp
		String client_mac_fixed = new String(device.deviceAddress).replace("3a", "38");
		Log.d(WiFiDirectActivity.TAG, "Client MAC fixed Richa: " + client_mac_fixed);
		String clientIP = Utils.getIPFromMac(client_mac_fixed);
		Log.d(WiFiDirectActivity.TAG, "Local IP Address Richa: " + localIP);
		Log.d(WiFiDirectActivity.TAG, "Client IP Address Richa: " + clientIP);
		// User has picked an image. Transfer it to group owner i.e peer using
		// FileTransferService.
		File root = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		String path = root.getAbsolutePath()+"/healthplus/"+"request.json";
		//Uri uri = new Uri.Builder().appendPath(path);
	
		//Log.d(WiFiDirectActivity.TAG, "URI richa: " + uri);
		TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
		statusText.setText("Sending: " + path);
		//Log.d(WiFiDirectActivity.TAG, "Intent----------- " + uri);
		Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
		Log.d(WiFiDirectActivity.TAG, "Reached here Richa before send");
		serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
		//Log.d(WiFiDirectActivity.TAG, "Reached here Richa after send");
		//Log.d(WiFiDirectActivity.TAG, "Path : " + path);
		serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, path);
		Log.d(WiFiDirectActivity.TAG, "Reached here Richa: put extra string to URI");
		Log.d(WiFiDirectActivity.TAG, "Reached here Richa after send");
		Log.d(WiFiDirectActivity.TAG, "Reached here Richa IP server =" + IP_SERVER);
		Log.d(WiFiDirectActivity.TAG, "Reached here Richa LOCAL IP =" + localIP);
		if(localIP.equals(IP_SERVER)){
			Log.d(WiFiDirectActivity.TAG, "Reached here client IP1:" + clientIP);
			serviceIntent.putExtra(FileTransferService.EXTRAS_ADDRESS, clientIP);
			Log.d(WiFiDirectActivity.TAG, "Reached here client IP2:" + clientIP);
		}else{ 
			Log.d(WiFiDirectActivity.TAG, "Reached here  IP server1:" + IP_SERVER);
			serviceIntent.putExtra(FileTransferService.EXTRAS_ADDRESS, IP_SERVER);
			Log.d(WiFiDirectActivity.TAG, "Reached here Extra address2:" + IP_SERVER);
		}

		serviceIntent.putExtra(FileTransferService.EXTRAS_PORT, PORT);
		Log.d(WiFiDirectActivity.TAG, "Reached here Richa service started");
		getActivity().startService(serviceIntent);
	}

	@Override
	public void onConnectionInfoAvailable(final WifiP2pInfo info) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		this.info = info;
		this.getView().setVisibility(View.VISIBLE);

		// The owner IP is now known.
		TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText(getResources().getString(R.string.group_owner_text)
				+ ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
						: getResources().getString(R.string.no)));

		// InetAddress from WifiP2pInfo struct.
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

		mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);

		if (!server_running){
			new ServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text)).execute();
			server_running = true;
		}

		// hide the connect button
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
	}

	/**
	 * Updates the UI with device data
	 * 
	 * @param device the device to be displayed
	 */
	public void showDetails(WifiP2pDevice device) {
		this.device = device;
		this.getView().setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		view.setText(device.deviceAddress);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(device.toString());

	}

	/**
	 * Clears the UI fields after a disconnect or direct mode disable operation.
	 */
	public void resetViews() {
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView.findViewById(R.id.device_address);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.status_text);
		view.setText(R.string.empty);
		mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
		this.getView().setVisibility(View.GONE);
	}

	/**
	 * A simple server socket that accepts connection and writes some data on
	 * the stream.
	 */
	public static class ServerAsyncTask extends AsyncTask<Void, Void, String> {

		private final Context context;
		private final TextView statusText;

		/**
		 * @param context
		 * @param statusText
		 */
		public ServerAsyncTask(Context context, View statusText) {
			this.context = context;
			this.statusText = (TextView) statusText;
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				//ServerSocket serverSocket = new ServerSocket(PORT);
				Log.d(WiFiDirectActivity.TAG, "POSRT:" + PORT);
				ServerSocket serverSocket = new ServerSocket(PORT);
				Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
				Socket client = serverSocket.accept();
				Log.d(WiFiDirectActivity.TAG, "Server: connection done");
				final File f = new File(Environment.getExternalStorageDirectory() + "/"
						+ context.getPackageName() + "/healthplus-" + System.currentTimeMillis()
						+ ".json");

				File dirs = new File(f.getParent());
				if (!dirs.exists())
					dirs.mkdirs();
				f.createNewFile();

				Log.d(WiFiDirectActivity.TAG, "server: copying files " + f.toString());
				InputStream inputstream = client.getInputStream();
				
				copyFile(inputstream, new FileOutputStream(f));
				Log.d(WiFiDirectActivity.TAG, "server: Copy file success richa");
				serverSocket.close();
				Log.d(WiFiDirectActivity.TAG, "server: Socket closed richa");
				server_running = false;
				Log.d(WiFiDirectActivity.TAG, "server: file absolute path: " + f.getAbsolutePath());
				return f.getAbsolutePath();
			} catch (IOException e) {
				Log.e(WiFiDirectActivity.TAG, e.getMessage());
				return null;
			}
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				statusText.setText("File copied - " + result);
				Log.d(WiFiDirectActivity.TAG, "File copied result : : " + result);
				//Call readFile method
				ExternalStorageUtil util = new ExternalStorageUtil();
				String response = null;
				
				
				Log.d(WiFiDirectActivity.TAG, "RESPONSE :" + response);
				//Intent intent = new Intent();
				//intent.setAction(android.content.Intent.ACTION_VIEW);
				//intent.setDataAndType(Uri.parse("file://" + result), "*/*");
				//context.startActivity(intent);
			}

		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			statusText.setText("Opening a server socket");
		}

	}

	public static boolean copyFile(InputStream inputStream, OutputStream out) {
		byte buf[] = new byte[1024];
		int len;
		try {
			while ((len = inputStream.read(buf)) != -1) {
				//System.out.println("len =inputStream"+);
				out.write(buf, 0, len);

			}
			out.close();
			inputStream.close();
		} catch (IOException e) {
			Log.d(WiFiDirectActivity.TAG, e.toString());
			return false;
		}
		return true;
	}

}
