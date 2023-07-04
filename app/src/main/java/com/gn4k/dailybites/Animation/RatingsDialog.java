package com.gn4k.dailybites.Animation;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.gn4k.dailybites.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.HashMap;

public class RatingsDialog {
    public RatingsDialog(Activity activity) {
        this.activity = activity;
    }

    private Activity activity;
    private Dialog dialog;

    public void showDialog(String messNo){

        dialog = new Dialog(activity, R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(R.layout.ratings_card);
        dialog.setCancelable(false);
        Button submit = dialog.findViewById(R.id.submit);

        RadioButton one = dialog.findViewById(R.id.one);
        RadioButton two = dialog.findViewById(R.id.two);
        RadioButton three = dialog.findViewById(R.id.three);
        RadioButton four = dialog.findViewById(R.id.four);
        RadioButton five = dialog.findViewById(R.id.five);



        submit.setOnClickListener(v -> {
            if(one.isChecked()){
                submitRatings(1.0, messNo);
            }else if(two.isChecked()){
                submitRatings(2.0, messNo);
            }else if(three.isChecked()){
                submitRatings(3.0, messNo);
            }else if(four.isChecked()){
                submitRatings(4.0, messNo);
            }else if(five.isChecked()){
                submitRatings(5.0, messNo);
            }else{
                Toast.makeText(activity, "Please give ratings", Toast.LENGTH_SHORT).show();
            }
        });


        dialog.show();

    }


    double newRatings;
    private void submitRatings(double userRating, String messNo) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbpath = db.child("mess").child(messNo);
        dbpath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();
                    String oldRatings = (String) data.get("ratings");
                    assert oldRatings != null;
                    double oldR = Double.parseDouble(oldRatings);
                    newRatings = (oldR+userRating)/2;
                    dbpath.child("ratings").setValue(new DecimalFormat("#.#").format(newRatings));
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}

        });


    }

}
