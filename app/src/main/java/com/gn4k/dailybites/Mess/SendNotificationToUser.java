package com.gn4k.dailybites.Mess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gn4k.dailybites.ConsumersList;
import com.gn4k.dailybites.NotificationForMess;
import com.gn4k.dailybites.R;
import com.gn4k.dailybites.SendNotificationClasses.Client;
import com.gn4k.dailybites.SendNotificationClasses.FcmNotificationsSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;


public class SendNotificationToUser extends AppCompatActivity {

    EditText title, body;
    Button button;
    String mobile, titleString, bodyString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification_to_user);


        CardView backBtn = findViewById(R.id.back);
        CardView notificationBtn = findViewById(R.id.notification);
        CardView walletBtn = findViewById(R.id.wallet);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SendNotificationToUser.this, NotificationForMess.class);
                startActivity(intent);
            }
        });

        walletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SendNotificationToUser.this, WalletForMess.class);
                startActivity(intent);
            }
        });




        SharedPreferences sharedPreferences = getSharedPreferences("MessOwnerData", MODE_PRIVATE);
        mobile = sharedPreferences.getString("MessOwnerMobileNo", "");


        title = findViewById(R.id.title);
        body = findViewById(R.id.body);

        button = findViewById(R.id.send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                titleString = title.getText().toString();
                bodyString = body.getText().toString();

                notifyEveryDiamondUser();//diamond
                notifyEveryGoldUser();//gold
                notifyEverySilverUser();//silver

            }
        });
    }


    private void notifyEveryGoldUser(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("mess").child(mobile).child("Goldplan").child("Users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userToken = userSnapshot.child("token").getValue(String.class);

                    if (userToken != null) {
                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(userToken,
                                titleString,
                                bodyString,
                                getApplicationContext(),
                                SendNotificationToUser.this);

                        notificationsSender.SendNotifications();
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
    }

    private void notifyEverySilverUser(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("mess").child(mobile).child("Silverplan").child("Users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userToken = userSnapshot.child("token").getValue(String.class);

                    if (userToken != null) {
                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(userToken,
                                titleString,
                                bodyString,
                                getApplicationContext(),
                                SendNotificationToUser.this);

                        notificationsSender.SendNotifications();
                    }
                }

                showInstructionDialogBox("Successful","Notifications send successful &\n OneDay plan added");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
    }

    private void notifyEveryDiamondUser(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("mess").child(mobile).child("Diamondplan").child("Users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userToken = userSnapshot.child("token").getValue(String.class);

                    if (userToken != null) {
                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(userToken,
                                titleString,
                                bodyString,
                                getApplicationContext(),
                                SendNotificationToUser.this);

                        notificationsSender.SendNotifications();
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
    }

    private void showInstructionDialogBox(String title, String mbody) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(mbody);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
