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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    Double latitude;

    public MessModel(String mobileNo, String messName, Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude and longitude values must not be null.");
        }
        this.mobileNo = mobileNo;
        this.messName = messName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    Double longitude;

}
