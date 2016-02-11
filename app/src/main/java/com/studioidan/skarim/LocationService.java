package com.studioidan.skarim;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationService extends Service implements LocationListener {
	LocationManager lm;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		initLocationManager();
		super.onCreate();
	}
	private void initLocationManager() {
		lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		/*
		Criteria myCriteria = new Criteria();
		myCriteria.setAccuracy(Criteria.ACCURACY_HIGH);
		myCriteria.setPowerRequirement(Criteria.POWER_HIGH);
		// let Android select the right location provider for you
		String myProvider = lm.getBestProvider(myCriteria, true); 
		if(myProvider==null)
		{
			Toast.makeText(getApplicationContext(), "No GPS enabled", Toast.LENGTH_SHORT).show();
			return;
		}
		lm.requestLocationUpdates(myProvider,1*1000*20,0,this);  // one minute	
		*/
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*5, 0, this);
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Log.i("Location service","new location! " + location.getLatitude()  +", " + location.getLongitude());
		Intent intent = new Intent("NEW_LOCATION");		
		intent.putExtra("loc", location);
		intent.putExtra("speed", (location.getSpeed()*1000)/60);
		sendBroadcast(intent);
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.i("GPS","status changed: " + provider);

	}
	@Override
	public void onProviderEnabled(String provider) {
		Log.i("GPS","provider enabled: " + provider);

	}
	@Override
	public void onProviderDisabled(String provider) {
		Log.i("GPS","provider disabled: " + provider);
	}
	@Override
	public void onDestroy() {
		lm.removeUpdates(this);
		super.onDestroy();
	}
}
