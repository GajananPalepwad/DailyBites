package com.gn4k.dailybites.RoomForRecent;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MessDao {
//    @Query("SELECT * FROM user")
//    List<Mess> getAll();
//
//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    List<User> loadAllByIds(int[] userIds);
//


    @Query("SELECT * FROM Mess")
    List<Mess> getAllMess();

    @Update
    void update(Mess mess);


    @Query("SELECT * FROM Mess WHERE uid = :userid")
    Mess getMessByUid(long userid);

    @Query("SELECT EXISTS(SELECT * FROM Mess WHERE uid = :userid)")
    Boolean is_exist(long userid);

    @Insert
    void insert(Mess messs);

    @Delete
    void delete(Mess mess);
}