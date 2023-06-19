package com.gn4k.dailybites.RoomForTransitionHistoryMess;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class WalletMess {

        @PrimaryKey(autoGenerate = true)
        public long uid;

        public String getAmount() {
                return Amount;
        }

        public void setAmount(String Amount) {
                this.Amount = Amount;
        }

        public String Amount;

        public WalletMess(long uid, String Status, String Time, String Amount) {
                this.uid = uid;
                this.Status = Status;
                this.Time = Time;
                this.Amount = Amount;
        }

        @ColumnInfo(name = "Mess_Name")
        public String Status;

        public long getUid() {
                return uid;
        }

        public void setUid(long uid) {
                this.uid = uid;
        }

        public String getStatus() {
                return Status;
        }

        public void setStatus(String Status) {
                this.Status = Status;
        }

        public String getTime() {
                return Time;
        }

        public void setTime(String Time) {
                this.Time = Time;
        }

        @ColumnInfo(name = "Time")
        public String Time;

}
