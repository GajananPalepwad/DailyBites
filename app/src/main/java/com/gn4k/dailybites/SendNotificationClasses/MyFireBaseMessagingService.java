package com.gn4k.dailybites.SendNotificationClasses;

import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.gn4k.dailybites.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


    public class MyFireBaseMessagingService extends FirebaseMessagingService {
        String title,message;
        @Override
        public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
            super.onMessageReceived(remoteMessage);
            title=remoteMessage.getData().get("Title");
            message=remoteMessage.getData().get("Message");
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.logowhite)

                            .setContentTitle(title)
                            .setContentText(message);

            NotificationManager manager =
                    (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());
        }


}

