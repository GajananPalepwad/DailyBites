package com.gn4k.dailybites.Mess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gn4k.dailybites.R;
import com.gn4k.dailybites.SendNotificationClasses.Client;
import com.gn4k.dailybites.SendNotificationClasses.FcmNotificationsSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;


public class SendNotificationToUser extends AppCompatActivity {

    EditText token, title, body;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification_to_user);

        token = findViewById(R.id.editTextText);
        title = findViewById(R.id.editTextText2);
        body = findViewById(R.id.editTextText3);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token.getText().toString(),
                        title.getText().toString(),
                        body.getText().toString(),
                        getApplicationContext(),
                        SendNotificationToUser.this);

                notificationsSender.SendNotifications();


            }
        });


        getToken();
    }
    String tokenString="";
    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    tokenString = task.getResult();
                    token.setText(tokenString);
                } else {
                    // Handle the case when token retrieval fails
                    Toast.makeText(SendNotificationToUser.this, "Failed to retrieve token",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
