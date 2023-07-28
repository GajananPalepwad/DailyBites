package com.gn4k.dailybites;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gn4k.dailybites.Mess.MessOnwnerLoginPage;
import com.gn4k.dailybites.User.UserLoginPage;

public class ChooseMessOrUser extends AppCompatActivity {

    Button messOwner, user;
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mess_or_user);

        messOwner = findViewById(R.id.mess);
        user = findViewById(R.id.user);


        int permissionState = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS);
        // If the permission is not granted, request it.
        if (permissionState == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
        }




        user.setOnClickListener(v -> {

            Intent intent = new Intent(ChooseMessOrUser.this, UserLoginPage.class);
            startActivity(intent);
        });

        messOwner.setOnClickListener(v -> {

            Intent intent = new Intent(ChooseMessOrUser.this, MessOnwnerLoginPage.class);
            startActivity(intent);
        });

    }
}