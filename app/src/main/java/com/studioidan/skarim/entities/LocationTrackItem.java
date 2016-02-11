package com.studioidan.skarim.entities;

import android.location.Location;

import java.io.Serializable;
import java.util.Date;


public class LocationTrackItem implements Serializable {
    public double lat;
    public double lon;
    public String DoorMode;
    public Date date;
    public String tripId;
    public String surviorId;
    public float deg;
    public String direction;
    public float speed = 0;

    public LocationTrackItem() {
        date = new Date();
    }

    public LocationTrackItem(double lat, double lon, String doorMode, String tripId, String surviorId) {
        date = new Date();
        this.lat = lat;
        this.lon = lon;
        DoorMode = doorMode;
        this.tripId = tripId;
        this.surviorId = surviorId;
        direction = "NON";
    }

    public Location getLocation() {
        Location answer = new Location("answer");
        answer.setLatitude(lat);
        answer.setLongitude(lon);
        return answer;
    }

    public static String getDirection(float deg) {
        String dir = "";
        if (deg > 30 && deg <= 60) {
            dir = "NE";
        } else if (deg > 60 && deg <= 120) {
            dir = "E";
        } else if (deg > 120 && deg <= 150) {
            dir = "SE";
        } else if (deg > 150 && deg <= 210) {
            dir = "S";
        } else if (deg > 210 && deg <= 240) {
            dir = "SW";
        } else if (deg > 240 && deg <= 300) {
            dir = "W";
        } else if (deg > 300 && deg <= 330) {
            dir = "WN";
        } else // between 330 to 30
        {
            dir = "N";
        }

        return dir;
    }
}
