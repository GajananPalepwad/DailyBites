package com.gn4k.dailybites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gn4k.dailybites.Animation.LoadingDialog;
import com.gn4k.dailybites.Animation.RatingsDialog;
import com.gn4k.dailybites.Mess.AddToDaysMenu;
import com.gn4k.dailybites.Mess.OneDayPlanClasses.OneDayUserList;
import com.gn4k.dailybites.Mess.QrCodeGenerator;
import com.gn4k.dailybites.Mess.SendNotificationToUser;
import com.gn4k.dailybites.Mess.WalletForMess;
import com.gn4k.dailybites.Mess.consumersUserlistFragment.ConsumersList;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class HomeForMessOwner extends AppCompatActivity {


    TextView messName, subscribers, ratings;
    ImageView profileImg;
    String mobileNo, aadhaarNo;
    int countD =0, countS = 0, countG =0, totalUsers = 0;
    RatingBar myRatingBar;
    Button qr, oneDayUsers;

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_for_mess_owner);
        loadingDialog = new LoadingDialog(HomeForMessOwner.this);
        loadingDialog.startLoading();
        Button silver, gold, diamond;
        CardView profile, settings, consumersBtn, wallet, sendMsg, support, updateMenu, notification, homelag;
        support = findViewById(R.id.support);
        homelag = findViewById(R.id.homelag);
        notification = findViewById(R.id.notification);
        sendMsg = findViewById(R.id.sendMSG);
        wallet = findViewById(R.id.walletM);
        updateMenu = findViewById(R.id.dailyMenu);
        settings = findViewById(R.id.settings);
        profile = findViewById(R.id.profile);
        silver = findViewById(R.id.silver);
        gold = findViewById(R.id.gold);
        diamond = findViewById(R.id.diamond);
        profileImg  = findViewById(R.id.profileimg);
        messName  = findViewById(R.id.messName);
        subscribers = findViewById(R.id.subscribers);
        ratings = findViewById(R.id.ratingInNumber);
        myRatingBar = findViewById(R.id.myRatingBar);
        consumersBtn = findViewById(R.id.ConsumersBtn);
        qr = findViewById(R.id.show_qr);
        oneDayUsers = findViewById(R.id.oneDayUsers);


        getAllDataFromFirebase();

        oneDayUsers. setOnClickListener(v -> {
            Intent intent = new Intent(HomeForMessOwner.this, OneDayUserList.class);
            startActivity(intent);
        });

        qr. setOnClickListener(v -> {
            Intent intent = new Intent(HomeForMessOwner.this, QrCodeGenerator.class);
            intent.putExtra("qr_data", "offer");
            startActivity(intent);
        });

        support.setOnClickListener(v -> {
            
        });

        sendMsg.setOnClickListener(v -> {
            Intent intent = new Intent(HomeForMessOwner.this, SendNotificationToUser.class);
            startActivity(intent);
        });
        consumersBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeForMessOwner.this, ConsumersList.class);
            startActivity(intent);
        });
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeForMessOwner.this, ProfilePageForMess.class);
            startActivity(intent);
        });

        silver.setOnClickListener(v -> {
            if(!aadhaarNo.equals("")) {
                Intent intent = new Intent(HomeForMessOwner.this, MonthlyPlanEditor.class);
                intent.putExtra("planName", "Silver");
                startActivity(intent);
            }else{
                showLogoutDialogToCompleteProfile();
            }
        });

        gold.setOnClickListener(v -> {
            if(!aadhaarNo.equals("")) {
                Intent intent = new Intent(HomeForMessOwner.this, MonthlyPlanEditor.class);
                intent.putExtra("planName", "Gold");
                startActivity(intent);
            }else{
                showLogoutDialogToCompleteProfile();
            }
        });

        diamond.setOnClickListener(v -> {
            if(!aadhaarNo.equals("")) {
                Intent intent = new Intent(HomeForMessOwner.this, MonthlyPlanEditor.class);
                intent.putExtra("planName", "Diamond");
                startActivity(intent);
            }else{
                showLogoutDialogToCompleteProfile();
            }
        });

        settings.setOnClickListener(v -> showSettingsBottomSheetDialog());

        updateMenu.setOnClickListener(v -> {
            Intent intent = new Intent(HomeForMessOwner.this, AddToDaysMenu.class);
            startActivity(intent);
        });

        wallet.setOnClickListener(v -> {
            Intent intent = new Intent(HomeForMessOwner.this, WalletForMess.class);
            startActivity(intent);
        });

        notification.setOnClickListener(v -> {
            Intent intent = new Intent(HomeForMessOwner.this, NotificationForMess.class);
            startActivity(intent);
        });

        homelag.setOnClickListener(v -> {
            Intent intent = new Intent(HomeForMessOwner.this, LanguageChooser.class);
            intent.putExtra("data", "settingsMess");
            startActivity(intent);
        });



        getCountOfUsers();
    }


    private void getAllDataFromFirebase(){
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
                        messName.setText(sharedPreferences.getString("MessName", ""));
                        ratings.setText((String) data.get("ratings"));
                        myRatingBar.setRating(Float. parseFloat((String) data.get("ratings")) );
                        aadhaarNo = (String) data.get("addharNo");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        mobileNo = sharedPreferences.getString("MessOwnerMobileNo", "");
    }




    public void showLogoutDialog(View V) {

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeForMessOwner.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");

        builder.setPositiveButton("Yes", (dialog, which) -> Logout());

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showLogoutDialogToCompleteProfile() {

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeForMessOwner.this);
        builder.setTitle("Oops!!!");
        builder.setMessage("It seems like you haven't completed your profile.\nPress \"OK\" to Continue");

        builder.setPositiveButton("Ok", (dialog, which) -> {
            // Call the logout function
            Intent intent = new Intent(HomeForMessOwner.this, ProfilePageForMess.class);
            startActivity(intent);
            dialog.dismiss();

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void Logout(){
        SharedPreferences sharedPreferences = getSharedPreferences("MessOwnerData", MODE_PRIVATE);
        SharedPreferences.Editor preferences = sharedPreferences.edit();

        preferences.putString("MessOwnerEmail","");
        preferences.putString("MessName","");
        preferences.putString("MessOwnerPassword","");
        preferences.putString("MessOwnerMobileNo","");
        preferences.putString("MessOwnerLatitude","");
        preferences.putString("MessOwnerLongitude","");
        preferences.putString("MessOwnerName","");
        preferences.putString("MessOwnerAddress","");
        preferences.apply();

        SharedPreferences sharedPreferencesChoose = getSharedPreferences("Choose", MODE_PRIVATE);
        SharedPreferences.Editor preferences2 = sharedPreferencesChoose.edit();
        preferences2.putString("MessOrUser","");
        preferences2.apply();

        Intent intent = new Intent(HomeForMessOwner.this, LanguageChooser.class);
        startActivity(intent);
        finish();
    }


    private BottomSheetDialog bottomSheetDialog;
    private void showSettingsBottomSheetDialog() {


        // Inflate the layout for the BottomSheetDialog
        View bottomSheetView = getLayoutInflater().inflate(R.layout.setting_bottomsheet_for_mess, (ConstraintLayout) findViewById(R.id.setting_sheet));

        // Find your button or any other view inside the BottomSheetDialog layout
        ConstraintLayout notifi = bottomSheetView.findViewById(R.id.notificationSettings1);
        ConstraintLayout cache = bottomSheetView.findViewById(R.id.clearCache1);
        ConstraintLayout lan = bottomSheetView.findViewById(R.id.lan1);
        ConstraintLayout logOut = bottomSheetView.findViewById(R.id.logout1);
        // Set click listener for the button inside the BottomSheetDialog

        notifi.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivity(intent);
        });


        cache.setOnClickListener(v -> {
            // Handle button click inside the BottomSheetDialog
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        });

        lan.setOnClickListener(v -> {
            Intent intent = new Intent(HomeForMessOwner.this, LanguageChooser.class);
            intent.putExtra("data", "settingsMess");
            startActivity(intent);
        });


        logOut.setOnClickListener(v -> showLogoutDialog(v));

        // Create the BottomSheetDialog
        bottomSheetDialog = new BottomSheetDialog(HomeForMessOwner.this,R.style.AppBottomSheetDialogTheme);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void n(View v){
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivity(intent);
    }


    public void c(View v){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    public void cl(View v){
        Intent intent = new Intent(HomeForMessOwner.this, LanguageChooser.class);
        intent.putExtra("data", "settingsMess");
        startActivity(intent);
    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getCountOfUsers(){

        DatabaseReference databaseRefD = FirebaseDatabase.getInstance().getReference().
                child("mess").
                child(mobileNo).
                child("Diamondplan").
                child("Users");
        databaseRefD.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    countD = (int) dataSnapshot.getChildrenCount();
                    totalUsers = totalUsers + countD;
                    subscribers.setText(""+totalUsers);
                    // Use the 'count' variable containing the number of child nodes
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
            }
        });

        DatabaseReference databaseRefG = FirebaseDatabase.getInstance().getReference().
                child("mess").
                child(mobileNo).
                child("Goldplan").
                child("Users");
        databaseRefG.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    countG = (int) dataSnapshot.getChildrenCount();
                    totalUsers = totalUsers + countG;
                    subscribers.setText(""+totalUsers);
                    // Use the 'count' variable containing the number of child nodes
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
            }
        });

        DatabaseReference databaseRefS = FirebaseDatabase.getInstance().getReference().
                child("mess").
                child(mobileNo).
                child("Silverplan").
                child("Users");
        databaseRefS.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    countS = (int) dataSnapshot.getChildrenCount();
                    totalUsers = totalUsers + countS;
                    subscribers.setText(""+totalUsers);
                    loadingDialog.stopLoading();
                    // Use the 'count' variable containing the number of child nodes
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
            }
        });

    }

}