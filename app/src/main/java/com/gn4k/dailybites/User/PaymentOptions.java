package com.gn4k.dailybites.User;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.gn4k.dailybites.Animation.RatingsDialog;
import com.gn4k.dailybites.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PaymentOptions extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private static final String KEY_WALLET_BALANCE = "WalletBalance";
    String price, walletBalance;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView tvplanPrice, tvWalletAmount;
    Button btnPay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_options);

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);

        CardView backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(v -> onBackPressed());

        btnPay = findViewById(R.id.btnPay);
        tvplanPrice = findViewById(R.id.tvPlanPrice);
        tvWalletAmount = findViewById(R.id.tvWalletAmount);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        price = extras.getString("price");
        setAllValues();

        btnPay.setOnClickListener(v -> {
            if(Integer.parseInt(price)<=Integer.parseInt(walletBalance)) {
                payment();
            }else{
                showInstructionDialogBox("Low Balance", "Insufficient wallet balance. Add "+getString(R.string.rupee)+(Integer.parseInt(price)-Integer.parseInt(walletBalance))+" to complete this payment");
            }
        });

    }

    private void setAllValues(){
        tvplanPrice.setText(getString(R.string.rupee)+price);
        btnPay.setText("Pay "+getString(R.string.rupee)+price);

        DocumentReference emailRef = db
                .collection("User")
                .document(sharedPreferences.getString("UserEmail",""));

        emailRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                walletBalance = documentSnapshot.getString(KEY_WALLET_BALANCE);
                tvWalletAmount.setText("Available Balance: " + getString(R.string.rupee) + walletBalance);
            }
        });

    }

    private void payment(){

    }

    private void onPaymentSuccessfulListener(){

    }



    private void showInstructionDialogBox(String title, String mbody) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(mbody);
        builder.setPositiveButton("OK", (dialog, which) -> {

            dialog.dismiss();
            Intent intent = new Intent(PaymentOptions.this, WalletForUser.class);
            startActivity(intent);

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}