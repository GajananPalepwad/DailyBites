package com.gn4k.dailybites.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gn4k.dailybites.R;

public class WalletForUser extends AppCompatActivity {

    private Dialog dialog;
    String amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_for_user);
        CardView backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(v -> onBackPressed());
        Button deposit = findViewById(R.id.deposite);

        deposit.setOnClickListener(v -> addAmountPopUp());


    }

    public void addAmountPopUp(){

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
            if(!amount.isEmpty()) {

                Intent intent = new Intent(WalletForUser.this, PaymentVerificationPage.class);
                intent.putExtra("amount", amount.replaceAll("\\s", ""));
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