package com.gn4k.dailybites.consumersUserlistFragment;

public class UserModelForMess {
    String email;

    public UserModelForMess(String email, String fromDate, String toDate, String name, String mobileNo) {
        this.email = email;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.name = name;
        this.mobileNo = mobileNo;
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