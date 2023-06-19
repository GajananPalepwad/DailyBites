package com.gn4k.dailybites.RoomForTransitionHistoryMess;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.gn4k.dailybites.RoomForRecent.Mess;
import com.gn4k.dailybites.RoomForRecent.MessDao;

@Database(entities = {WalletMess.class}, version = 2)
public abstract class WalletDatabase extends RoomDatabase {
    public abstract WalletForMessDao walletDao();
}
