package com.app.dogsapp.objects;

import android.net.Uri;

import java.util.List;

public class Dog {


    public enum Size {
        LARGE, MEDIUM, SMALL;
    }

    public enum Attributes {
        FRIENDLY, PLAYFUL, GOODWITHPEOPLE
    }

    private String name;
    private Size size;
    private List<Attributes> attributesList;


    public Dog() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public List<Attributes> getAttributesList() {
        return attributesList;
    }

    public void setAttributesList(List<Attributes> attributesList) {
        this.attributesList = attributesList;
    }
}
