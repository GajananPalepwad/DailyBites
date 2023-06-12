package com.gn4k.dailybites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DishInfo extends AppCompatActivity {
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_info);
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

//
        TextView priceW = findViewById(R.id.Tv_prizeW);
        if (bundle.getString("messIsDelivery").equals("no")){
            priceW.setText("Not Available");
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
                    myRatingBar.setRating(Float. parseFloat((String) data.get("ratings")) );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
            options.put("amount", 2000);

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
        Toast.makeText(this, "Payment successful " + s, Toast.LENGTH_SHORT).show();

        GetDateTime getDateTime = new GetDateTime(this);
        getDateTime.getDateTime(new GetDateTime.VolleyCallBack() {
            @Override
            public void onGetDateTime(String date, String time) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                SharedPreferences.Editor preferences = sharedPreferences.edit();

                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put(KEY_MESSNAME, messName);
                userInfo.put(KEY_MESSNO, mobileNo);
                userInfo.put(KEY_PLANNAME, planName + " Plan");
                userInfo.put(KEY_FROMDATE, date);

                preferences.putString("messName", messName);
                preferences.putString("MessNo", mobileNo);
                preferences.putString("planName", planName + " Plan");
                preferences.putString("fromDate", date);

                String nextMonthDateString = getNextMonthDate(date);

                userInfo.put(KEY_TODATE, nextMonthDateString);
                preferences.putString("toDate", nextMonthDateString);
                preferences.apply();

                db.collection("User").document(sharedPreferences.getString("UserEmail", ""))
                        .update(userInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Move to the home screen
                                Toast.makeText(PlanInfo.this, "Plan added successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showInstructionDialogBox("Payment failed", "If transition done by your bank, you will get money back within 48 hours.");
                            }
                        });

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                DatabaseReference dataRef = ref.child("mess").child(mobileNo).child(planName + "plan").child("Users").child(sharedPreferences.getString("UserMobileNo", ""));

                Map<String, Object> data = new HashMap<>();
                data.put("name", sharedPreferences.getString("UserName", ""));
                data.put("email", sharedPreferences.getString("UserEmail", ""));
                data.put("plan", planName + " Plan");
                data.put("fromDate", date);
                data.put("toDate", nextMonthDateString);


                dataRef.updateChildren(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Data saved successfully
                                // Toast.makeText(PlanInfo.this, "", Toast.LENGTH_SHORT).show();
                            }
                        })
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
//        Toast.makeText(this, "Payment failed "+s, Toast.LENGTH_SHORT).show();
//        showInstructionDialogBox("Payment failed", "If transition done by your bank, you will get money back within 48 hours.");

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