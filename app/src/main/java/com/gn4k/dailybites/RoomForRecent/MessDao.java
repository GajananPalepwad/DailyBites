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


    @Query("SELECT * FROM Mess WHERE Mess_no = :messNo")
    Mess getMessByUid(String messNo);


    @Query("SELECT EXISTS(SELECT * FROM Mess WHERE Mess_no = :messNo)")
    Boolean isExistByMessNo(String messNo);



    @Insert
    void insert(Mess messs);

    @Delete
    void delete(Mess mess);

    @Query("SELECT uid FROM Mess ORDER BY uid DESC LIMIT 1")
    long getLastMessUid();

}