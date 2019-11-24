package com.app.dogsapp.objects;

import android.net.Uri;

public class User {


    private String userName;
    private Location location;
    private Dog dog;



    public User(String userName,Dog dog,Location location) {

        this.userName = userName;
        this.dog = dog;
        this.location=location;
    }




    public User(){

    }


    public Location getLocation() {
        return location;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Dog getDog() {
        return dog;
    }

    public void setDog(Dog dog) {
        this.dog = dog;
    }


}
