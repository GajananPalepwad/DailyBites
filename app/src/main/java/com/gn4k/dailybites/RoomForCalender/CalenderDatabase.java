package com.gn4k.dailybites.RoomForCalender;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.gn4k.dailybites.RoomForWhishList.Wishlist;
import com.gn4k.dailybites.RoomForWhishList.WishlistDao;

@Database(entities = {Calender.class}, version = 2)
public abstract class CalenderDatabase extends RoomDatabase {
    public abstract CalenderDao userDao();
}
