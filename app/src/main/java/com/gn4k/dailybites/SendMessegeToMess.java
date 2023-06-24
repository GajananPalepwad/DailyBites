package com.gn4k.dailybites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gn4k.dailybites.Mess.SendNotificationToUser;
import com.gn4k.dailybites.SendNotificationClasses.FcmNotificationsSender;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SendMessegeToMess extends AppCompatActivity {

    EditText title, body;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_messege_to_mess);

        title = findViewById(R.id.title);
        body = findViewById(R.id.body);

        button = findViewById(R.id.send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserData",MODE_PRIVATE);


                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                DatabaseReference dbpath = db.child("mess")
                        .child((sharedPreferences.getString("MessNo", "")));

                            dbpath.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();
                                        String token = (String) data.get("token");


                            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                                    token,
                                    title.getText().toString(),
                                    body.getText().toString(),
                                    getApplicationContext(),
                                    SendMessegeToMess.this);

                            notificationsSender.SendNotifications();
                            } else {
                                Toast.makeText(SendMessegeToMess.this, "Something went wrong!!!!", Toast.LENGTH_SHORT).show();
                            }
                        }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
        });

    }
}