package com.app.dogsapp.objects;

import android.net.Uri;

public class Friend {

    private String id;
    private String dogsName;
    private boolean friend;
    private Uri dogImageUri;
    private boolean hasImage;
    private boolean online;


    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public Friend() {
    }

    public Uri getDogImageUri() {
        return dogImageUri;
    }

    public void setDogImageUri(Uri dogImageUri) {
        this.dogImageUri = dogImageUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDogsName() {
        return dogsName;
    }

    public void setDogsName(String dogsName) {
        this.dogsName = dogsName;
    }

    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }
}
