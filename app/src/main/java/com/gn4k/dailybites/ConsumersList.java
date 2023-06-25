package com.gn4k.dailybites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.gn4k.dailybites.Mess.AddToDaysMenu;
import com.gn4k.dailybites.Mess.WalletForMess;
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
                Intent intent = new Intent(ConsumersList.this, NotificationForMess.class);
                startActivity(intent);
            }
        });

        walletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConsumersList.this, WalletForMess.class);
                startActivity(intent);
            }
        });



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