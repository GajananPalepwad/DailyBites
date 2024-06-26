package com.gn4k.dailybites.Mess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.gn4k.dailybites.Animation.LoadingDialog;
import com.gn4k.dailybites.GetDateTime;

import com.gn4k.dailybites.R;


import com.gn4k.dailybites.RoomForTransitionHistoryMess.WalletDatabase;
import com.gn4k.dailybites.RoomForTransitionHistoryMess.WalletForMessDao;
import com.gn4k.dailybites.RoomForTransitionHistoryMess.WalletWithdrawAdapter;
import com.gn4k.dailybites.RoomForTransitionHistoryMess.WalletMess;


import com.gn4k.dailybites.SendNotificationClasses.FcmNotificationsSender;
import com.gn4k.dailybites.User.PaymentVerificationPage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalletForMess extends AppCompatActivity {

    TextView balance, pending;
    Button withdrawbtn;
    String passwordFromFB, date;
    private BottomSheetDialog bottomSheetDialog;
    String balanceString, pendingString;
    RecyclerView recyclerView;
    LoadingDialog loadingDialog;
    ImageView btnInfo;
    double balanceDouble, pendingDouble;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_for_mess);
        loadingDialog = new LoadingDialog(WalletForMess.this);
        loadingDialog.startLoading();

        btnInfo = findViewById(R.id.btnInfo);
        balance = findViewById(R.id.balance);
        pending = findViewById(R.id.pending);
        withdrawbtn = findViewById(R.id.withdrawBtn);
        recyclerView = findViewById(R.id.recyclerViewWallet);


        CardView backBtn = findViewById(R.id.back);



        backBtn.setOnClickListener(v -> onBackPressed());


        btnInfo.setOnClickListener(v -> {
            showInstructionDialogBox("", "Your Daily Bites wallet balance available for online payment, in app subscription only");
        });

        withdrawbtn.setOnClickListener(v -> {
            if(Double.parseDouble(balanceString)>1.0){
                showPasswordBottomSheetDialog();
            }else{
                showInstructionDialogBox("Not enough Balance", "You should have balance more that ₹1 to withdraw");
            }
        });
        updateAccordingtofirebase();


        new Bgthread(recyclerView).start();

    }


    class Bgthread extends Thread {  // to display recent transition history in recyclerView
        private RecyclerView recyclerView;

        Bgthread(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        public void run() {
            super.run();

            WalletDatabase messdb = Room.databaseBuilder(WalletForMess.this, WalletDatabase.class, "WalletView_DB").build();
            WalletForMessDao messDao = messdb.walletDao();
            List<WalletMess> mess = messDao.getAllMess();
            Collections.reverse(mess);

            WalletForMess.this.runOnUiThread(() -> {
                recyclerView.setLayoutManager(new LinearLayoutManager(WalletForMess.this));
                WalletWithdrawAdapter WalletAdapter = new WalletWithdrawAdapter(WalletForMess.this,mess);
                recyclerView.setAdapter(WalletAdapter);
            });
        }
    }


    private void gettime(){
        GetDateTime getDateTime = new GetDateTime(WalletForMess.this);
        getDateTime.getDateTime((date2, time) -> {
            DateFormat inputDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            DateFormat outputDateFormat = new SimpleDateFormat("dd MMM yy");
            String finalDate="", finalTime="";
            try {
                Date date3 = inputDateFormat.parse(date2);
                String formattedDate = outputDateFormat.format(date3);
                finalDate = formattedDate;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            DateFormat inputTimeFormat = new SimpleDateFormat("HH:mm");
            DateFormat outputTimeFormat = new SimpleDateFormat("hh:mm a");

            try {
                Date Time = inputTimeFormat.parse(time);
                String formattedTime = outputTimeFormat.format(Time);
                finalTime = formattedTime.toUpperCase();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            date = finalDate + ", " + finalTime;
            new PendingAddBgthread().start();
        });

    }


    class PendingAddBgthread extends Thread {
        public void run() {
            super.run();

            WalletDatabase messdb = Room.databaseBuilder(getApplicationContext(),
                    WalletDatabase.class, "WalletView_DB").build();

            WalletForMessDao messDao = messdb.walletDao();

            long lastUid = messDao.getLastMessUid();


            if (lastUid == 0) {
                // Database is empty, set initial uid to 1
                int initialUid = 1;
                messDao.insert(new WalletMess(initialUid,"Pending", date, "₹"+pendingString));
            } else {
                long nextUid = lastUid + 1;

                if (messDao.isExistByMessNo(date)) {
                    WalletMess existingDate = messDao.getMessByUid(date);
                    messDao.delete(existingDate);
                    messDao.insert(new WalletMess(nextUid, "Under Review", date, "₹"+pendingString));
                } else {
                    messDao.insert(new WalletMess(nextUid, "Under Review", date, "₹"+pendingString));
                }

            }
        }
    }

    private void getTimeForCompletePayment(){
        GetDateTime getDateTime = new GetDateTime(WalletForMess.this);
        getDateTime.getDateTime(new GetDateTime.VolleyCallBack() {
            @Override
            public void onGetDateTime(String date2, String time) {
                DateFormat inputDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                DateFormat outputDateFormat = new SimpleDateFormat("dd MMM yy");
                String finalDate="", finalTime="";
                try {
                    Date date3 = inputDateFormat.parse(date2);
                    String formattedDate = outputDateFormat.format(date3);
                    finalDate = formattedDate;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                DateFormat inputTimeFormat = new SimpleDateFormat("HH:mm");
                DateFormat outputTimeFormat = new SimpleDateFormat("hh:mm a");

                try {
                    Date Time = inputTimeFormat.parse(time);
                    String formattedTime = outputTimeFormat.format(Time);
                    finalTime = formattedTime.toUpperCase();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                date = finalDate + ", " + finalTime;
                new CompletedAddBgthread().start();
            }
        });

    }

    class CompletedAddBgthread extends Thread {
        public void run() {
            super.run();

            WalletDatabase messdb = Room.databaseBuilder(getApplicationContext(),
                    WalletDatabase.class, "WalletView_DB").build();

            WalletForMessDao messDao = messdb.walletDao();

            long lastUid = messDao.getLastMessUid();

            SharedPreferences sharedPreferences = getSharedPreferences("MessOwnerData",MODE_PRIVATE);

            if (lastUid == 0) {
                // Database is empty, set initial uid to 1
                int initialUid = 1;
                messDao.insert(new WalletMess(initialUid,"Completed", date, "₹"+ PreviousWithdraw));
            } else {
                long nextUid = lastUid + 1;

                if (messDao.isExistByMessNo(date)) {
                    WalletMess existingDate = messDao.getMessByUid(date);
                    messDao.delete(existingDate);
                    messDao.insert(new WalletMess(nextUid, "Completed", date, "₹"+ PreviousWithdraw));
                } else {
                    messDao.insert(new WalletMess(nextUid, "Completed", date, "₹"+ PreviousWithdraw));
                }

            }
        }
    }

    String PreviousWithdraw;

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
                    passwordFromFB = (String) data.get("password");

                    balance.setText("₹"+balanceString);
                    pending.setText("₹"+pendingString);
                    PreviousWithdraw = sharedPreferences.getString("PreviousWithdraw", "");
                    if( ("₹"+pendingString).equals("₹0.0") && !PreviousWithdraw.equals("₹0.0")){
                        SharedPreferences.Editor preferences = sharedPreferences.edit();
                        getTimeForCompletePayment();
                        preferences.putString("PreviousWithdraw", "₹0.0");
                        preferences.apply();
                    }


                }
                loadingDialog.stopLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    private void setWithdraw(){



        balanceDouble = Double.parseDouble(balanceString);
        pendingDouble = Double.parseDouble(pendingString);

        pendingDouble = pendingDouble + balanceDouble;
        balanceDouble = 0;

        SharedPreferences sharedPreferences = getSharedPreferences("MessOwnerData",MODE_PRIVATE);
        SharedPreferences.Editor preferences = sharedPreferences.edit();

        preferences.putString("PreviousWithdraw", String.valueOf(pendingDouble));
        preferences.apply();
        // Update user information in Firebase database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dataRef = ref.child("mess").
                child(sharedPreferences.getString("MessOwnerMobileNo", ""));

        Map<String, Object> data = new HashMap<>();
        data.put("withdrawPending", pendingDouble+"");
        data.put("wallet", balanceDouble+"");

        dataRef.updateChildren(data)
                .addOnSuccessListener(aVoid -> {
                    // Move to the home screen
//                    showInstructionDialogBox("Payment Under Process", "You will receive amount in bank account within 48 Hours");
                    updateAccordingtofirebase();
                    sendRequestToAdmin();
                    gettime();
                })
                .addOnFailureListener(e -> {
                    // Error occurred while saving data
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                });

    }


    private void sendRequestToAdmin() {
        SharedPreferences sharedPreferences = getSharedPreferences("MessOwnerData", MODE_PRIVATE);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dataRef = ref.child("WithdrawRequest").child(sharedPreferences.getString("MessOwnerMobileNo",""));

        Map<String, Object> data = new HashMap<>();
        data.put("email", sharedPreferences.getString("MessOwnerEmail",""));
        data.put("mobileNo", sharedPreferences.getString("MessOwnerMobileNo",""));
        data.put("amount", pendingDouble+"");
        dataRef.setValue(data)
                .addOnSuccessListener(aVoid -> {
                    notifyAdmin();
                    showInstructionDialogBox("Payment Under Review","You will receive amount in you account within 48 Hours\n\nClick on OK to Confirm");
                })
                .addOnFailureListener(e -> {
                    // Error occurred while saving data
                    Toast.makeText(WalletForMess.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                });
    }

    private void notifyAdmin(){
        SharedPreferences sharedPreferences = getSharedPreferences("MessOwnerData", MODE_PRIVATE);
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
                            sharedPreferences.getString("MessOwnerMobileNo",""),
                            "Requested to withdraw "+pendingDouble+" rupee",
                            getApplicationContext(),
                            WalletForMess.this);

                    notificationsSender.SendNotifications();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void showPasswordBottomSheetDialog() {


        // Inflate the layout for the BottomSheetDialog
        View bottomSheetView = getLayoutInflater().inflate(R.layout.password_check_bottomsheet, (ConstraintLayout) findViewById(R.id.password_check_bottomsheet_id));

        // Find your button or any other view inside the BottomSheetDialog layout
        Button withdraw = bottomSheetView.findViewById(R.id.withdraw);
        EditText pass = bottomSheetView.findViewById(R.id.tvPassword);
        // Set click listener for the button inside the BottomSheetDialog

        withdraw.setOnClickListener(v -> {

            if(pass.getText().toString().equals(passwordFromFB)){
                setWithdraw();
                bottomSheetDialog.dismiss();
            }else{
                Toast.makeText(WalletForMess.this, "Invalid Password", Toast.LENGTH_SHORT).show();
            }

        });


        // Create the BottomSheetDialog
        bottomSheetDialog = new BottomSheetDialog(WalletForMess.this,R.style.AppBottomSheetDialogTheme);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
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