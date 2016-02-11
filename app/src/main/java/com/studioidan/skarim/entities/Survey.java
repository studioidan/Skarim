package com.studioidan.skarim.entities;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Survey {
    public final String TAG = getClass().getName();

    public String tripid_plan;
    public String surveyorid;
    public String tablet_id = "";
    public Date date_start;
    public Date date_end;
    public List<Stop> stops;

    // final details
    public String busNumber;
    public String NumOfSits;
    public String BusKind;
    public String freeField1;
    public String freeField2;

    public List<LocationTrackItem> locationChain;


    public Survey(String deviceId) {
        tablet_id = deviceId;
        //date_start = new Date();
        //date_end = new Date();
        stops = new ArrayList<>();
        locationChain = new ArrayList<>();
    }

    public void start() {
        date_start = new Date();
    }

    public void finish() {
        date_end = new Date();
    }

    public void addLocationToChain(LocationTrackItem locationTrackItem) {
        if (locationChain.size() == 0) {
            locationChain.add(locationTrackItem);
            return;
        }
        //get the direction according to the last location item (if not the first location item)
        Location A = locationChain.get(locationChain.size() - 1).getLocation();
        Location B = locationTrackItem.getLocation();

        try {
            float deg = ((A.bearingTo(B) + 360) % 360);
            locationTrackItem.deg = deg;
            locationTrackItem.direction = LocationTrackItem.getDirection(deg);

            locationChain.add(locationTrackItem);
        } catch (Exception e) {
            locationTrackItem.deg = 0;
            locationTrackItem.direction = "NON";
        }

    }

    public int getContinueCount() {
        int up = 0, down = 0;
        for (Stop stop : stops) {
            up += stop.On;
            down += stop.Off;
        }
        int answer = up - down;
        Log.d(TAG, "continuing by system: " + answer);
        return answer;
    }
}
