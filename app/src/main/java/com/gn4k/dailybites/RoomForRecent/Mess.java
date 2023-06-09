package com.gn4k.dailybites.RoomForRecent;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Mess {

        @PrimaryKey(autoGenerate = true)
        public long uid;

        public String getUrlCover() {
                return urlCover;
        }

        public void setUrlCover(String urlCover) {
                this.urlCover = urlCover;
        }

        public String urlCover;

        public Mess(long uid, String messNameR, String messNoR, String urlCover) {
                this.uid = uid;
                this.messNameR = messNameR;
                this.messNoR = messNoR;
                this.urlCover = urlCover;
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
