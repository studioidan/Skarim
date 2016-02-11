package com.studioidan.skarim.entities;

import java.util.Date;

public class Stop {
    public Date timeDoorOpen; // door opened
    public Date timeDoorClose; // door closed
    public int stopId;
    public int On; // number of incoming people
    public int Off; // number of outgoing people
    public int continue_by_survior; // number of people continuing
    public int continue_by_system; // number of people continuing by system
    public float lat;
    public float lon;

    public Stop(int stopId) {
        timeDoorOpen = new Date();
        this.stopId = stopId;
    }
}
