package com.gn4k.dailybites;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gn4k.dailybites.Mess.SendNotificationToUser;
import com.gn4k.dailybites.SendNotificationClasses.FcmNotificationsSender;
import com.gn4k.dailybites.User.CaptureAct;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.pdf417.decoder.ec.ModulusGF;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.HashMap;
import java.util.Map;

public class SendMessegeToMess extends AppCompatActivity {

    private static final String KEY_MESSNAME = "OneDayMessName";
    private static final String KEY_MESSNO = "OneDayMessNo";
    private static final String KEY_MESSTOKEN = "OneDayToken";
    private static final String KEY_ORDER_ID = "OneDayOrderId";
    EditText title, body;
    Button send, scannerBtn;
    TextView name, no;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor preferences;
    DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_messege_to_mess);
        db = FirebaseDatabase.getInstance().getReference();
        title = findViewById(R.id.title);
        body = findViewById(R.id.body);
        name = findViewById(R.id.messName);
        no = findViewById(R.id.number);
        send = findViewById(R.id.send);
        scannerBtn = findViewById(R.id.qr_scanner);

        CardView backBtn = findViewById(R.id.back);
        CardView walletBtn = findViewById(R.id.wallet);

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        preferences = sharedPreferences.edit();

        name.setText("Mess Name: "+sharedPreferences.getString("OneDayMessName", ""));
        no.setText("Mess No: +91 "+sharedPreferences.getString("OneDayMessNo", ""));

        backBtn.setOnClickListener(v -> onBackPressed());

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.CAMERA,
        };

        if(!hasPermission(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS,PERMISSION_ALL);
        }else {


            scannerBtn.setOnClickListener(v -> scanCode());
        }

        walletBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SendMessegeToMess.this, WalletForUser.class);
            startActivity(intent);
        });




        send.setOnClickListener(v -> {


            DatabaseReference dbpath = db.child("mess")
                    .child((sharedPreferences.getString("MessNo", "")));

                        dbpath.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();
                                    String token = (String) data.get("token");

                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                                token,
                                title.getText().toString(),
                                body.getText().toString(),
                                getApplicationContext(),
                                SendMessegeToMess.this);

                        notificationsSender.SendNotifications();
                        } else {
                            Toast.makeText(SendMessegeToMess.this, "Something went wrong!!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        });

    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("        Scanning.....\n\n\n\n\n\n\n\nVolume up to flash on\n\n");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);

        barLaucher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result ->
    {

        if (result.getContents() != null) {

            String id = result.getContents();
            if(id.equals(sharedPreferences.getString("OneDayOrderId", ""))) {
                DatabaseReference dataRef = db.child("mess").
                        child(sharedPreferences.getString("OneDayMessNo", "")).
                        child("OneDayPlan").child("Users").
                        child(sharedPreferences.getString("UserMobileNo", ""));

                dataRef.removeValue((error, ref) -> {
                    if (error == null) {


                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put(KEY_MESSNAME, "");
                        userInfo.put(KEY_MESSNO, "");
                        userInfo.put(KEY_ORDER_ID, "");
                        userInfo.put(KEY_MESSTOKEN, "");
                        String messToken = sharedPreferences.getString("OneDayToken","");
                        preferences.putString("OneDayMessName", "");
                        preferences.putString("OneDayMessNo", "");
                        preferences.putString("OneDayOrderId", "");
                        preferences.putString("OneDayToken", "");

                        preferences.apply();
                        FirebaseFirestore db1 = FirebaseFirestore.getInstance();
                        db1.collection("User").document(sharedPreferences.getString("UserEmail", ""))
                                .update(userInfo)
                                .addOnSuccessListener(unused -> {
                                    // Move to the home screen
                                    sendNotificationToMess(messToken);
                                    sendNotificationToMess(sharedPreferences.getString("UserToken", ""));
                                    showInstructionDialogBox("Successful", "Show this message to mess agent and enjoy your food");
                                })
                                .addOnFailureListener(e -> showInstructionDialogBox("Payment failed", "If transition done by your bank, you will get money back within 48 hours."));

                    } else {
                        Toast.makeText(SendMessegeToMess.this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

    private void sendNotificationToMess(String token){
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token,
                "📢Order Delivered",
                "Congratulations!🥳🥳🥳.",
                getApplicationContext(),
                SendMessegeToMess.this);

        notificationsSender.SendNotifications();
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