package com.gn4k.dailybites.User;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gn4k.dailybites.Animation.PaymentLoadingDialog;
import com.gn4k.dailybites.GetDateTime;
import com.gn4k.dailybites.R;
import com.gn4k.dailybites.RoomForTransitionHistoryMess.WalletDatabase;
import com.gn4k.dailybites.RoomForTransitionHistoryMess.WalletForMessDao;
import com.gn4k.dailybites.RoomForTransitionHistoryMess.WalletMess;
import com.gn4k.dailybites.SendNotificationClasses.FcmNotificationsSender;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PaymentOptions extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private static final String KEY_WALLET_BALANCE = "WalletBalance";
    String price, walletBalance, messWalletAmount, messName, messMobileNo, planName, messToken, delivery, OrderId, date, numberOfPlate;
    int newWalletBalance=0;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView tvplanPrice, tvWalletAmount;
    Button btnPay;

    private static final String KEY_MESSNAME = "messName";
    private static final String KEY_MESSNO = "messNo";
    private static final String KEY_PLANNAME = "planName";
    private static final String KEY_FROMDATE = "from";
    private static final String KEY_MESSTOKEN = "messToken";
    private static final String KEY_FREEDISH = "freeDish";
    private static final String KEY_TODATE = "to";
    private static final String KEY_RATING = "isRating";
    private static final String KEY_ONE_DAY_MESSNAME = "OneDayMessName";
    private static final String KEY_ONE_DAY_MESSNO = "OneDayMessNo";
    private static final String KEY_ONE_DAY_MESSTOKEN = "OneDayToken";
    private static final String KEY_ONE_DAY_ORDER_ID = "OneDayOrderId";

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
        messWalletAmount = extras.getString("messWalletAmount");
        messName = extras.getString("messName");
        messMobileNo = extras.getString("messMobileNo");
        planName = extras.getString("planName");
        messToken = extras.getString("messToken");
        delivery = extras.getString("delivery");
        numberOfPlate = extras.getString("plateCount");
        OrderId = generateOrderId();

        setAllValues();

        btnPay.setOnClickListener(v -> {
            PaymentLoadingDialog paymentLoadingDialog = new PaymentLoadingDialog(this);

            if(Integer.parseInt(price)<=Integer.parseInt(walletBalance)) {
                if(planName.equals("OneDay")){

                    paymentLoadingDialog.startLoading();
                    Thread thread = new Thread() {

                        public void run() {
                            try {
                                sleep(4000);

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                oneDayPayment();
                            }
                        }
                    };thread.start();
                }else {

                    paymentLoadingDialog.startLoading();
                    Thread thread = new Thread() {

                        public void run() {
                            try {
                                sleep(4000);

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                monthlyPayment();
                            }
                        }
                    };thread.start();
                }
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
                newWalletBalance = Integer.parseInt(walletBalance) - Integer.parseInt(price);
            }
        });

    }


    private void oneDayPayment(){

        GetDateTime getDateTime = new GetDateTime(this);
        getDateTime.getDateTime((date, time) -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor preferences = sharedPreferences.edit();

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put(KEY_ONE_DAY_MESSNAME, messName);
            userInfo.put(KEY_ONE_DAY_MESSNO, messMobileNo);
            userInfo.put(KEY_ONE_DAY_ORDER_ID, OrderId);
            userInfo.put(KEY_ONE_DAY_MESSTOKEN, messToken);
            userInfo.put(KEY_WALLET_BALANCE, newWalletBalance+"");

            preferences.putString("OneDayMessName", messName);
            preferences.putString("OneDayMessNo", messMobileNo);
            preferences.putString("OneDayOrderId", OrderId);
            preferences.putString("OneDayToken", messToken);
            preferences.putString("previousBalance", newWalletBalance+"");

            preferences.apply();

            db.collection("User").document(sharedPreferences.getString("UserEmail", ""))
                    .update(userInfo)
                    .addOnSuccessListener(unused -> {
                        // Move to the home screen
                        Toast.makeText(PaymentOptions.this, "Plan added successfully", Toast.LENGTH_SHORT).show();
                        sendNotificationToMess();
                    })
                    .addOnFailureListener(e -> showInstructionDialogBox("Payment failed", "If transition done by your wallet, you will get money back within 48 hours."));

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            DatabaseReference dataRef = ref.child("mess").child(messMobileNo).child("OneDayPlan").child("Users").child(sharedPreferences.getString("UserMobileNo", ""));

            Map<String, Object> data = new HashMap<>();
            data.put("plan", "One Day Plan");
            data.put("forDate", date);
            data.put("time", time);
            data.put("orderId", OrderId);
            data.put("delivery", delivery);
            data.put("numberOfPlate", numberOfPlate);
            data.put("name", sharedPreferences.getString("UserName", ""));
            data.put("email", sharedPreferences.getString("UserEmail", ""));
            data.put("token", sharedPreferences.getString("UserToken", ""));
            data.put("address", sharedPreferences.getString("UserAddress", ""));
            data.put("latitude", sharedPreferences.getString("UserLatitude", ""));
            data.put("mobileNo", sharedPreferences.getString("UserMobileNo", ""));
            data.put("longitude", sharedPreferences.getString("UserLongitude", ""));





            dataRef.updateChildren(data)
                    .addOnSuccessListener(aVoid ->{
                        DatabaseReference dataRef2 = ref.child("mess").child(messMobileNo).child("wallet");
                        double finalAmount = Double.parseDouble(messWalletAmount) + Double.parseDouble(price);
                        dataRef2.setValue(new DecimalFormat("#####.##").format(finalAmount));
                        gettime();
                        Intent intent = new Intent(PaymentOptions.this, SendMessegeToMess.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // Error occurred while saving data
                        showInstructionDialogBox("Payment failed", "If transition done by your bank, you will get money back within 48 hours.");
                        onBackPressed();
                        finish();
                    });
        });
    }

    private void monthlyPayment(){

        GetDateTime getDateTime = new GetDateTime(this);
        getDateTime.getDateTime((date, time) -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor preferences = sharedPreferences.edit();

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put(KEY_MESSNAME, messName);
            userInfo.put(KEY_MESSNO, messMobileNo);
            userInfo.put(KEY_PLANNAME, planName + " Plan");
            userInfo.put(KEY_FROMDATE, date);
            userInfo.put(KEY_MESSTOKEN, messToken);
            userInfo.put(KEY_FREEDISH, "1");
            userInfo.put(KEY_RATING, "1");
            userInfo.put(KEY_WALLET_BALANCE, newWalletBalance+"");

            preferences.putString("messName", messName);
            preferences.putString("MessNo", messMobileNo);
            preferences.putString("planName", planName + " Plan");
            preferences.putString("fromDate", date);
            preferences.putString("freeDish", "1");
            preferences.putString("isRating", "1");
            preferences.putString("messToken", messToken);
            preferences.putString("previousBalance", newWalletBalance+"");

            String nextMonthDateString = getNextMonthDate(date);

            userInfo.put(KEY_TODATE, nextMonthDateString);
            preferences.putString("toDate", nextMonthDateString);
            preferences.apply();

            db.collection("User").document(sharedPreferences.getString("UserEmail", ""))
                    .update(userInfo)
                    .addOnSuccessListener(unused -> {
                        // Move to the home screen
                        Toast.makeText(PaymentOptions.this, "Plan added successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> showInstructionDialogBox("Payment failed", "If transition done by your bank, you will get money back within 48 hours."));

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            DatabaseReference dataRef = ref.child("mess").child(messMobileNo).child(planName + "plan").child("Users").child(sharedPreferences.getString("UserMobileNo", ""));

            Map<String, Object> data = new HashMap<>();
            data.put("name", sharedPreferences.getString("UserName", ""));
            data.put("email", sharedPreferences.getString("UserEmail", ""));
            data.put("plan", planName + " Plan");
            data.put("fromDate", date);
            data.put("orderId", OrderId);
            data.put("latitude", sharedPreferences.getString("UserLatitude", ""));
            data.put("longitude", sharedPreferences.getString("UserLongitude", ""));
            data.put("address", sharedPreferences.getString("UserAddress", ""));
            data.put("mobileNo", sharedPreferences.getString("UserMobileNo", ""));
            data.put("toDate", nextMonthDateString);
            data.put("token", sharedPreferences.getString("UserToken", ""));


            dataRef.updateChildren(data)
                    .addOnSuccessListener(aVoid -> {
                        // Data saved successfully
                        DatabaseReference dataRef2 = ref.child("mess").child(messMobileNo).child("wallet");
                        double finalAmount = Double.parseDouble(messWalletAmount) + Double.parseDouble(price);
                        dataRef2.setValue(new DecimalFormat("#####.##").format(finalAmount));
                        sendNotificationToMess();
                        gettime();
                        onBackPressed();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // Error occurred while saving data
                        showInstructionDialogBox("Payment failed", "If transition done by your wallet, you will get money back within 48 hours.");
                        onBackPressed();
                        finish();
                    });
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
                messDao.insert(new WalletMess(initialUid,"Plan Subscribed", date, "-₹"+price));
            } else {
                long nextUid = lastUid + 1;

                if (messDao.isExistByMessNo(date)) {
                    WalletMess existingDate = messDao.getMessByUid(date);
                    messDao.delete(existingDate);
                    messDao.insert(new WalletMess(nextUid, "Plan Subscribed", date, "-₹"+price));
                } else {
                    messDao.insert(new WalletMess(nextUid, "Plan Subscribed", date, "-₹"+price));
                }

            }
        }
    }


    private void gettime(){
        GetDateTime getDateTime = new GetDateTime(PaymentOptions.this);
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




    private String getNextMonthDate(String currentDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            // Parse the date string to a Date object
            Date date2 = dateFormat.parse(currentDate);

            // Convert the Date to Calendar
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date2);

            // Add one month to the date
            calendar.add(Calendar.MONTH, 1);

            // Get the next month date
            Date nextMonthDate = calendar.getTime();

            // Format the next month date to mm/dd/yyyy format
            return dateFormat.format(nextMonthDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String generateOrderId() {
        long timestampMs = System.currentTimeMillis();
        int randomNumber = (int) (Math.random() * 9000) + 1000;
        String orderId = timestampMs + "-" + randomNumber;
        return orderId;
    }


    private void sendNotificationToMess(){
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(messToken,
                "📢New Customer Alert!",
                "Congratulations!🥳🥳🥳 \nYou've just acquired a new customer for "+planName + " Plan"+". Take a moment to celebrate this achievement and get ready to provide exceptional service.",
                getApplicationContext(),
                PaymentOptions.this);

        notificationsSender.SendNotifications();
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