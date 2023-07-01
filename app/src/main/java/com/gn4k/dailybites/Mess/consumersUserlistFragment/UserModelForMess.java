package com.gn4k.dailybites.Mess.consumersUserlistFragment;

public class UserModelForMess {
    String email;
    String latitude;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    String longitude;
    String address;


    public UserModelForMess(String email, String fromDate, String toDate, String name, String mobileNo, String longitude, String latitude, String address) {
        this.email = email;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.name = name;
        this.mobileNo = mobileNo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;

    }
    public UserModelForMess(){}
    String fromDate;
    String toDate;
    String name;

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    String mobileNo;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}