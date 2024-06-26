package com.gn4k.dailybites.Mess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gn4k.dailybites.Animation.LoadingDialog;
import com.gn4k.dailybites.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MonthlyPlanEditor extends AppCompatActivity {

    private TextView TVplanName;
    private EditText evprice, evdescription;
    private ImageView planImage;
    private CardView back;
    private String isNonVegInclude = "no";
    private String isHomeDeliveryAvailable = "no";
    private String isBreakFastAvailable = "no";
    private String planName;
    private Button updateBTN;
    MaterialButtonToggleGroup toggleDelivery, toggleNonVeg, toggleBreakFast;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_plan_editor);

        loadingDialog = new LoadingDialog(MonthlyPlanEditor.this);
        loadingDialog.startLoading();

        TVplanName = findViewById(R.id.tv_planName);
        planImage = findViewById(R.id.planImage);
        back = findViewById(R.id.back);
        evprice = findViewById(R.id.price);
        evdescription = findViewById(R.id.description);
        updateBTN = findViewById(R.id.update);
        toggleNonVeg = findViewById(R.id.isNonVegBTN);
        toggleDelivery = findViewById(R.id.isHDBTN);
        toggleBreakFast = findViewById(R.id.isBrkFastBTN);

        toggleNonVeg.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                // Handle the button checked event
                if (isChecked) {
                    // Button is checked
                    switch (checkedId) {
                        case R.id.btn_yesNonveg:
                            // YES button is checked
                            isNonVegInclude = "yes";
//                            Toast.makeText(MonthlyPlanEditor.this, isNonVegInclude, Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.btn_noNonveg:
                            // NO button is checked
                            isNonVegInclude = "no";
//                            Toast.makeText(MonthlyPlanEditor.this, isNonVegInclude, Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
//                    Toast.makeText(MonthlyPlanEditor.this, "Please select one option in Non-Veg", Toast.LENGTH_SHORT).show();
                }
            }
        });

        toggleBreakFast.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                // Handle the button checked event
                if (isChecked) {
                    // Button is checked
                    switch (checkedId) {
                        case R.id.btn_yesbrkFast:
                            // YES button is checked
                            isBreakFastAvailable = "yes";

                            break;
                        case R.id.btn_nobrkFast:
                            // NO button is checked
                            isBreakFastAvailable = "no";

                            break;
                    }
                } else {
//                    Toast.makeText(MonthlyPlanEditor.this, "Please select one option in Non-Veg", Toast.LENGTH_SHORT).show();
                }
            }
        });


        toggleDelivery.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                // Handle the button checked event
                if (isChecked) {
                    // Button is checked
                    switch (checkedId) {
                        case R.id.btn_yesHD:
                            // YES button is checked
                            isHomeDeliveryAvailable = "yes";
//                            Toast.makeText(MonthlyPlanEditor.this, isNonVegInclude, Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.btn_noHD:
                            // NO button is checked
                            isHomeDeliveryAvailable = "no";
//                            Toast.makeText(MonthlyPlanEditor.this, isNonVegInclude, Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
//                    Toast.makeText(MonthlyPlanEditor.this, "Please select one option in Non-Veg", Toast.LENGTH_SHORT).show();
                }
            }
        });


        updateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAllDataToUpdateInFirebase();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Get the string from home screen to choose which plan theme should apply to this activity
        Bundle bundle = getIntent().getExtras();
        planName = bundle.getString("planName");
        setAllThingsAccordingToPlan(planName);

    }

    private void sendAllDataToUpdateInFirebase(){
        int price = Integer.parseInt(evprice.getText().toString());
        String description = evdescription.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("MessOwnerData",MODE_PRIVATE);
        // Update user information in Firebase database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dataRef = ref.child("mess").
                child(sharedPreferences.getString("MessOwnerMobileNo", "")).
                child(planName+"plan");

        Map<String, Object> data = new HashMap<>();
        data.put("price", price);
        data.put("isNonVegInclude", isNonVegInclude);
        data.put("isHomeDeliveryAvailable", isHomeDeliveryAvailable);
        data.put("isBreakFastAvailable", isBreakFastAvailable);
        data.put("description", description);

        dataRef.updateChildren(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Move to the home screen
                        Intent intent = new Intent(MonthlyPlanEditor.this, HomeForMessOwner.class);
                        startActivity(intent);
                        finish();
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

    private void setAllThingsAccordingToPlan(String planName) {


        TVplanName.setText(planName + " Plan");

        if(planName.equals("Diamond")){

            TVplanName.setTextColor(Color.parseColor("#9747FF"));
            Drawable drawable  = getResources().getDrawable(R.drawable.diamond);
            planImage.setImageDrawable(drawable);


        }
        else if (planName.equals("Gold")) {

            TVplanName.setTextColor(Color.parseColor("#EBBF5C"));
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

        SharedPreferences sharedPreferences = getSharedPreferences("MessOwnerData",MODE_PRIVATE);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbpath = db.child("mess")
                .child(sharedPreferences.getString("MessOwnerMobileNo", ""))
                .child(planName+"plan");

        dbpath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();
                    evdescription.setText((String) data.get("description"));
                    evprice.setText(String.valueOf(data.get("price")));
                    isNonVegInclude=(String) data.get("isNonVegInclude");
                    isHomeDeliveryAvailable=(String) data.get("isHomeDeliveryAvailable");
                    isBreakFastAvailable = (String) data.get("isBreakFastAvailable");

                    if(isNonVegInclude.equals("yes")){
                        toggleNonVeg.check(R.id.btn_yesNonveg);
                    }else {
                        toggleNonVeg.check(R.id.btn_noNonveg);
                    }

                    if(isHomeDeliveryAvailable.equals("yes")){
                        toggleDelivery.check(R.id.btn_yesHD);
                    }else {
                        toggleDelivery.check(R.id.btn_noHD);
                    }

                    if(isBreakFastAvailable.equals("yes")){
                        toggleBreakFast.check(R.id.btn_yesbrkFast);
                    }else {
                        toggleBreakFast.check(R.id.btn_nobrkFast);
                    }
                    loadingDialog.stopLoading();

                    updateBTN.setText("Update");
                } else {
                    loadingDialog.stopLoading();

                    updateBTN.setText("Upload");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }
}