package com.gn4k.dailybites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseMessOrUser extends AppCompatActivity {

    Button messOwner, user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mess_or_user);

        messOwner = findViewById(R.id.mess);
        user = findViewById(R.id.user);

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseMessOrUser.this,UserLoginPage.class);
                startActivity(intent);
                finish();
            }
        });

        messOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseMessOrUser.this,MessOnwnerLoginPage.class);
                startActivity(intent);
                finish();
            }
        });

    }
}