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

                    SharedPreferences sharedPreferencesUser = getSharedPreferences("UserData",MODE_PRIVATE);
                    SharedPreferences sharedPreferencesMess = getSharedPreferences("MessOwnerData",MODE_PRIVATE);

                    if(sharedPreferencesUser.getString("UserEmail","").isEmpty() ||
                            sharedPreferencesUser.getString("UserName","").isEmpty() ||
                            sharedPreferencesUser.getString("UserPassword","").isEmpty() ||
                            sharedPreferencesUser.getString("UserMobileNo","").isEmpty()) {
                        
                        if(sharedPreferencesMess.getString("MessName","").isEmpty() ||
                                sharedPreferencesMess.getString("MessOwnerPassword","").isEmpty() ||
                                sharedPreferencesMess.getString("MessOwnerName","").isEmpty() ||
                                sharedPreferencesMess.getString("MessOwnerMobileNo","").isEmpty() ||
                                sharedPreferencesMess.getString("MessOwnerEmail","").isEmpty()) {

                            Intent intent = new Intent(MainActivity.this, LanguageChooser.class);
                            startActivity(intent);
                            finish();

                        } else {
                            if (sharedPreferencesMess.getString("MessOwnerAddress","").isEmpty() ||
                                    sharedPreferencesMess.getString("MessOwnerLongitude","").isEmpty() ||
                                    sharedPreferencesMess.getString("MessOwnerLatitude","").isEmpty()) {

                                Intent intent = new Intent(MainActivity.this,MapToLocateMess.class);
                                startActivity(intent);
                                finish();

                            }
                        }

                    } else if (sharedPreferencesUser.getString("UserLatitude","").isEmpty() ||
                            sharedPreferencesUser.getString("UserLongitude","").isEmpty() ||
                            sharedPreferencesUser.getString("UserAddress","").isEmpty()) {

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