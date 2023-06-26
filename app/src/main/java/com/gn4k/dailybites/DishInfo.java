package com.gn4k.dailybites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gn4k.dailybites.Animatin.LoadingDialog;
import com.gn4k.dailybites.Mess.WalletForMess;
import com.gn4k.dailybites.SendNotificationClasses.FcmNotificationsSender;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DishInfo extends AppCompatActivity implements PaymentResultListener{

    private static final String KEY_MESSNAME = "messName";
    private static final String KEY_MESSNO = "messNo";
    private static final String KEY_PLANNAME = "planName";
    private static final String KEY_FROMDATE = "from";
    private static final String KEY_TODATE = "to";
    private static final String KEY_MESSTOKEN = "messToken";
    Bundle bundle;
    RadioButton withDeliveryRadioButton;
    RadioButton withoutDeliveryRadioButton;
    int planPrice = 0;
    String token="";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_info);
        loadingDialog = new LoadingDialog(DishInfo.this);
        loadingDialog.startLoading();


        bundle = getIntent().getExtras();
        TextView MessName = findViewById(R.id.MessName);
        MessName.setText(bundle.getString("messName"));
//
        ImageView cover = findViewById(R.id.coverImgB);
        Glide.with(DishInfo.this).load(bundle.getString("messCoverImage")).centerCrop().placeholder(R.drawable.silver).into(cover);
//
        TextView menu = findViewById(R.id.menuB);
        menu.setText(bundle.getString("messToDayDish"));
//
        withDeliveryRadioButton = findViewById(R.id.WithRadioButton);
        withoutDeliveryRadioButton = findViewById(R.id.WithOutRadioButton);
