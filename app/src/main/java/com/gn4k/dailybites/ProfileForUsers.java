package com.gn4k.dailybites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileForUsers extends AppCompatActivity {

    private static final String KEY_NAME = "name";
    private EditText  Name, email, number;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_for_users);

        Name = findViewById(R.id.Name);
        email = findViewById(R.id.email);
        number = findViewById(R.id.number);
        Button update, goToMap;
        update = findViewById(R.id.uploadpdU);
        goToMap = findViewById(R.id.updatelocationU);

        SharedPreferences sharedPreferences = this.getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor preferences = sharedPreferences.edit();

        Name.setText(sharedPreferences.getString("UserName",""));
        email.setText(sharedPreferences.getString("UserEmail",""));
        number.setText(sharedPreferences.getString("UserMobileNo",""));

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                preferences.putString("UserName",Name.getText().toString());
                preferences.apply();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> userInfo = new HashMap<>();

                userInfo.put(KEY_NAME, Name.getText().toString());
                db.collection("User").document(email.getText().toString()).update(userInfo).

                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ProfileForUsers.this, "UPDATE SUCCESSFUL", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileForUsers.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        goToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileForUsers.this, MapActivityToChooseLocation.class);
                startActivity(intent);
            }
        });

    }
}