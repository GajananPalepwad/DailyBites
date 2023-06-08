package com.gn4k.dailybites.RoomForRecent;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Mess.class}, version = 2)
public abstract class MessDatabase extends RoomDatabase {
    public abstract MessDao userDao();
}
