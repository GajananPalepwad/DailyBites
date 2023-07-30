package com.gn4k.dailybites.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gn4k.dailybites.GetDateTime;
import com.gn4k.dailybites.Mess.WalletForMess;
import com.gn4k.dailybites.R;
import com.gn4k.dailybites.RoomForTransitionHistoryMess.WalletDatabase;
import com.gn4k.dailybites.RoomForTransitionHistoryMess.WalletDepositeAdapter;
import com.gn4k.dailybites.RoomForTransitionHistoryMess.WalletForMessDao;
import com.gn4k.dailybites.RoomForTransitionHistoryMess.WalletMess;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class WalletForUser extends AppCompatActivity {

    private Dialog dialog;
    String amount, upi, totalPending, totalBalance, date, status, prevTotalPending, prevTotalBalance;
    TextView tvBalance, tvTotalPending;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor preferences;
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

        recyclerView = findViewById(R.id.recyclerView);

        sharedPreferences = getSharedPreferences("UserData",MODE_PRIVATE);
        preferences = sharedPreferences.edit();

        deposit.setOnClickListener(v -> addAmountPopUp());

        setDataAccordingToFirebase();
        new Bgthread(recyclerView).start();
    }


    class Bgthread extends Thread {  // to display recent transition history in recyclerView
        private RecyclerView recyclerView;

        Bgthread(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        public void run() {
            super.run();

            WalletDatabase messdb = Room.databaseBuilder(WalletForUser.this, WalletDatabase.class, "WalletView_DB").build();
            WalletForMessDao messDao = messdb.walletDao();
            List<WalletMess> mess = messDao.getAllMess();
            Collections.reverse(mess);

            WalletForUser.this.runOnUiThread(() -> {
                recyclerView.setLayoutManager(new LinearLayoutManager(WalletForUser.this));
                WalletDepositeAdapter WalletAdapter = new WalletDepositeAdapter(WalletForUser.this,mess);
                recyclerView.setAdapter(WalletAdapter);
            });
        }
    }

    class CompletedAddBgthread extends Thread {
        public void run() {
            super.run();

            WalletDatabase messdb = Room.databaseBuilder(getApplicationContext(),
                    WalletDatabase.class, "WalletView_DB").build();

            WalletForMessDao messDao = messdb.walletDao();

            long lastUid = messDao.getLastMessUid();

            if (lastUid == 0) {
                // Database is empty, set initial uid to 1
                int initialUid = 1;
                messDao.insert(new WalletMess(initialUid,status, date, "₹"+ prevTotalPending));
            } else {
                long nextUid = lastUid + 1;

                if (messDao.isExistByMessNo(date)) {
                    WalletMess existingDate = messDao.getMessByUid(date);
                    messDao.delete(existingDate);
                    messDao.insert(new WalletMess(nextUid, status, date, "₹"+ prevTotalPending));
                } else {
                    messDao.insert(new WalletMess(nextUid, status, date, "₹"+ prevTotalPending));
                }

            }
        }
    }

    private void gettime(){
        GetDateTime getDateTime = new GetDateTime(WalletForUser.this);
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
            new CompletedAddBgthread().start();
        });

    }

    public void setDataAccordingToFirebase(){
        DocumentReference emailRef = db
                .collection("User")
                .document(sharedPreferences.getString("UserEmail",""));

        emailRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){

                totalBalance = documentSnapshot.getString(KEY_WALLET_BALANCE);
                totalPending = documentSnapshot.getString(KEY_PENDING_BALANCE);

                tvBalance.setText(getString(R.string.rupee)+totalBalance);
                tvTotalPending.setText(getString(R.string.rupee)+totalPending);

                prevTotalBalance = sharedPreferences.getString("previousBalance","");
                prevTotalPending = sharedPreferences.getString("previousPendingDeposit","");

                assert prevTotalBalance != null;
                if(Integer.parseInt(totalBalance) == Integer.parseInt(prevTotalBalance)+Integer.parseInt(prevTotalPending)
                        && !prevTotalPending.equals("0")
                        && totalPending.equals("0")) {
                    status = "Completed";

                    preferences.putString("previousPendingDeposit", "0");
                    preferences.putString("previousBalance", totalBalance);
                    preferences.apply();

                    gettime();
                } else if (Integer.parseInt(totalBalance) == Integer.parseInt(prevTotalBalance)
                        && !prevTotalPending.equals("0")
                        && totalPending.equals("0") ) {
                    status = "Rejected";

                    preferences.putString("previousPendingDeposit", "0");
                    preferences.putString("previousBalance", totalBalance);
                    preferences.apply();

                    gettime();
                }



            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setDataAccordingToFirebase();
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
                intent.putExtra("preBalance", totalBalance);
                intent.putExtra("pendingBalance", totalPending);
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