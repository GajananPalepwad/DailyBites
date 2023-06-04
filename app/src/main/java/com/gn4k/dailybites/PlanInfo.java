package com.gn4k.dailybites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class PlanInfo extends AppCompatActivity {

    private TextView TVplanName,evprice,evdescription, TVveg, TVdelivery, TVbreakfast;
    private ImageView planImage, isVeg;
    private CardView back;


    private String isNonVegInclude = "no";
    private String isHomeDeliveryAvailable = "no";
    private String planName, mobileNo;
    private Button subscribe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_info);

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


        setAllThingsAccordingToPlan();

    }

    private void setAllThingsAccordingToPlan() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            planName = bundle.getString("plan");
            mobileNo = bundle.getString("mobileNo");
        }
        TVplanName.setText(planName + " Plan");

        if(planName.equals("Diamond")){

            TVplanName.setTextColor(Color.parseColor("#9747FF"));
            Drawable drawable  = getResources().getDrawable(R.drawable.diamond);
            planImage.setImageDrawable(drawable);


        }
        else if (planName.equals("Gold")) {

            TVplanName.setTextColor(Color.parseColor("#FDD500"));
            Drawable drawable  = getResources().getDrawable(R.drawable.gold);
            planImage.setImageDrawable(drawable);
        }
        else if (planName.equals("Silver")) {

            TVplanName.setTextColor(Color.parseColor("#B5B5B5"));
            Drawable drawable  = getResources().getDrawable(R.drawable.silver);
            planImage.setImageDrawable(drawable);
        }
        updateAccordingtofirebase();
    }

    private void updateAccordingtofirebase(){

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbpath = db.child("mess")
                .child(mobileNo)
                .child(planName+"plan");

        dbpath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();
                    evdescription.setText((String) data.get("description"));
                    evprice.setText("₹"+String.valueOf(data.get("price")));
                    if(((String) data.get("isNonVegInclude")).equals("yes")){
                        TVveg.setText("Non-Veg");
                        isVeg.setImageResource(R.drawable.nonveg);
                    }
                    if(!((String) data.get("isHomeDeliveryAvailable")).equals("yes")){
                        TVdelivery.setText("No Home Delivery");
                    }
                } else {
                    Toast.makeText(PlanInfo.this, "Something went wrong!!!!", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


}