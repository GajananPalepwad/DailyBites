package com.gn4k.dailybites.Animation;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class RatingsDialog {
    public RatingsDialog(Activity activity) {
        this.activity = activity;
    }

    private Activity activity;
    private static final String KEY_RATING = "isRating";
    private Dialog dialog;

    public void showDialog(String messNo, SharedPreferences sharedPreferences){

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
                submitRatings(1.0, messNo, sharedPreferences);
            }else if(two.isChecked()){
                submitRatings(2.0, messNo, sharedPreferences);
            }else if(three.isChecked()){
                submitRatings(3.0, messNo, sharedPreferences);
            }else if(four.isChecked()){
                submitRatings(4.0, messNo, sharedPreferences);
            }else if(five.isChecked()){
                submitRatings(5.0, messNo, sharedPreferences);
            }else{
                Toast.makeText(activity, "Please give ratings", Toast.LENGTH_SHORT).show();
            }
        });


        dialog.show();

    }


    double newRatings;
    private void submitRatings(double userRating, String messNo, SharedPreferences sharedPreferences) {

        SharedPreferences.Editor preferences = sharedPreferences.edit();

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
                    preferences.putString("isRating", "0");
                    preferences.apply();


                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference emailRef = db.collection("User").document(sharedPreferences.getString("UserEmail",""));
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put(KEY_RATING, "0");
                    emailRef.update(userInfo);

                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}

        });


    }

}
