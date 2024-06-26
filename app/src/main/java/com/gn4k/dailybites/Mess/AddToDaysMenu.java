package com.gn4k.dailybites.Mess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gn4k.dailybites.Animation.LoadingDialog;
import com.gn4k.dailybites.R;
import com.gn4k.dailybites.SendNotificationClasses.FcmNotificationsSender;
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
    String mobile;
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_days_menu);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoading();
        evmenuL = findViewById(R.id.menuL);
        evmenuD = findViewById(R.id.menuD);
        evprise = findViewById(R.id.price);
        update = findViewById(R.id.update);

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
                Intent intent = new Intent(AddToDaysMenu.this, NotificationForMess.class);
                startActivity(intent);
            }
        });

        walletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddToDaysMenu.this, WalletForMess.class);
                startActivity(intent);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoading();
                sendAllDataToUpdateInFirebase();
                notifyEveryDiamondUser();//diamond
                notifyEveryGoldUser();//gold
                notifyEverySilverUser();//silver
            }
        });
        updateAccordingtofirebase();
    }
    String menu="";
    private void sendAllDataToUpdateInFirebase(){
        int price = Integer.parseInt(evprise.getText().toString());
        if( evmenuL.getText().toString().isEmpty() && evmenuD.getText().toString().isEmpty()){
            menu ="";
        }else {
            menu = "Lunch: " + evmenuL.getText().toString() + "\nDinner: " + evmenuD.getText().toString();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("MessOwnerData",MODE_PRIVATE);
        // Update user information in Firebase database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dataRef = ref.child("mess").
                child(sharedPreferences.getString("MessOwnerMobileNo", ""));

        Map<String, Object> data = new HashMap<>();
        data.put("dishPrize", price+"");
        data.put("menu", menu);

        dataRef.updateChildren(data)
                .addOnSuccessListener(aVoid -> {
                    // Move to the home screen
                    Intent intent = new Intent(AddToDaysMenu.this, HomeForMessOwner.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Error occurred while saving data
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateAccordingtofirebase(){

        SharedPreferences sharedPreferences = getSharedPreferences("MessOwnerData",MODE_PRIVATE);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbpath = db.child("mess")
                .child(sharedPreferences.getString("MessOwnerMobileNo", ""));
        mobile = sharedPreferences.getString("MessOwnerMobileNo", "");
        dbpath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();

                    String s = (String) data.get("menu");

                    Pattern lunchPattern = Pattern.compile("Lunch: (.+?)\nDinner:");
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
                loadingDialog.stopLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    private void notifyEveryGoldUser(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("mess").child(mobile).child("Goldplan").child("Users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userToken = userSnapshot.child("token").getValue(String.class);

                    if (userToken != null) {
                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(userToken,
                                "📢Today's Menu😋",
                                menu,
                                getApplicationContext(),
                                AddToDaysMenu.this);

                        notificationsSender.SendNotifications();
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
    }

    private void notifyEverySilverUser(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("mess").child(mobile).child("Silverplan").child("Users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userToken = userSnapshot.child("token").getValue(String.class);

                    if (userToken != null) {
                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(userToken,
                                "📢Today's Menu😋",
                                menu,
                                getApplicationContext(),
                                AddToDaysMenu.this);

                        notificationsSender.SendNotifications();
                    }
                }
                loadingDialog.stopLoading();
                showInstructionDialogBox("Successful","Notifications send successful &\n OneDay plan added");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
    }

    private void notifyEveryDiamondUser(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("mess").child(mobile).child("Diamondplan").child("Users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userToken = userSnapshot.child("token").getValue(String.class);

                    if (userToken != null) {
                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(userToken,
                                "📢Today's Menu😋",
                                menu,
                                getApplicationContext(),
                                AddToDaysMenu.this);

                        notificationsSender.SendNotifications();
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
    }

    private void showInstructionDialogBox(String title, String mbody) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(mbody);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}