package com.blindmatchrace;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.blindmatchrace.classes.C;
import com.blindmatchrace.classes.SendDataHThread;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Administrator activity. Shows a google map with option to add buoys on it.
 *
 */
public class AdminActivity extends FragmentActivity implements LocationListener, OnClickListener {

	// Application variables.
	private String user = "", event = "";
	private LocationManager locationManager;
	private boolean firstUse = true;
	private boolean disableLocation = false;
	private Marker[] markArr;
	private boolean[] greyedOut;

	// Views.
	private Marker currentPosition;
	private GoogleMap googleMap;
	private TextView tvLat, tvLng, tvUser, tvSpeed, tvDirection, tvEvent;
	private Button bBuoy1, bBuoy2, bBuoy3, bBuoy4, bBuoy5, bBuoy6, bBuoy7, bBuoy8, bBuoy9, bBuoy10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Disables lock-screen and keeps screen on.
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		initialize();
	}

	/**
	 * Initialize components.
	 */
	private void initialize() {
		// The user name and event number connected to the application.
		user = getIntent().getStringExtra(C.USER_NAME);
		event = getIntent().getStringExtra(C.EVENT_NUM);

		// Initialize location ability.
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		String provider = locationManager.getBestProvider(criteria, true);
		locationManager.requestLocationUpdates(provider, C.MIN_TIME, C.MIN_DISTANCE, this);

		// Initialize map.
		FragmentManager fm = getSupportFragmentManager();
		googleMap = ((SupportMapFragment) fm.findFragmentById(R.id.map)).getMap();

		// getting GPS & network status
		boolean isGPSEnabled = locationManager .isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkAvailable = MainActivity.isNetworkAvailable(this);

		if (!isGPSEnabled || !isNetworkAvailable){
			if (!isGPSEnabled && !isNetworkAvailable){
				new AlertDialog.Builder(this)
				.setTitle("Warning !")
				.setMessage("Your GPS & Network connections are disabled")
				.setNegativeButton(android.R.string.cancel, null)
				.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
					}
				}).create().show();
			}
			else if(!isGPSEnabled){
				new AlertDialog.Builder(this)
				.setTitle("Warning !")
				.setMessage("Your GPS connection is disabled")
				.setNegativeButton(android.R.string.cancel, null)
				.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				}).create().show();
			}
			else {
				new AlertDialog.Builder(this)
				.setTitle("Warning !")
				.setMessage("Your Network connection is disabled")
				.setNegativeButton(android.R.string.cancel, null)
				.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						startActivity(new Intent(android.provider.Settings.ACTION_NETWORK_OPERATOR_SETTINGS));
					}
				}).create().show();
			}
		}

		// Adds location button in the top-right screen.
		googleMap.setMyLocationEnabled(true);

		// Focus the camera on the latLng location.
		LatLng latLng = new LatLng(32.056286, 34.824598);
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, C.ZOOM_LEVEL);
		googleMap.animateCamera(cameraUpdate);

		// Initializing TextViews and Buttons.
		tvLat = (TextView) findViewById(R.id.tvLat);
		tvLng = (TextView) findViewById(R.id.tvLng);
		tvSpeed = (TextView) findViewById(R.id.tvSpeed);
		tvDirection = (TextView) findViewById(R.id.tvDirection);
		tvUser = (TextView) findViewById(R.id.tvUser);
		tvEvent = (TextView) findViewById(R.id.tvEvent);
		tvUser.setText(user.substring(6));
		tvEvent.setText(event);

		bBuoy1 = (Button) findViewById(R.id.bBuoy1);
		bBuoy2 = (Button) findViewById(R.id.bBuoy2);
		bBuoy3 = (Button) findViewById(R.id.bBuoy3);
		bBuoy4 = (Button) findViewById(R.id.bBuoy4);
		bBuoy5 = (Button) findViewById(R.id.bBuoy5);
		bBuoy6 = (Button) findViewById(R.id.bBuoy6);
		bBuoy7 = (Button) findViewById(R.id.bBuoy7);
		bBuoy8 = (Button) findViewById(R.id.bBuoy8);
		bBuoy9 = (Button) findViewById(R.id.bBuoy9);
		bBuoy10 = (Button) findViewById(R.id.bBuoy10);

		bBuoy1.setOnClickListener(this);
		bBuoy2.setOnClickListener(this);
		bBuoy3.setOnClickListener(this);
		bBuoy4.setOnClickListener(this);
		bBuoy5.setOnClickListener(this);
		bBuoy6.setOnClickListener(this);
		bBuoy7.setOnClickListener(this);
		bBuoy8.setOnClickListener(this);
		bBuoy9.setOnClickListener(this);
		bBuoy10.setOnClickListener(this);

		markArr = new Marker[10];
		greyedOut = new boolean[10];
		for (int i = 0; i < markArr.length; i++) {
			markArr[i] = null;
		}
	}

	@Override
	public void onBackPressed() {
		/*super.onBackPressed();

		// Disables the location changed code.
		disableLocation = true;
		finish();*/
		new AlertDialog.Builder(this)
		.setTitle("Really Exit?")
		.setMessage("Are you sure you want to exit?")
		.setNegativeButton(android.R.string.no, null)
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface arg0, int arg1) {
				AdminActivity.super.onBackPressed();

				// Disables the location changed code.
				disableLocation = true;
				finish();
			}
		}).create().show();
	}

	@Override
	public void onLocationChanged(Location location) {
		if (!disableLocation) {
			// If true- enables the buoy buttons.
			if (firstUse) {
				firstUse = false;
				bBuoy1.setEnabled(true);
				bBuoy2.setEnabled(true);
				bBuoy3.setEnabled(true);
				bBuoy4.setEnabled(true);
				bBuoy5.setEnabled(true);
				bBuoy6.setEnabled(true);
				bBuoy7.setEnabled(true);
				bBuoy8.setEnabled(true);
				bBuoy9.setEnabled(true);
				bBuoy10.setEnabled(true);
			}

			double dLat = location.getLatitude();
			double dLong = location.getLongitude();
			String lat = new DecimalFormat("##.######").format(dLat);
			String lng = new DecimalFormat("##.######").format(dLong);
			String speed = "" + location.getSpeed();
			String bearing = "" + location.getBearing();

			// Updates TextViews in layout.
			tvLat.setText(lat);
			tvLng.setText(lng);
			tvSpeed.setText(speed);
			tvDirection.setText(bearing);

			// Adds currentPosition marker to the google map.
			LatLng latLng = new LatLng(dLat, dLong);
			if (currentPosition != null) {
				currentPosition.remove();
			}
			currentPosition = googleMap.addMarker(new MarkerOptions().position(latLng).title("Current Position").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_sailor_user_low)));

			// Focus the camera on the currentPosition marker.
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, C.ZOOM_LEVEL);
			googleMap.animateCamera(cameraUpdate);
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

	@Override
	public void onClick(View v) {
		// The buoy name with the event number.
		String fullBuoyName = "";
		switch (v.getId()) {
		case R.id.bBuoy1:
			fullBuoyName = C.BUOY_PREFIX + "1_" + event;
			//bBuoy1.setEnabled(false);
			if(!greyedOut[0]) {
				bBuoy1.setBackgroundColor(Color.GRAY);
				greyedOut[0] = true;
				addBuoy(fullBuoyName, 0);
			}
			else {
				removeBuoy(bBuoy1, 0);
			}
			break;
		case R.id.bBuoy2:
			fullBuoyName = C.BUOY_PREFIX + "2_" + event;
			//bBuoy2.setEnabled(false);
			if(!greyedOut[1]) {
				bBuoy2.setBackgroundColor(Color.GRAY);
				greyedOut[1] = true;
				addBuoy(fullBuoyName, 1);
			}
			else {
				removeBuoy(bBuoy2, 1);
			}
			break;
		case R.id.bBuoy3:
			fullBuoyName = C.BUOY_PREFIX + "3_" + event;
			//bBuoy3.setEnabled(false);
			if(!greyedOut[2]) {
				bBuoy3.setBackgroundColor(Color.GRAY);
				greyedOut[2] = true;
				addBuoy(fullBuoyName, 2);
			}
			else {
				removeBuoy(bBuoy3, 2);
			}
			break;
		case R.id.bBuoy4:
			fullBuoyName = C.BUOY_PREFIX + "4_" + event;
			//bBuoy4.setEnabled(false);
			if(!greyedOut[3]) {
				bBuoy4.setBackgroundColor(Color.GRAY);
				greyedOut[3] = true;
				addBuoy(fullBuoyName, 3);
			}
			else {
				removeBuoy(bBuoy4, 3);
			}
			break;
		case R.id.bBuoy5:
			fullBuoyName = C.BUOY_PREFIX + "5_" + event;
			//bBuoy5.setEnabled(false);
			if(!greyedOut[4]) {
				bBuoy5.setBackgroundColor(Color.GRAY);
				greyedOut[4] = true;
				addBuoy(fullBuoyName, 4);
			}
			else {
				removeBuoy(bBuoy5, 4);
			}
			break;
		case R.id.bBuoy6:
			fullBuoyName = C.BUOY_PREFIX + "6_" + event;
			//bBuoy6.setEnabled(false);
			if(!greyedOut[5]) {
				bBuoy6.setBackgroundColor(Color.GRAY);
				greyedOut[5] = true;
				addBuoy(fullBuoyName, 5);
			}
			else {
				removeBuoy(bBuoy6, 5);
			}
			break;
		case R.id.bBuoy7:
			fullBuoyName = C.BUOY_PREFIX + "7_" + event;
			//bBuoy7.setEnabled(false);
			if(!greyedOut[6]) {
				bBuoy7.setBackgroundColor(Color.GRAY);
				greyedOut[6] = true;
				addBuoy(fullBuoyName, 6);
			}
			else {
				removeBuoy(bBuoy7, 6);
			}
			break;
		case R.id.bBuoy8:
			fullBuoyName = C.BUOY_PREFIX + "8_" + event;
			//bBuoy8.setEnabled(false);
			if(!greyedOut[7]) {
				bBuoy8.setBackgroundColor(Color.GRAY);
				greyedOut[7] = true;
				addBuoy(fullBuoyName, 7);
			}
			else {
				removeBuoy(bBuoy8, 7);
			}
			break;
		case R.id.bBuoy9:
			fullBuoyName = C.BUOY_PREFIX + "9_" + event;
			//bBuoy9.setEnabled(false);
			if(!greyedOut[8]) {
				bBuoy9.setBackgroundColor(Color.GRAY);
				greyedOut[8] = true;
				addBuoy(fullBuoyName, 8);
			}
			else {
				removeBuoy(bBuoy9, 8);
			}
			break;
		case R.id.bBuoy10:
			fullBuoyName = C.BUOY_PREFIX + "10_" + event;
			//bBuoy10.setEnabled(false);
			if(!greyedOut[9]) {
				bBuoy10.setBackgroundColor(Color.GRAY);
				greyedOut[9] = true;
				addBuoy(fullBuoyName, 9);
			}
			else {
				removeBuoy(bBuoy10, 9);
			}
			break;
		}
	}
	private void removeBuoy(final View v, final int index) {
		new AlertDialog.Builder(this)
		.setTitle("Remove Marker?")
		.setMessage("Do you want to remove buoy #" + (index + 1) + "from the map?")
		.setNegativeButton(android.R.string.no, null)
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface arg0, int arg1) {
				markArr[index].remove();
				markArr[index] = null;
				v.setBackgroundColor(Color.WHITE);
				greyedOut[index] = false;
				removeBuoyFromDB(C.BUOY_PREFIX + (index + 1) + "_" + event);
			}
		}).create().show();
	}

	private void removeBuoyFromDB(String fullBuoyName) {
		SendDataHThread thread = new SendDataHThread("RemoveBuoys", false);
		thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);

		thread.setFullUserName(fullBuoyName);
		thread.start();
	}
	private void addBuoy(String fullBuoyName, int index) {
		// HandlerThread for sending the buoy location to the DB through thread.
		SendDataHThread thread = new SendDataHThread("SendBuoys", true);
		thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);

		double dLat = currentPosition.getPosition().latitude;
		double dLong = currentPosition.getPosition().longitude;

		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator(','); 

		String lat = new DecimalFormat("##.######", otherSymbols).format(dLat);
		String lng = new DecimalFormat("##.######", otherSymbols).format(dLong);

		String speed = "" + 0;
		String bearing = "" + 0;

		thread.setFullUserName(fullBuoyName);
		thread.setLat(lat);
		thread.setLng(lng);
		thread.setSpeed(speed);
		thread.setBearing(bearing);
		thread.setEvent(event);
		thread.start();
		// Adds a buoy on the map.
		LatLng latLng = new LatLng(dLat, dLong);
		markArr[index] = googleMap.addMarker(new MarkerOptions().position(latLng).title(fullBuoyName.split("_")[0]).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_buoy_low)));
	}

}
