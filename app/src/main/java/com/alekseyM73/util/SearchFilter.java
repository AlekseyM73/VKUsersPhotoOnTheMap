package com.alekseyM73.util;

public class SearchFilter {
    private int sex;
    private double latitude;

    private double longitude;
    private String radius;
    private String ageStart;
    private String ageFinish;


    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
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

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public void setAgeStart(String ageStart) {
        this.ageStart = ageStart;
    }

    public void setAgeFinish(String ageFinish) {
        this.ageFinish = ageFinish;
    }

    public String getRadius() {
        return radius;
    }

    public String getAgeStart() {
        return ageStart;
    }

    public String getAgeFinish() {
        return ageFinish;
    }
}
