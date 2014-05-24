package com.blindmatchrace.classes;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.HandlerThread;
import android.util.Log;

/**
 * HandlerThread for sending the data to DB.
 * 
 */
public class SendDataHThread extends HandlerThread {

	private HttpURLConnection urlConnection;
	private String lat, lng, speed, bearing, event;
	private String name, fullUserName;
	private boolean add;

	public SendDataHThread(String name, boolean add) {
		super(name);
		this.name = name;
		this.add = add;
	}

	@Override
	public void run() {
		httpConnSendData();
	}

	/**
	 * Creates the HTTP connection for sending data to DB.
	 */
	private void httpConnSendData() {
		try {
			URL url;
			if(add) {
			url = new URL(C.URL_INSERT_CLIENT + "&Latitude=" + lat +"&Longitude=" + lng +"&Pressure="+ speed + "&Azimuth="+ bearing + "&Bearing=" + bearing + "&Information=" + fullUserName + "&Event=" + event);
			}
			else {
				url = new URL(C.URL_DELETE_CLIENT + "&Information=" + fullUserName);
			}
			urlConnection = (HttpURLConnection) url.openConnection();
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String result = br.readLine();
				Log.i("from DB", result);
				if (!result.toLowerCase().startsWith("ok")) { // Something is wrong.
					Log.i(name, "Not OK!");
				}
				else { // Data sent.
					Log.i(name, "OK!");
				}
			}
			catch (IOException e) {
				Log.i(name, "IOException");
				urlConnection.disconnect();
			}
			urlConnection.disconnect();
		}
		catch (MalformedURLException e) {
			Log.i(name, "MalformedURLException");
		}
		catch (IOException e) {
			Log.i(name, "IOException");
		}
	}

	// Getters and Setters.
	public String getFullUserName() {
		return fullUserName;
	}

	public void setFullUserName(String fullUserName) {
		this.fullUserName = fullUserName;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getBearing() {
		return bearing;
	}

	public void setBearing(String bearing) {
		this.bearing = bearing;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

}
