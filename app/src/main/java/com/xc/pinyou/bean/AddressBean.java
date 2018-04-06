package com.xc.pinyou.bean;

/**
 * Created by xum19 on 2018/2/11.
 */

public class AddressBean {
    private double latitude;
    private double longitude;
    private String city;
    private String address;

    public AddressBean(){

    }

    public AddressBean(double latitude, double longitude, String city, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
}
