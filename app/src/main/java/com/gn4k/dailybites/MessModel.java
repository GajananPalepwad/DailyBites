package com.gn4k.dailybites;

public class MessModel {

    String mobileNo;
    String messName;
    MessModel(){

    }


    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getMessName() {
        return messName;
    }

    public void setMessName(String messName) {
        this.messName = messName;
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

    double latitude;

    public MessModel(String mobileNo, String messName, double latitude, double longitude) {
        this.mobileNo = mobileNo;
        this.messName = messName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    double longitude;

}
