package com.gn4k.dailybites.RoomForCalender;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CalenderDao {
//    @Query("SELECT * FROM user")
//    List<Mess> getAll();
//
//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    List<User> loadAllByIds(int[] userIds);
//


    @Query("SELECT * FROM Calender")
    List<Calender> getAllCalender();

    @Update
    void update(Calender mess);


    @Query("SELECT * FROM Calender WHERE date = :date")
    Calender getCalenderByUid(String date);


    @Query("SELECT EXISTS(SELECT * FROM Calender WHERE date = :date)")
    Boolean isExistByCalenderNo(String date);



    @Insert
    void insert(Calender messs);

    @Delete
    void delete(Calender mess);

    @Query("SELECT uid FROM Calender ORDER BY uid DESC LIMIT 1")
    long getLastCalenderUid();

}