package com.app.dogsapp.objects;

public class Message {

    private String userName;
    private String userID;
    private String time;

    private String message;


    public Message(String time, String userName, String userID, String message) {
        this.time = time;
        this.userName = userName;
        this.userID = userID;
        this.message = message;
    }

    public Message() {
    }

    public String getUserName() {
        return userName;
    }

    public String getTime() {
        return time;
    }


    public String getMessage() {
        return message;
    }



}
