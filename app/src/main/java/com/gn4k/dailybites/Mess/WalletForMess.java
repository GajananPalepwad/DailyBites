package com.gn4k.dailybites.Mess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gn4k.dailybites.HomeForMessOwner;
import com.gn4k.dailybites.R;
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

public class WalletForMess extends AppCompatActivity {

    TextView balance, pending;
    Button withdraw;

    String balanceString, pendingString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_for_mess);

        balance = findViewById(R.id.balance);
        pending = findViewById(R.id.pending);
        withdraw = findViewById(R.id.withdraw);

        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWithdraw();
            }
        });


        updateAccordingtofirebase();

    }

    private void updateAccordingtofirebase(){

        SharedPreferences sharedPreferences = getSharedPreferences("MessOwnerData",MODE_PRIVATE);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbpath = db.child("mess")
                .child(sharedPreferences.getString("MessOwnerMobileNo", ""));

        dbpath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();

                    balanceString = (String) data.get("wallet");
                    pendingString = (String) data.get("withdrawPending");

                    balance.setText("₹"+balanceString);
                    pending.setText("₹"+pendingString);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    private void setWithdraw(){

        double balanceDouble, pendingDouble;

        balanceDouble = Double.parseDouble(balanceString);
        pendingDouble = Double.parseDouble(pendingString);

        pendingDouble = pendingDouble + balanceDouble;
        balanceDouble = 0;

        SharedPreferences sharedPreferences = getSharedPreferences("MessOwnerData",MODE_PRIVATE);
        // Update user information in Firebase database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dataRef = ref.child("mess").
                child(sharedPreferences.getString("MessOwnerMobileNo", ""));

        Map<String, Object> data = new HashMap<>();
        data.put("withdrawPending", pendingDouble+"");
        data.put("wallet", balanceDouble+"");

        dataRef.updateChildren(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Move to the home screen
                        showInstructionDialogBox("Payment Under Process", "You will receive you amount in bank account within 48 Hours");
                        updateAccordingtofirebase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error occurred while saving data
                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
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