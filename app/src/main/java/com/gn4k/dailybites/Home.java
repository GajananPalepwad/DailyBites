package com.gn4k.dailybites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {

    private static final int YOUR_REQUEST_CODE = 123;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.body_container, new HomeFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        MenuItem homebtn = bottomNavigationView.getMenu().findItem(R.id.nav_home);
        MenuItem calenderbtn = bottomNavigationView.getMenu().findItem(R.id.nav_calender);
        MenuItem recentbtn = bottomNavigationView.getMenu().findItem(R.id.nav_recent);
        MenuItem userbtn = bottomNavigationView.getMenu().findItem(R.id.nav_user);

        homebtn.setEnabled(false);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.nav_home:
                        fragment = new HomeFragment();
                        homebtn.setEnabled(false);
                        calenderbtn.setEnabled(true);
                        recentbtn.setEnabled(true);
                        userbtn.setEnabled(true);
                        break;

                    case R.id.nav_calender:
                        fragment = new CalenderFragment();
                        homebtn.setEnabled(true);
                        calenderbtn.setEnabled(false);
                        recentbtn.setEnabled(true);
                        userbtn.setEnabled(true);
                        break;

                    case R.id.nav_recent:
                        fragment = new RecentFragment();
                        homebtn.setEnabled(true);
                        calenderbtn.setEnabled(true);
                        recentbtn.setEnabled(false);
                        userbtn.setEnabled(true);
                        break;

                    case R.id.nav_user:
                        fragment = new UserFragment();
                        homebtn.setEnabled(true);
                        calenderbtn.setEnabled(true);
                        recentbtn.setEnabled(true);
                        userbtn.setEnabled(false);
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();


                return true;
            }
        });


    }
}