package com.app.dogsapp.objects;

public class Location {

    private double latitude;
    private double longitude;
    private long lastUpdate;

    public Location(double latitude, double longitude,long lastUpdate) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastUpdate = lastUpdate;
    }

    public Location() {
    }

    public double getLatitude() {
        return latitude;
    }



    public double getLongitude() {
        return longitude;
    }



    public long getLastUpdate() {
        return lastUpdate;
    }


}