//
        CardView backBtn = findViewById(R.id.back);
        CardView notificationBtn = findViewById(R.id.notification);
        CardView walletBtn = findViewById(R.id.wallet);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DishInfo.this, SendMessegeToMess.class);
                startActivity(intent);
            }
        });

        walletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DishInfo.this, WalletForUser.class);
                startActivity(intent);
            }
        });

        TextView priceW = findViewById(R.id.Tv_prizeW);
        if (bundle.getString("messIsDelivery").equals("no")){
            priceW.setText("Not Available");
            withDeliveryRadioButton.setEnabled(false);
            withoutDeliveryRadioButton.setChecked(true);
            planPrice = Integer.parseInt(bundle.getString("messDishPrize"))*100;
        }else {
            priceW.setText("₹"+(Integer.parseInt(bundle.getString("messDishPrize"))+40)+"");
        }

        TextView priceWO = findViewById(R.id.Tv_prizeWO);
        priceWO.setText("₹"+bundle.getString("messDishPrize"));

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbpath = db.child("mess")
                .child(bundle.getString("messMobile"));

        dbpath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();
                    RatingBar myRatingBar = findViewById(R.id.myRatingBarB);
                    TextView ratings = findViewById(R.id.ratingsB);
                    ratings.setText((String) data.get("ratings"));
                    myRatingBar.setRating(Float.parseFloat((String) data.get("ratings")));
                    token = (String) data.get("token");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        withDeliveryRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planPrice = (Integer.parseInt(bundle.getString("messDishPrize"))+40)*100;
                // Set 'With Delivery' radio button as checked
                withDeliveryRadioButton.setChecked(true);
                // Uncheck 'Without Delivery' radio button
                withoutDeliveryRadioButton.setChecked(false);
            }
        });

        withoutDeliveryRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planPrice = Integer.parseInt(bundle.getString("messDishPrize"))*100;
                // Set 'Without Delivery' radio button as checked
                withoutDeliveryRadioButton.setChecked(true);
                // Uncheck 'With Delivery' radio button
                withDeliveryRadioButton.setChecked(false);
            }
        });






        Button gotomap = findViewById(R.id.location);

        gotomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DishInfo.this, MessInfo.class);
                intent.putExtra("messMobile", bundle.getString("messMobile"));
                intent.putExtra("messName", bundle.getString("messName"));
                intent.putExtra("messLatitude", bundle.getString("messLatitude"));
                intent.putExtra("messLongitude", bundle.getString("messLongitude"));
                startActivity(intent);
            }
        });

        Button buy = findViewById(R.id.buy);

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);

                if(sharedPreferences.getString("planName", "").equals("")){

                    if (withDeliveryRadioButton.isChecked() || withoutDeliveryRadioButton.isChecked()) {
                        startPayment();
                    } else {
                        showInstructionDialogBox("Choose a option", "with delivery or without delivery");
                    }
                }else {
                    showInstructionDialogBox("Plan Exist", "You already have an another plan....");
                }
            }
        });
        loadingDialog.stopLoading();
    }



    public void startPayment() { //Payment gateway opener method

        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String mobileNo = sharedPreferences.getString("UserMobileNo", "");
        String email = sharedPreferences.getString("UserEmail", "");

        Checkout checkout = new Checkout();
        checkout.setImage(R.drawable.logo__1_);
        final Activity activity = this;
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Daily Bites");
            options.put("description", "For "+"One Day"+" Plan of "+bundle.getString("messName"));
            options.put("send_sms_hash", true);
            options.put("allow_rotation", false);
            options.put("currency", "INR");
            options.put("amount", planPrice);

            JSONObject preFill = new JSONObject();
            preFill.put("email", email);
            preFill.put("contact", mobileNo);

            options.put("prefill", preFill);

            checkout.open(activity, options);

        } catch (JSONException e) {
            Toast.makeText(activity, e+"", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onPaymentSuccess(String s) {
//        Toast.makeText(this, "Payment successful " + s, Toast.LENGTH_SHORT).show();

        GetDateTime getDateTime = new GetDateTime(this);
        getDateTime.getDateTime(new GetDateTime.VolleyCallBack() {
            @Override
            public void onGetDateTime(String date, String time) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                SharedPreferences.Editor preferences = sharedPreferences.edit();

                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put(KEY_MESSNAME, bundle.getString("messName"));
                userInfo.put(KEY_MESSNO, bundle.getString("messMobile"));
                userInfo.put(KEY_PLANNAME, "One day" + " Plan");
                userInfo.put(KEY_FROMDATE, date);
                userInfo.put(KEY_MESSTOKEN, token);

                preferences.putString("messName", bundle.getString("messName"));
                preferences.putString("MessNo", bundle.getString("messMobile"));
                preferences.putString("planName", "One day" + " Plan");
                preferences.putString("fromDate", date);
                preferences.putString("toDate", date);
                preferences.putString("token", token);

                userInfo.put(KEY_TODATE, sharedPreferences.getString("UserToken", ""));

                preferences.apply();

                db.collection("User").document(sharedPreferences.getString("UserEmail", ""))
                        .update(userInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Move to the home screen
                                Toast.makeText(DishInfo.this, "Plan added successfully", Toast.LENGTH_SHORT).show();
                                sendNotificationToMess();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showInstructionDialogBox("Payment failed", "If transition done by your bank, you will get money back within 48 hours.");
                            }
                        });

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                DatabaseReference dataRef = ref.child("mess").child(bundle.getString("messMobile")).child("OneDay" + "Plan").child("Users").child(sharedPreferences.getString("UserMobileNo", ""));

                Map<String, Object> data = new HashMap<>();
                data.put("name", sharedPreferences.getString("UserName", ""));
                data.put("email", sharedPreferences.getString("UserEmail", ""));
                data.put("plan", "One Day" + " Plan");
                data.put("mobileNo", sharedPreferences.getString("UserMobileNo", ""));
                data.put("forDate", date);
                data.put("token", sharedPreferences.getString("token", ""));


                dataRef.updateChildren(data)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error occurred while saving data
                                showInstructionDialogBox("Payment failed", "If transition done by your bank, you will get money back within 48 hours.");
                            }
                        });
            }
        });
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment failed "+s, Toast.LENGTH_SHORT).show();
        showInstructionDialogBox("Payment failed", "If transition done by your bank, you will get money back within 48 hours.");

    }

    private void sendNotificationToMess(){
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token,
                "📢New Customer Alert!",
                "Congratulations!🥳🥳🥳 \nYou've just acquired a new customer for OneDay Plan. Take a moment to celebrate this achievement and get ready to provide exceptional service.",
                getApplicationContext(),
                DishInfo.this);

        notificationsSender.SendNotifications();
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