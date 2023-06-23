package com.gn4k.dailybites.RoomForNotification;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.gn4k.dailybites.RoomForRecent.Mess;

import java.util.List;

@Dao
public interface NotificationDao {


    @Query("SELECT * FROM NotificationData")
    List<NotificationData> getAllNotification();

    @Update
    void update(NotificationData notification);


    @Query("SELECT * FROM NotificationData WHERE title = :title")
    Mess getNotificationByUid(String title);


    @Query("SELECT EXISTS(SELECT * FROM NotificationData WHERE title = :title)")
    Boolean isExistByTitle(String title);



    @Insert
    void insert(NotificationData messs);

    @Delete
    void delete(NotificationData mess);

    @Query("SELECT uid FROM NotificationData ORDER BY uid DESC LIMIT 1")
    long getLastNotificationUid();

}