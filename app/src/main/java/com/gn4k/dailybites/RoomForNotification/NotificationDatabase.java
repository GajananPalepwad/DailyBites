package com.gn4k.dailybites.RoomForNotification;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {NotificationData.class}, version = 2)
public abstract class NotificationDatabase extends RoomDatabase {
    public abstract NotificationDao notificationDao();
}
