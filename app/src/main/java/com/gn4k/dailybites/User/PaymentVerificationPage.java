package com.gn4k.dailybites.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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
import com.gn4k.dailybites.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class PaymentVerificationPage extends AppCompatActivity {

    InboxReader inboxReader;
    Button done, cancel, copy;
    ImageView qrCode;
    Cursor cursor;
    TextView tvAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_verification_page);

        inboxReader = new InboxReader(PaymentVerificationPage.this);
        done = findViewById(R.id.btnDone);
        copy = findViewById(R.id.btnCopy);
        qrCode = findViewById(R.id.qr_code);
        tvAmount = findViewById(R.id.tvAmount);


        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String amount = extras.getString("amount");

        tvAmount.setText(getString(R.string.rupee)+ " " + amount);

        String url = "upi://pay?pa=9975413723@paytm&pn=DailyBites&cu=INR&am=" + amount;

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
            int permissionStatus = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS);
            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                cursor = inboxReader.getInboxMessages();
                cursor.moveToFirst();
                assert amount != null;
                boolean contains = inboxReader.getMessageBody(cursor).contains(amount);
                runOnUiThread(() -> {
                    if (contains) {
                        done.setText("Payment successful");
                    } else {
                        done.setText("Payment fail");
                    }
                });
            }
        });

        cancel.setOnClickListener(v -> onBackPressed());




    }



    @Override
    protected void onRestart() {
        super.onRestart();
        Thread thread = new Thread() {
            public void run() {
                try {
                    sleep(8000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor = inboxReader.getInboxMessages();
                    cursor.moveToFirst();
                    boolean contains = inboxReader.getMessageBody(cursor).contains("dailybites");
                    runOnUiThread(() -> {
                        if (contains) {
                            done.setVisibility(View.VISIBLE);
                        } else {
                            done.setText("Payment fail");
                        }
                    });
                }
            }
        };
        thread.start();
    }


}