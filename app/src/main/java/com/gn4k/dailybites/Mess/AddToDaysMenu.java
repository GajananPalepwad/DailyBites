package com.gn4k.dailybites.Mess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gn4k.dailybites.HomeForMessOwner;
import com.gn4k.dailybites.MonthlyPlanEditor;
import com.gn4k.dailybites.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddToDaysMenu extends AppCompatActivity {

    EditText evmenuL, evprise, evmenuD;
    Button update;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_days_menu);
        evmenuL = findViewById(R.id.menuL);
        evmenuD = findViewById(R.id.menuD);
        evprise = findViewById(R.id.price);
        update = findViewById(R.id.update);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAllDataToUpdateInFirebase();
            }
        });
        updateAccordingtofirebase();
    }

    private void sendAllDataToUpdateInFirebase(){
        int price = Integer.parseInt(evprise.getText().toString());
        String menu = "Lunch: "+evmenuL.getText().toString()+"\nDinner: "+evmenuD.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("MessOwnerData",MODE_PRIVATE);
        // Update user information in Firebase database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dataRef = ref.child("mess").
                child(sharedPreferences.getString("MessOwnerMobileNo", ""));

        Map<String, Object> data = new HashMap<>();
        data.put("dishPrize", price+"");
        data.put("menu", menu);

        dataRef.updateChildren(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Move to the home screen
                        Intent intent = new Intent(AddToDaysMenu.this, HomeForMessOwner.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error occurred while saving data
                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateAccordingtofirebase(){

        SharedPreferences sharedPreferences = getSharedPreferences("MessOwnerData",MODE_PRIVATE);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbpath = db.child("mess")
                .child(sharedPreferences.getString("MessOwnerMobileNo", ""));

        dbpath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();

                    String s = (String) data.get("menu");

                    Pattern lunchPattern = Pattern.compile("Lunch: (.+?) \nDinner:");
                    Pattern dinnerPattern = Pattern.compile("Dinner: (.+)");

                    // Create the matchers
                    Matcher lunchMatcher = lunchPattern.matcher(s);
                    Matcher dinnerMatcher = dinnerPattern.matcher(s);

                    // Find the matches and extract the strings
                    if (lunchMatcher.find()) {
                        String lunch = lunchMatcher.group(1);
                        evmenuL.setText(lunch);

                    }

                    if (dinnerMatcher.find()) {
                        String dinner = dinnerMatcher.group(1);
                        evmenuD.setText(dinner);
                    }

                    evprise.setText(String.valueOf(data.get("dishPrize")));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

}