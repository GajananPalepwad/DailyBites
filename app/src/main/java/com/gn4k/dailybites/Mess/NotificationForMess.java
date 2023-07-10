package com.gn4k.dailybites.Mess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;

import com.gn4k.dailybites.R;
import com.gn4k.dailybites.RoomForNotification.NotificationAdapter;
import com.gn4k.dailybites.RoomForNotification.NotificationDao;
import com.gn4k.dailybites.RoomForNotification.NotificationData;
import com.gn4k.dailybites.RoomForNotification.NotificationDatabase;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class NotificationForMess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_mess);
        CardView backBtn = findViewById(R.id.back);

        backBtn.setOnClickListener(v -> onBackPressed());

        RecyclerView recentRecyclerView = findViewById(R.id.recyclerView);
        new Bgthread(recentRecyclerView).start();
    }

    class Bgthread extends Thread {  // to display recent list in recyclerView
        private RecyclerView recyclerView;

        Bgthread(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        public void run() {
            super.run();

            NotificationDatabase messdb = Room.databaseBuilder(NotificationForMess.this, NotificationDatabase.class, "NotificationView_DB").build();
            NotificationDao messDao = messdb.notificationDao();
            List<NotificationData> notification = messDao.getAllNotification();
            Collections.reverse(notification);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView.setLayoutManager(new LinearLayoutManager(NotificationForMess.this));
                    NotificationAdapter notificationAdapter = new NotificationAdapter(NotificationForMess.this,notification);
                    recyclerView.setAdapter(notificationAdapter);
                }
            });
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new Date(System.currentTimeMillis());
        return dateFormat.format(currentDate);
    }


//    class BgthreadNotificationStore extends Thread { // to add a notification in notification list in room database
//        public void run() {
//            super.run();
//
//            NotificationDatabase messdb = Room.databaseBuilder(getApplicationContext(),
//                    NotificationDatabase.class, "NotificationView_DB").build();
//
//            NotificationDao notificationDao = messdb.notificationDao();
//
//            long lastUid = notificationDao.getLastNotificationUid();
//
//            if (lastUid == 0) {
//                // Database is empty, set initial uid to 1
//                int initialUid = 1;
//                notificationDao.insert(new NotificationData(initialUid, title, body, getCurrentTime()));
//            } else {
//                long nextUid = lastUid + 1;
//
//                notificationDao.insert(new NotificationData(nextUid, title, body, getCurrentTime()));
//
//            }
//        }
//    }


}