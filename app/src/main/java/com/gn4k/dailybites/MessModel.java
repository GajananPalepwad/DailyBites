package com.gn4k.dailybites;

public class MessModel {

    String mobileNo;
    String dishPrize;

    public String getDishPrize() {
        return dishPrize;
    }

    public void setDishPrize(String dishPrize) {
        this.dishPrize = dishPrize;
    }

    public String getToDayDish() {
        return toDayDish;
    }

    public void setToDayDish(String toDayDish) {
        this.toDayDish = toDayDish;
    }

    String toDayDish;
    String messName;

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    String ratings;

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

    private double distance; // New property for storing distance

    // Existing getters and setters

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
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

    public MessModel(String mobileNo, String messName, String coverImage, Double latitude, Double longitude, String isVerified, double distance, String dishPrize, String toDayDish, String ratings) {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude and longitude values must not be null.");
        }
        this.coverImage = coverImage;
        this.mobileNo = mobileNo;
        this.messName = messName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isVerified = isVerified;
        this.distance = distance;
        this.toDayDish = toDayDish;
        this.dishPrize = dishPrize;
        this.ratings = ratings;
    }

    Double longitude;

}
