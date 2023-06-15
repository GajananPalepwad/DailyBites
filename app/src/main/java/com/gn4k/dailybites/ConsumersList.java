package com.gn4k.dailybites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.gn4k.dailybites.consumersUserlistFragment.DiamondUserList;
import com.gn4k.dailybites.consumersUserlistFragment.GoldUserList;
import com.gn4k.dailybites.consumersUserlistFragment.SilverUserList;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ConsumersList extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumers_list);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.user_container, new SilverUserList()).commit();
        bottomNavigationView.setSelectedItemId(R.id.silverbtn);

        MenuItem silverbtn = bottomNavigationView.getMenu().findItem(R.id.silverbtn);
        MenuItem goldbtn = bottomNavigationView.getMenu().findItem(R.id.goldbtn);
        MenuItem diamondbtn = bottomNavigationView.getMenu().findItem(R.id.diamondbtn);

        silverbtn.setEnabled(false);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.silverbtn:
                        fragment = new SilverUserList();
                        silverbtn.setEnabled(false);
                        goldbtn.setEnabled(true);
                        diamondbtn.setEnabled(true);
                        break;

                    case R.id.goldbtn:
                        fragment = new GoldUserList();
                        silverbtn.setEnabled(true);
                        goldbtn.setEnabled(false);
                        diamondbtn.setEnabled(true);
                        break;

                    case R.id.diamondbtn:
                        fragment = new DiamondUserList();
                        silverbtn.setEnabled(true);
                        goldbtn.setEnabled(true);
                        diamondbtn.setEnabled(false);
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.user_container, fragment).commit();

                return true;
            }
        });


    }
}