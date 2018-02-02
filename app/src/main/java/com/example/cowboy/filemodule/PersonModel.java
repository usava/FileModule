package com.example.cowboy.filemodule;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Cowboy on 26.01.2018.
 */

public class PersonModel {

    @Attribute
    long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Element
    String name;
    @Element
    String date;
    @Element
    String time;
    @Element
    double latitude;
    @Element
    double longitude;
    @Element
    float distanceByStart;
    @Element
    String address;

    public PersonModel(long id, String name, String date, String time, double latitude, double longitude, float distanceByStart, double p10, double p2_5) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceByStart = distanceByStart;
    }

    public PersonModel(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public float getDistanceByStart() {
        return distanceByStart;
    }

    public void setDistanceByStart(float distanceByStart) {
        this.distanceByStart = distanceByStart;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "PersonModel{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", latitude=" + String.valueOf(latitude) +
                ", longitude=" + String.valueOf(longitude) +
                ", distanceByStart=" + String.valueOf(distanceByStart) +
                ", address='" + String.valueOf(address) + '\'' +
                '}';
    }

    public PersonModel mockModel(int id){
        this.setId(id);
        this.setName("Clone Slava Poliakov - "+id);
        this.setDate(new SimpleDateFormat("yyyy/MM/dd").format(new Date()).toString());
        this.setDate(new SimpleDateFormat("HH:mm:ss").format(new Date()).toString());
        this.setAddress("Dnipro Grushevskogo St. 9A 001 cab");
        this.setLatitude(50.009);
        this.setLongitude(-89.009);

        return this;
    }
}
