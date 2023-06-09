package com.gn4k.dailybites.RoomForWhishList;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Wishlist {

    @PrimaryKey(autoGenerate = true)
    public long uid;
    public String isVerify;

    public String getUrlCover() {
        return urlCover;
    }

    public void setUrlCover(String urlCover) {
        this.urlCover = urlCover;
    }

    public String getIsVerify() {
        return isVerify;
    }

    public void setIsVerify(String isVerify) {
        this.isVerify = isVerify;
    }

    public String urlCover;

    public Wishlist(long uid, String messNameR, String messNoR, String urlCover, String isVerify) {
        this.uid = uid;
        this.messNameR = messNameR;
        this.messNoR = messNoR;
        this.urlCover = urlCover;
        this.isVerify = isVerify;
    }

    @ColumnInfo(name = "Mess_Name")
    public String messNameR;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getMessNameR() {
        return messNameR;
    }

    public void setMessNameR(String messNameR) {
        this.messNameR = messNameR;
    }

    public String getMessNoR() {
        return messNoR;
    }

    public void setMessNoR(String messNoR) {
        this.messNoR = messNoR;
    }

    @ColumnInfo(name = "Mess_no")
    public String messNoR;

}
