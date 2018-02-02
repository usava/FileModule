package com.example.cowboy.filemodule;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Cowboy on 19.01.2018.
 */

public class FileModel {

    @ElementList
    private ArrayList<FileModel> list;
    @Attribute
    private String name;

    public FileModel(){}

    public FileModel(ArrayList<FileModel> list, String name) {
        this.list = list;
        this.name = name;
    }
    @Attribute
    long id;
    @Element
    String date;
    @Element
    String time;
    @Element
    double latitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    @Element

    double longitude;
    @Element
    float distanceByStart;
    @Element
    String address;

    public FileModel(long id, String date, String time, double latitude, double longitude, float distanceByStart, double p10, double p2_5) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceByStart = distanceByStart;
    }

    public FileModel mockModel(int id){
        this.setId(id);
        this.setName("Clone Slava Poliakov - "+id);
        this.setDate(new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
        this.setDate(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        this.setAddress("Dnipro Grushevskogo St. 9A 001 cab");
        this.setLatitude(50.009);
        this.setLongitude(-89.009);

        return this;
    }

    @Override
    public String toString() {
        return "FileModel{" +
                "id=" + String.valueOf(id) +
                ", date='" + String.valueOf(date) + '\'' +
                ", time='" + String.valueOf(time)+ '\'' +
                ", latitude=" + String.valueOf(latitude) +
                ", longitude=" + String.valueOf(longitude) +
                ", distanceByStart=" + String.valueOf(distanceByStart) +
                ", address='" + String.valueOf(address) + '\'' +
                '}';
    }

}
