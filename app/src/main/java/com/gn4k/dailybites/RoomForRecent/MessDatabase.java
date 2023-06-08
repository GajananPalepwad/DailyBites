package com.gn4k.dailybites.RoomForRecent;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Mess.class}, version = 1)
public abstract class MessDatabase extends RoomDatabase {
    public abstract MessDao userDao();
}
