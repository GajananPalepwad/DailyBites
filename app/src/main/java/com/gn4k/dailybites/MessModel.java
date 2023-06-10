package com.gn4k.dailybites;

public class MessModel {

    String mobileNo;
    String messName;

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    String coverImage;

    public String getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }

    String isVerified;
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

    public MessModel(String mobileNo, String messName, String coverImage, Double latitude, Double longitude, String isVerified) {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude and longitude values must not be null.");
        }
        this.coverImage = coverImage;
        this.mobileNo = mobileNo;
        this.messName = messName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isVerified = isVerified;
    }

    Double longitude;

}
