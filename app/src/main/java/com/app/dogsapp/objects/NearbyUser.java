package com.app.dogsapp.objects;

import android.net.Uri;

public class NearbyUser {
    private String id;
    private double latitude;
    private double longitude;
    private String dogsName;
    private int friend;

    private Uri dogImageURI;
    private boolean hasImage;

    public NearbyUser(){


    }

    public Uri getDogImageURI() {
        return dogImageURI;
    }

    public void setDogImageURI(Uri dogImageURI) {
        this.dogImageURI = dogImageURI;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDogsName() {
        return dogsName;
    }

    public void setDogsName(String dogsName) {
        this.dogsName = dogsName;
    }

    public int getFriend() {
        return friend;
    }

    public void setFriend(int friend) {
        this.friend = friend;
    }
}
