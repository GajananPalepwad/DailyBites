package com.gn4k.dailybites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gn4k.dailybites.Animation.LoadingDialog;
import com.gn4k.dailybites.SendNotificationClasses.FcmNotificationsSender;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PlanInfo extends AppCompatActivity implements PaymentResultListener {

    private static final String KEY_MESSNAME = "messName";
    private static final String KEY_MESSNO = "messNo";
    private static final String KEY_PLANNAME = "planName";
    private static final String KEY_FROMDATE = "from";
    private static final String KEY_MESSTOKEN = "messToken";
    private static final String KEY_FREEDISH = "freeDish";
    private static final String KEY_TODATE = "to";
    private TextView TVplanName, evprice, evdescription, TVveg, TVdelivery, TVbreakfast;
    private ImageView planImage, isVeg;
    private CardView back;
    private String planName, mobileNo, messName, token;
    private int planPrize;
    private Button subscribe;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_info);

        loadingDialog = new LoadingDialog(PlanInfo.this);
        loadingDialog.startLoading();

        TVplanName = findViewById(R.id.tv_planName);
        planImage = findViewById(R.id.planImage);
        isVeg = findViewById(R.id.isVeg);
        back = findViewById(R.id.back);
        evprice = findViewById(R.id.rate);
        evdescription = findViewById(R.id.description);
        subscribe = findViewById(R.id.subscribe);
        TVveg = findViewById(R.id.isveg);
        TVbreakfast = findViewById(R.id.breakfast);
        TVdelivery = findViewById(R.id.isdelivery);

        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);

                if(sharedPreferences.getString("planName", "").equals("")) {
                    startPayment();
                }else {
                    showInstructionDialogBox("Plan Exist", "You already have an another plan....");
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        setAllThingsAccordingToPlan();

    }

    private void setAllThingsAccordingToPlan() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            planName = bundle.getString("plan");
            mobileNo = bundle.getString("mobileNo");
            messName = bundle.getString("messName");
            token = bundle.getString("token");
        }
        TVplanName.setText(planName + " Plan");

        if (planName.equals("Diamond")) {

            TVplanName.setTextColor(Color.parseColor("#9747FF"));
            Drawable drawable = getResources().getDrawable(R.drawable.diamond);
            planImage.setImageDrawable(drawable);


        } else if (planName.equals("Gold")) {

            TVplanName.setTextColor(Color.parseColor("#FDD500"));
            Drawable drawable = getResources().getDrawable(R.drawable.gold);
            planImage.setImageDrawable(drawable);
        } else if (planName.equals("Silver")) {

            TVplanName.setTextColor(Color.parseColor("#B5B5B5"));
            Drawable drawable = getResources().getDrawable(R.drawable.silver);
            planImage.setImageDrawable(drawable);
        }
        updateAccordingtofirebase();
    }

    private void updateAccordingtofirebase() {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbpath = db.child("mess")
                .child(mobileNo)
                .child(planName + "plan");

        dbpath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();
                    evdescription.setText((String) data.get("description"));
                    evprice.setText("₹" + String.valueOf(data.get("price")));
                    planPrize = Integer.parseInt(String.valueOf(data.get("price")))*100;
                    if (((String) data.get("isNonVegInclude")).equals("yes")) {
                        TVveg.setText("Non-Veg");
                        isVeg.setImageResource(R.drawable.nonveg);
                    }
                    if (!((String) data.get("isHomeDeliveryAvailable")).equals("yes")) {
                        TVdelivery.setText("No Home Delivery");
                    }
                    if (((String) data.get("isBreakFastAvailable")).equals("yes")) {
                        TVbreakfast.setText("Breakfast Available");
                    }
                } else {
                    Toast.makeText(PlanInfo.this, "Something went wrong!!!!", Toast.LENGTH_SHORT).show();
                }
                loadingDialog.stopLoading();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
            options.put("description", "For "+planName+" Plan of "+messName);
            options.put("send_sms_hash", true);
            options.put("allow_rotation", false);
            options.put("currency", "INR");
            options.put("amount", planPrize);

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
                userInfo.put(KEY_MESSNAME, messName);
                userInfo.put(KEY_MESSNO, mobileNo);
                userInfo.put(KEY_PLANNAME, planName + " Plan");
                userInfo.put(KEY_FROMDATE, date);
                userInfo.put(KEY_MESSTOKEN, token);
                userInfo.put(KEY_FREEDISH, "1");

                preferences.putString("messName", messName);
                preferences.putString("MessNo", mobileNo);
                preferences.putString("planName", planName + " Plan");
                preferences.putString("fromDate", date);
                preferences.putString("freeDish", "1");
                preferences.putString("isRating", "1");
                preferences.putString("messToken", token);

                String nextMonthDateString = getNextMonthDate(date);

                userInfo.put(KEY_TODATE, nextMonthDateString);
                preferences.putString("toDate", nextMonthDateString);
                preferences.apply();

                db.collection("User").document(sharedPreferences.getString("UserEmail", ""))
                        .update(userInfo)
                        .addOnSuccessListener(unused -> {
                            // Move to the home screen
                            Toast.makeText(PlanInfo.this, "Plan added successfully", Toast.LENGTH_SHORT).show();
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
                data.put("orderId", s);
                data.put("latitude", sharedPreferences.getString("UserLatitude", ""));
                data.put("longitude", sharedPreferences.getString("UserLongitude", ""));
                data.put("address", sharedPreferences.getString("UserAddress", ""));
                data.put("mobileNo", sharedPreferences.getString("UserMobileNo", ""));
                data.put("toDate", nextMonthDateString);
                data.put("token", sharedPreferences.getString("UserToken", ""));

                dataRef.updateChildren(data)
                        .addOnSuccessListener(aVoid -> {
                            // Data saved successfully
                            sendNotificationToMess();
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
        showInstructionDialogBox("Payment failed", "If transition done by your bank, you will get money back within 48 hours.");

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

    private void sendNotificationToMess(){
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token,
                "📢New Customer Alert!",
                "Congratulations!🥳🥳🥳 \nYou've just acquired a new customer for "+planName + " Plan"+". Take a moment to celebrate this achievement and get ready to provide exceptional service.",
                getApplicationContext(),
                PlanInfo.this);

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