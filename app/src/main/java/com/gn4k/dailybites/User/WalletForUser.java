package com.gn4k.dailybites.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gn4k.dailybites.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class WalletForUser extends AppCompatActivity {

    private Dialog dialog;
    String amount, upi;
    TextView tvBalance, tvTotalPending;
    SharedPreferences sharedPreferences;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String KEY_WALLET_BALANCE = "WalletBalance";
    private static final String KEY_PENDING_BALANCE = "PendingDeposit";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_for_user);

        CardView backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(v -> onBackPressed());

        Button deposit = findViewById(R.id.deposite);

        tvBalance = findViewById(R.id.tvBalance);

        tvTotalPending = findViewById(R.id.tvTotalPending);

        sharedPreferences = getSharedPreferences("UserData",MODE_PRIVATE);

        deposit.setOnClickListener(v -> addAmountPopUp());

        setDataAccordingToFirebase();

    }


    public void setDataAccordingToFirebase(){
        DocumentReference emailRef = db
                .collection("User")
                .document(sharedPreferences.getString("UserEmail",""));

        emailRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){

                tvBalance.setText(getString(R.string.rupee)+documentSnapshot.getString(KEY_WALLET_BALANCE));
                tvTotalPending.setText(getString(R.string.rupee)+documentSnapshot.getString(KEY_PENDING_BALANCE));


            }
        });
    }


    private void addAmountPopUp(){

        dialog = new Dialog(this, R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(R.layout.add_amount_popup);

        Button pay, cancel;
        TextView edAmount, edUpi;

        pay = dialog.findViewById(R.id.btn_pay);
        cancel = dialog.findViewById(R.id.btn_cancel);
        edAmount = dialog.findViewById(R.id.edAmount);
        edUpi = dialog.findViewById(R.id.edUpi);




        pay.setOnClickListener(v -> {

            amount = edAmount.getText().toString();
            upi = edUpi.getText().toString();

            if(!amount.isEmpty()) {

                Intent intent = new Intent(WalletForUser.this, PaymentVerificationPage.class);
                intent.putExtra("amount", amount.replaceAll("\\s", ""));
                intent.putExtra("upi", upi);
                intent.putExtra("preBalance", tvBalance.getText().toString());
                dialog.dismiss();
                startActivity(intent);

            }else{
                Toast.makeText(this, "Please add Amount", Toast.LENGTH_SHORT).show();
            }

        });


        cancel.setOnClickListener(v -> dialog.dismiss());


        dialog.setCancelable(false);
        dialog.show();

    }

}