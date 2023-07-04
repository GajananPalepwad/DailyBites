package com.gn4k.dailybites.Mess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gn4k.dailybites.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QrCodeGenerator extends AppCompatActivity {

    ImageView qrCode;
    TextView name;
    SharedPreferences sharedPreferences;
    String qr_text="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_generator);

        sharedPreferences = getSharedPreferences("MessOwnerData",MODE_PRIVATE);

        qrCode = findViewById(R.id.qr_code);
        name = findViewById(R.id.messName);
        CardView backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(v -> onBackPressed());
        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("qr_data").equals("offer")){
            qr_text = sharedPreferences.getString("MessOwnerMobileNo", "");
        }else{
            qr_text = bundle.getString("qr_data");
        }

        name.setText(sharedPreferences.getString("MessName", ""));

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{

            BitMatrix bitMatrix = multiFormatWriter.encode(qr_text, BarcodeFormat.QR_CODE, 300, 300);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            qrCode.setImageBitmap(bitmap);

        }catch (WriterException e){
            Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
        }

    }
}