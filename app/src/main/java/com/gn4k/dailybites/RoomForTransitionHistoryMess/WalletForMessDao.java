package com.gn4k.dailybites.RoomForTransitionHistoryMess;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.gn4k.dailybites.RoomForRecent.Mess;

import java.util.List;

@Dao
public interface WalletForMessDao {

    @Query("SELECT * FROM WalletMess")
    List<WalletMess> getAllMess();

    @Update
    void update(WalletMess mess);


    @Query("SELECT * FROM WalletMess WHERE Time = :time")
    WalletMess getMessByUid(String time);


    @Query("SELECT EXISTS(SELECT * FROM WalletMess WHERE Time = :time)")
    Boolean isExistByMessNo(String time);



    @Insert
    void insert(WalletMess times);

    @Delete
    void delete(WalletMess time);

    @Query("SELECT uid FROM WalletMess ORDER BY uid DESC LIMIT 1")
    long getLastMessUid();

}