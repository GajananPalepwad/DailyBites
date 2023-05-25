package com.gn4k.dailybites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread thread = new Thread() {

            public void run() {
                try {
                    sleep(2500);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    SharedPreferences sharedPreferences = getSharedPreferences("UserData",MODE_PRIVATE);

                    if(sharedPreferences.getString("UserEmail","").isEmpty() ||
                            sharedPreferences.getString("UserName","").isEmpty() ||
                            sharedPreferences.getString("UserPassword","").isEmpty() ||
                            sharedPreferences.getString("UserMobileNo","").isEmpty()) {
                        Intent intent = new Intent(MainActivity.this, LanguageChooser.class);
                        startActivity(intent);
                        finish();

                    } else if (sharedPreferences.getString("UserLatitude","").isEmpty() ||
                            sharedPreferences.getString("UserLongitude","").isEmpty() ||
                            sharedPreferences.getString("UserAddress","").isEmpty()) {

                            Intent intent = new Intent(MainActivity.this, MapActivityToChooseLocation.class);
                            startActivity(intent);
                            finish();

                    } else{
                        Intent intent = new Intent(MainActivity.this,Home.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };thread.start();
    }

}