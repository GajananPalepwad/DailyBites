package com.gn4k.dailybites.SendNotificationClasses;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.room.Room;


import com.gn4k.dailybites.Mess.NotificationForMess;
import com.gn4k.dailybites.R;

import com.gn4k.dailybites.RoomForNotification.NotificationDao;
import com.gn4k.dailybites.RoomForNotification.NotificationData;
import com.gn4k.dailybites.RoomForNotification.NotificationDatabase;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

     NotificationManager mNotificationManager;
     String title="", body="";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        title = remoteMessage.getNotification().getTitle().toString();
        body = remoteMessage.getNotification().getBody().toString();

//
        new Bgthread().start();
        // playing audio and vibration when user send request
        Uri notification = Uri.parse("android.resource://" + getPackageName() + "/raw/notification_sound");
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            r.setLooping(false);
        }

        // vibration
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 300, 300, 300};
        v.vibrate(pattern, -1);


        String icon = remoteMessage.getNotification().getIcon();
        int resourceImage;
        if (icon != null) {
            resourceImage = getResources().getIdentifier(icon, "drawable", getPackageName());
        } else {
            // Provide a default icon resource ID if the icon is not specified
            resourceImage = R.drawable.logo_black;
        }



        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            builder.setSmallIcon(resourceImage);
        } else {

            builder.setSmallIcon(resourceImage);
        }

        Intent resultIntent = new Intent(this, NotificationForMess.class);



        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity
                    (this, 1, resultIntent, PendingIntent.FLAG_MUTABLE);
        }
        else
        {
            pendingIntent = PendingIntent.getActivity
                    (this, 1, resultIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        }



        builder.setContentTitle(remoteMessage.getNotification().getTitle());
        builder.setContentText(remoteMessage.getNotification().getBody());
        builder.setContentIntent(pendingIntent);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()));
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setSmallIcon(R.drawable.logo_black);


        mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }


        // notificationId is a unique int for each notification that you must define
        mNotificationManager.notify(100, builder.build());

    }
    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new Date(System.currentTimeMillis());
        return dateFormat.format(currentDate);
    }



    class Bgthread extends Thread { // to add a notification in notification list in room database
        public void run() {
            super.run();

            NotificationDatabase messdb = Room.databaseBuilder(getApplicationContext(),
                    NotificationDatabase.class, "NotificationView_DB").build();

            NotificationDao notificationDao = messdb.notificationDao();

            long lastUid = notificationDao.getLastNotificationUid();

            if (lastUid == 0) {
                // Database is empty, set initial uid to 1
                int initialUid = 1;
                notificationDao.insert(new NotificationData(initialUid, title, body, getCurrentTime()));
            } else {
                long nextUid = lastUid + 1;

                notificationDao.insert(new NotificationData(nextUid, title, body, getCurrentTime()));

            }
        }
    }
}


