package com.gn4k.dailybites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class HomeForMessOwner extends AppCompatActivity {


TextView messName, subscribers;
    ImageView profileImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_for_mess_owner);
        Button silver, gold, diamond;
        CardView profile = findViewById(R.id.profile);
        silver = findViewById(R.id.silver);
        gold = findViewById(R.id.gold);
        diamond = findViewById(R.id.diamond);
        profileImg  = findViewById(R.id.profileimg);
        messName  = findViewById(R.id.messName);
        subscribers = findViewById(R.id.subscribers);

        SharedPreferences sharedPreferences = getSharedPreferences("MessOwnerData",MODE_PRIVATE);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbpath = db.child("mess")
                .child(sharedPreferences.getString("MessOwnerMobileNo", ""));

        dbpath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();
                    if ((String) data.get("coverImage") != null || (String) data.get("messName")!= null) {
                        String url = (String) data.get("coverImage");
                        Glide.with(HomeForMessOwner.this).load(url).centerCrop().placeholder(R.drawable.cooking).into(profileImg);
                        messName.setText((String) data.get("messName"));
                        messName.setText((String) data.get("ratings"));
                    }


                }
            }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    });


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeForMessOwner.this, ProfilePageForMess.class);
                intent.putExtra("planName", "Silver");
                startActivity(intent);
            }
        });

        silver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeForMessOwner.this, MonthlyPlanEditor.class);
                intent.putExtra("planName", "Silver");
                startActivity(intent);
            }
        });

        gold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeForMessOwner.this, MonthlyPlanEditor.class);
                intent.putExtra("planName", "Gold");
                startActivity(intent);
            }
        });

        diamond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeForMessOwner.this, MonthlyPlanEditor.class);
                intent.putExtra("planName", "Diamond");
                startActivity(intent);
            }
        });



    }
}