package com.mountineer.mountaineer;

import java.util.Calendar;

/**
 * Created by Tobias on 16/02/16.
 */
public class MountaineerLocation {

    private String id;
    private String date;
    private String altitude;
    private String location;

    public MountaineerLocation(String id, String date, String altitude, String location) {
        this.id = id;
        this.date = date;
        this.altitude = altitude;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getAltitude() {
        return altitude;
    }

    public String getLocation() {
        return location;
    }
}
