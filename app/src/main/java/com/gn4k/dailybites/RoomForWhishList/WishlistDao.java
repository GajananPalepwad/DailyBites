package com.gn4k.dailybites.RoomForWhishList;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WishlistDao {
//    @Query("SELECT * FROM user")
//    List<Mess> getAll();
//
//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    List<User> loadAllByIds(int[] userIds);
//


    @Query("SELECT * FROM Wishlist")
    List<Wishlist> getAllWishlist();

    @Update
    void update(Wishlist mess);


    @Query("SELECT * FROM Wishlist WHERE Mess_no = :messNo")
    Wishlist getWishlistByUid(String messNo);


    @Query("SELECT EXISTS(SELECT * FROM Wishlist WHERE Mess_no = :messNo)")
    Boolean isExistByWishlistNo(String messNo);



    @Insert
    void insert(Wishlist messs);

    @Delete
    void delete(Wishlist mess);

    @Query("SELECT uid FROM Wishlist ORDER BY uid DESC LIMIT 1")
    long getLastWishlistUid();

}