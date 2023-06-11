package com.gn4k.dailybites.RoomForCalender;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Calender {

    @PrimaryKey(autoGenerate = true)
    public long uid;
    public String date;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getMessNoR() {
        return messNoR;
    }

    public void setMessNoR(String messNoR) {
        this.messNoR = messNoR;
    }

    public String menu;

    public Calender(long uid, String date, String menu, String messNoR) {
        this.uid = uid;
        this.date = date;
        this.menu = menu;
        this.messNoR = messNoR;
    }

    @ColumnInfo(name = "Mess_no")
    public String messNoR;

}
