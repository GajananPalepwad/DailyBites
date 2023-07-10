package com.gn4k.dailybites.User;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.gn4k.dailybites.AboutUs;
import com.gn4k.dailybites.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OfferCodeScanner extends AppCompatActivity {


    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_code_scanner);

        Button scanner_btn = findViewById(R.id.scanner);
        CardView backBtn = findViewById(R.id.back);
        TextView dateExpire = findViewById(R.id.expired_date);
        TextView aboutUs = findViewById(R.id.aboutUs);

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);

        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");

        try {
            Date date = inputFormat.parse(sharedPreferences.getString("toDate",""));
            String formattedDate = outputFormat.format(date);
            dateExpire.setText("Expires on "+formattedDate+", 11:59");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        backBtn.setOnClickListener(v -> onBackPressed());

        aboutUs.setOnClickListener(v -> {
            Intent intent = new Intent(OfferCodeScanner.this, AboutUs.class);
            startActivity(intent);
        });

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.CAMERA,
        };

        if(!hasPermission(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS,PERMISSION_ALL);
        }else {
            scanner_btn.setOnClickListener(v -> scanCode());
        }

    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("        Scanning.....\n\n\n\n\n\n\n\nVolume up to flash on\n\n");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);

        barLaucher.launch(options);
    }
    int i=0;
    String tokensLeft="";
    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result ->
    {

        if (result.getContents() != null) {

            String messNo = result.getContents();
            if(sharedPreferences.getString("UserEmail", "").equals("MessNo")) {
                Toast.makeText(this, messNo, Toast.LENGTH_SHORT).show();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                DatabaseReference dbpath = db.child("mess").child(messNo).child("freeDish");
                dbpath.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            tokensLeft = snapshot.getValue(String.class);

                            if (tokensLeft.equals("0")) {
                                showInstructionDialogBox("No Token Left", "This Mess don't have token for this month");
                            } else {
                                i = Integer.parseInt(tokensLeft);
                                i = i - 1;
                                DatabaseReference dbpath = db.child("mess").child(messNo);
                                Map<String, Object> data = new HashMap<>();
                                data.put("freeDish", i + "");
                                dbpath.updateChildren(data).addOnSuccessListener(aVoid -> {
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("freeDish", "0");
                                    db.collection("User").
                                            document(sharedPreferences.getString("UserEmail", "")).
                                            update(updates).
                                            addOnSuccessListener(aVoid1 -> {
                                                // Field deleted successfully
                                                SharedPreferences.Editor preferences = sharedPreferences.edit();
                                                preferences.putString("freeDish", "0");
                                                preferences.apply();
                                                showInstructionDialogBox("Successful", "Show this message to mess owner and enjoy your free meal.");
                                            });
                                });

                            }

                        } else {
                            Toast.makeText(OfferCodeScanner.this, "Something went wrong!!!!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }else {
                showInstructionDialogBox("Not available", "You can't use this token in this mess");
            }
        }
    });

    private static boolean hasPermission(Context context, String... permissions){
        if(context!=null && permissions != null){
            for(String permission : permissions){
                if(ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }

    private void showInstructionDialogBox(String title, String mbody) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(mbody);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}