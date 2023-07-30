package com.gn4k.dailybites.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gn4k.dailybites.InboxReader;
import com.gn4k.dailybites.Mess.MapToLocateMess;
import com.gn4k.dailybites.Mess.MessRegistration;
import com.gn4k.dailybites.R;
import com.gn4k.dailybites.SendNotificationClasses.FcmNotificationsSender;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class PaymentVerificationPage extends AppCompatActivity {

    InboxReader inboxReader;
    Button done, cancel, copy;
    ImageView qrCode;
    Cursor cursor;
    TextView tvAmount;
    String amount;
    String upi, preBalance;
    String status = "under review";


    SharedPreferences sharedPreferencesUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_verification_page);

        sharedPreferencesUser = getSharedPreferences("UserData",MODE_PRIVATE);
        inboxReader = new InboxReader(PaymentVerificationPage.this);
        done = findViewById(R.id.btnDone);
        copy = findViewById(R.id.btnCopy);
        cancel = findViewById(R.id.btnCancel );

        qrCode = findViewById(R.id.qr_code);
        tvAmount = findViewById(R.id.tvAmount);


        Bundle extras = getIntent().getExtras();
        assert extras != null;
        amount = extras.getString("amount");
        upi = extras.getString("upi");
        preBalance = extras.getString("preBalance");

        tvAmount.setText(getString(R.string.rupee)+ " " + amount);

        String url = "upi://pay?pa=dailybites.pay@ibl&pn=DailyBites&cu=INR&am=" + amount;

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{

            BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE, 300, 300);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            qrCode.setImageBitmap(bitmap);

        }catch (WriterException e){
            Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
        }



        copy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            ClipData clipData = ClipData.newPlainText("text", getResources().getString(R.string.upi));

            clipboard.setPrimaryClip(clipData);

            Toast.makeText(getApplicationContext(), "UPI ID copied", Toast.LENGTH_SHORT).show();
        });

        done.setOnClickListener(v -> {
            sendForVerification();
        });

        cancel.setOnClickListener(v -> onBackPressed());

    }

    private void sendForVerification(){

        StringTokenizer tokenizer = new StringTokenizer(sharedPreferencesUser.getString("UserEmail",""), "@");
        String username = tokenizer.nextToken();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dataRef = ref.child("DepositRequest").child(username);

        Map<String, Object> data = new HashMap<>();
        data.put("email", sharedPreferencesUser.getString("UserEmail",""));
        data.put("mobileNo", sharedPreferencesUser.getString("UserMobileNo",""));
        data.put("amount", amount);
        data.put("upi", upi);
        data.put("preBalance", preBalance);

        int permissionStatus = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            cursor = inboxReader.getInboxMessages();
            cursor.moveToFirst();
            assert amount != null;
            boolean contains = inboxReader.getMessageBody(cursor).contains(amount);
            if (contains) {
                data.put("statue", "completed");
                status = "completed";
            } else {
                data.put("statue", "review");
                status = "under review";
            }
        }else{
            data.put("statue", "review");
            status = "under review";
        }

        dataRef.setValue(data)
                .addOnSuccessListener(aVoid -> {
                    showInstructionDialogBox("Payment "+status,"You will receive amount in wallet within 30 Minute");
                })
                .addOnFailureListener(e -> {
                    // Error occurred while saving data
                    Toast.makeText(PaymentVerificationPage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                });

    }

    private void showInstructionDialogBox(String title, String mbody) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(mbody);
        builder.setPositiveButton("OK", (dialog, which) -> {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("User").document(sharedPreferencesUser.getString("UserEmail",""));

            Map<String, Object> updates = new HashMap<>();
            updates.put("PendingDeposit", amount+"");

            docRef.update(updates);
            notifyAdmin();
            onBackPressed();
            finish();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void notifyAdmin(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbpath = db.child("Admin");
        dbpath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();
                    String token = (String) data.get("token");

                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                            token,
                            upi,
                            "Requested to add "+amount+" rupee",
                            getApplicationContext(),
                            PaymentVerificationPage.this);

                    notificationsSender.SendNotifications();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}