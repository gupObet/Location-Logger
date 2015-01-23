package com.example.networkscanproj;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	LocationManager locMan;
	String provider, provider2;
	Boolean netWork_enabled = false;
	Boolean gps_enabled = false;
	private static long MINTIME;
	private static float MINDIS;
	Cursor cursor;
	NetworkScanDB GeoLocInfoDb;
	String row;
	double lat;
	double lon;
	double accur;
	double time;
	EditText etMinTime;
	EditText etMinDis;
	ListView lv;
	SimpleCursorAdapter sd;
	String[] columns;
	int[] to;
	boolean requestScan = false;

	private SharedPreferences prefs;
	private String prefName = "MyPref";
	String savedReqScan = "savedReqScan";
	String savedMinTime = "savedMinTime";
	String savedMinDis = "savedMinDis";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// initialize lv
		lv = (ListView) findViewById(R.id.listView1);

		// getting min time and distance from edit text
		etMinTime = (EditText) findViewById(R.id.et_minTime);
		etMinDis = (EditText) findViewById(R.id.et_minDis);

		// initiating location
		locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		provider = locMan.NETWORK_PROVIDER;
		
		provider2 = locMan.GPS_PROVIDER;

		try {
			netWork_enabled = locMan.isProviderEnabled(provider);
		} catch (Exception ex) {
		}
		
		try {
			gps_enabled = locMan.isProviderEnabled(provider2);
		} catch (Exception ex) {
		}

		
		 /* columns, to, sd, lv intialize here so it gets a new copy on every
		 * onCreate, not using old from previous onCreate
		 */

		columns = new String[] { NetworkScanDB.Key_RowID, NetworkScanDB.Key_Prov,
				NetworkScanDB.Key_Lat, NetworkScanDB.Key_Lon,
				NetworkScanDB.Key_Accur, NetworkScanDB.Key_Time };

		to = new int[] { R.id.t0, R.id.t5, R.id.t1, R.id.t2, R.id.t3, R.id.t4 };

		sd = new SimpleCursorAdapter(MainActivity.this, R.layout.nsrow, cursor,
				columns, to, 0); // had to change to api 11., 0=no query

		lv.setAdapter(sd); // this didn't fix the problem

	}

	LocationListener locationListenerNetwork = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub

			if (location == null) {
				return;
			}
			try {

				GeoLocInfoDb = new NetworkScanDB(MainActivity.this);
				GeoLocInfoDb.open();

				// insert row into DB
				GeoLocInfoDb.insertGeoLocInfo("network", location.getLatitude(),
						location.getLongitude(), location.getAccuracy(),
						location.getTime());

				cursor = GeoLocInfoDb.getGeoLocInfoCursor();
				
				bindDataToListView(cursor);
				
				GeoLocInfoDb.close();
				
			} catch (Exception e) {
				Log.w("nwscan", e.toString());
			}

		}
		

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	};
	
	LocationListener locationListenerGPS = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
			//copied from locationListenerNetwork
			if (location == null) {
				return;
			}
			try {

				GeoLocInfoDb = new NetworkScanDB(MainActivity.this);
				GeoLocInfoDb.open();

				// insert row into DB
				GeoLocInfoDb.insertGeoLocInfo("gps", location.getLatitude(),
						location.getLongitude(), location.getAccuracy(),
						location.getTime());

				cursor = GeoLocInfoDb.getGeoLocInfoCursor();
				
				bindDataToListView(cursor);
				
				GeoLocInfoDb.close();
				
			} catch (Exception e) {
				Log.w("nwscan", e.toString());
			}

			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
	};

	public void getNetworkLocation(View v) {

		MINTIME = Long.parseLong(etMinTime.getText().toString());
		MINDIS = Float.parseFloat(etMinDis.getText().toString());

		if (netWork_enabled) {

			requestScan = true;

			locMan.requestLocationUpdates(provider, MINTIME, MINDIS,
					locationListenerNetwork);

		} else {
			Toast.makeText(getApplicationContext(), "network not enabled",
					Toast.LENGTH_LONG).show();
		}

	}
	
	public void getGPSLocation(View v) {

		MINTIME = Long.parseLong(etMinTime.getText().toString());
		MINDIS = Float.parseFloat(etMinDis.getText().toString());

		if (gps_enabled) {

			requestScan = true;

			locMan.requestLocationUpdates(provider2, MINTIME, MINDIS,
					locationListenerGPS);

		} else {
			Toast.makeText(getApplicationContext(), "gps not enabled",
					Toast.LENGTH_LONG).show();
		}

	}
	
	public void clearList(View v) {
		
		/*GeoLocInfoDb = new NetworkScanDB(MainActivity.this);
		GeoLocInfoDb.open();*/
		
		

		
	}
	
	public void bindDataToListView(Cursor cursor) {
		
		

		sd = new SimpleCursorAdapter(MainActivity.this, R.layout.nsrow,
				cursor, columns, to, 0); // had to change to api 11.,
											// 0=no query

		// change the last four columns to double, string does not have
		// enough precision
		sd.setViewBinder(new ViewBinder() {
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {

				// only the last four columns need double precision
				if (columnIndex == cursor
						.getColumnIndex(NetworkScanDB.Key_Lat)) {
					double lat = cursor.getDouble(cursor
							.getColumnIndex(NetworkScanDB.Key_Lat));
					TextView textView = (TextView) view;
					textView.setText(String.format("%.6f", lat));
					return true;
				}

				if (columnIndex == cursor
						.getColumnIndex(NetworkScanDB.Key_Lon)) {
					double lon = cursor.getDouble(cursor
							.getColumnIndex(NetworkScanDB.Key_Lon));
					TextView textView = (TextView) view;
					textView.setText(String.format("%.6f", lon));
					return true;

				}

				if (columnIndex == cursor
						.getColumnIndex(NetworkScanDB.Key_Accur)) {
					double accur = cursor.getDouble(cursor
							.getColumnIndex(NetworkScanDB.Key_Accur));
					TextView textView = (TextView) view;
					textView.setText(String.format("%.6f", accur));
					return true;
				}

				if (columnIndex == cursor
						.getColumnIndex(NetworkScanDB.Key_Time)) {
					double time = cursor.getDouble(cursor
							.getColumnIndex(NetworkScanDB.Key_Time));
					TextView textView = (TextView) view;
					textView.setText(String.format("%.0f", time));
					return true;
				}

				return false;

			}
		});

		Toast.makeText(getApplicationContext(),
				"added new location onLocationChanged",
				Toast.LENGTH_LONG).show();

		lv = (ListView) findViewById(R.id.listView1);

		sd.notifyDataSetChanged();

		lv.setAdapter(sd);


	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// retrieve requestScan with SharedPreferences
		prefs = getSharedPreferences(prefName, MODE_PRIVATE);
		if (prefs.contains(savedReqScan)) {
			requestScan = prefs.getBoolean(savedReqScan, false); // false if "savedReqScan" not present
                                                                                                                            
			Toast.makeText(getApplicationContext(),
					"onResume, reqScan " + requestScan, Toast.LENGTH_LONG)
					.show();
		}

		if (prefs.contains(savedMinTime) && prefs.contains(savedMinDis)) {

			MINTIME = prefs.getLong(savedMinTime, 99);
			MINDIS = prefs.getFloat(savedMinDis, 100);
		}

		// if intent is to getNetworkScan, then open DB, and continue location
		// updates
		
		GeoLocInfoDb = new NetworkScanDB(MainActivity.this);
		GeoLocInfoDb.open();
		cursor = GeoLocInfoDb.getGeoLocInfoCursor();
		bindDataToListView(cursor);

		GeoLocInfoDb.close();
	

		if (requestScan) {
			locMan.requestLocationUpdates(provider, MINTIME, MINDIS,locationListenerNetwork);
			
			locMan.requestLocationUpdates(provider2, MINTIME, MINDIS, locationListenerGPS);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		locMan.removeUpdates(locationListenerNetwork);

		Toast.makeText(getApplicationContext(),
				"onPause,  reqScan ," + requestScan, Toast.LENGTH_LONG).show();

		prefs = getSharedPreferences(prefName, MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();

		// saved requestScan variable so when app returns, requestScan can be
		// retrieved
		editor.putBoolean(savedReqScan, requestScan);

		// save MinTime (long) and MinDis (float)
		editor.putLong(savedMinTime, MINTIME);
		editor.putFloat(savedMinDis, MINDIS);

		editor.commit();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	public void stopNetworkLocation(View v) {
		locMan.removeUpdates(locationListenerNetwork);
		locMan.removeUpdates(locationListenerGPS);

		requestScan = false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
