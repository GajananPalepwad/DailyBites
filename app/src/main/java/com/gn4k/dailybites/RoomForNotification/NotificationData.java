package com.gn4k.dailybites.RoomForNotification;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NotificationData {

    @PrimaryKey(autoGenerate = true)
    public long uid;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String time;

    public NotificationData(long uid, String title, String body, String time) {
        this.uid = uid;
        this.title = title;
        this.body = body;
        this.time = time;
    }

    @ColumnInfo(name = "title")
    public String title;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @ColumnInfo(name = "body")
    public String body;

}
