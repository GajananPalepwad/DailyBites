package com.gn4k.dailybites.Mess.OneDayPlanClasses;

public class OneDayUserModel {

    String address;
    String email;
    String latitude;
    String longitude;
    String mobileNo;

    public String getNumberOfPlate() {
        return numberOfPlate;
    }

    public void setNumberOfPlate(String numberOfPlate) {
        this.numberOfPlate = numberOfPlate;
    }

    String numberOfPlate;

    public OneDayUserModel(String address, String email, String latitude, String longitude, String mobileNo, String delivery, String name, String orderId, String time, String numberOfPlate) {
        this.address = address;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mobileNo = mobileNo;
        this.delivery = delivery;
        this.name = name;
        this.orderId = orderId;
        this.time = time;
        this.numberOfPlate = numberOfPlate;
    }

    public OneDayUserModel(){}

    String delivery;
    String name;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    String orderId;
    String time;

}
