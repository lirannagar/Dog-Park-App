package com.app.dogsapp.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Park implements Parcelable {

    private String id;
    private String parkName;
    private double latitude;
    private double longitude;
    private double radius;



    public Park() {
    }

    protected Park(Parcel in) {
        id = in.readString();
        parkName = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        radius = in.readDouble();
    }

    public static final Creator<Park> CREATOR = new Creator<Park>() {
        @Override
        public Park createFromParcel(Parcel in) {
            return new Park(in);
        }

        @Override
        public Park[] newArray(int size) {
            return new Park[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
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

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(parkName);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeDouble(radius);

    }
}
