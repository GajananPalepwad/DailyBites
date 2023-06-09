package com.gn4k.dailybites.RoomForWhishList;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.gn4k.dailybites.RoomForRecent.Mess;
import com.gn4k.dailybites.RoomForRecent.MessDao;

@Database(entities = {Wishlist.class}, version = 2)
public abstract class WishlistDatabase extends RoomDatabase {
    public abstract WishlistDao userDao();
}
