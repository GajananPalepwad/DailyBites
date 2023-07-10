package com.gn4k.dailybites.Mess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gn4k.dailybites.R;
import com.gn4k.dailybites.TermAndConditions;
import com.gn4k.dailybites.User.UserRegistration;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class MessRegistration extends AppCompatActivity {

    private EditText name, mobileNo, email,messName, password, ConfirmPassword;
    private TextView passwordDoNotMatch, tos;
    private CheckBox checkBox;
    private Button BTNregistor;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_registration);

        name = findViewById(R.id.name);
        mobileNo = findViewById(R.id.phoneNo);
        email = findViewById(R.id.email);
        messName = findViewById(R.id.MessName);
        password = findViewById(R.id.password);
        ConfirmPassword = findViewById(R.id.passwordConfi);
        passwordDoNotMatch = findViewById(R.id.passwarning);
        checkBox = findViewById(R.id.checkBox);
        BTNregistor = findViewById(R.id.registration);
        tos = findViewById(R.id.tos);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String personEmail = account.getEmail();
            email.setText(personEmail);
            email.setFocusable(false);
            email.setClickable(false);
        }

        BTNregistor.setOnClickListener(v -> {
            getToken();
            getRegister();
        });

        tos.setOnClickListener(v -> {
            Intent intent = new Intent(MessRegistration.this, TermAndConditions.class);
            startActivity(intent);
        });


    }

    private void getRegister() {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbpath = db.child("mess").child(mobileNo.getText().toString());

        dbpath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    checkAllParametersToRegistration();
                } else {
                    Toast.makeText(MessRegistration.this, "Account already exist", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkAllParametersToRegistration() {

        String getName = name.getText().toString();
        String getEmail = email.getText().toString();
        String getMessName = messName.getText().toString();
        String getMobileNo = mobileNo.getText().toString();
        String getPassword = password.getText().toString();
        String getConfirmPassword = ConfirmPassword.getText().toString();


        if(checkBox.isChecked()){

            if(!getName.isEmpty() && !getEmail.isEmpty() && !getMobileNo.isEmpty() && !getPassword.isEmpty() && !getConfirmPassword.isEmpty()){

                if(getMobileNo.length()==10){

                    if(getPassword.equals(getConfirmPassword)){

                        if (getPassword.length() >= 8 && isPasswordValid(getPassword)) {

                        //Saving the data to SharedPreference so we will not need to get data from Firestore.
                        SharedPreferences sharedPreferences = getSharedPreferences("MessOwnerData",MODE_PRIVATE);
                        SharedPreferences.Editor preferences = sharedPreferences.edit();

                        preferences.putString("MessOwnerEmail",getEmail);
                        preferences.putString("MessName",getMessName);
                        preferences.putString("MessOwnerPassword",getPassword);
                        preferences.putString("MessOwnerMobileNo",getMobileNo);
                        preferences.putString("MessOwnerName",getName);
                        preferences.putString("MessToken",tokenString);
                        preferences.apply();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference dataRef = ref.child("mess").child(getMobileNo);


                        Map<String, Object> data = new HashMap<>();
                        data.put("ownerName", getName);
                        data.put("email", getEmail);
                        data.put("messName", getMessName);
                        data.put("mobileNo", getMobileNo);
                        data.put("password", getPassword);
                        data.put("token", tokenString);
                        data.put("freeDish", "10");
                        data.put("wallet", "0.0");
                        data.put("withdrawPending", "0.0");
                        data.put("totalWithdraw", "0.0");
                        data.put("ratings", "0.0");
                        data.put("menu", "");
                        data.put("isDelivery", "");
                        data.put("ifsc", "");
                        data.put("dishPrize", "0");
                        data.put("coverImage", "");
                        data.put("bankName", "");
                        data.put("bankHolderName", "");
                        data.put("addharNo", "");
                        data.put("accountNo", "");
                        data.put("isVerified","no");

                        dataRef.setValue(data)
                                .addOnSuccessListener(aVoid -> {
                                    // Data saved successfully
                                    Toast.makeText(MessRegistration.this, "REGISTRATION SUCCESSFUL", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MessRegistration.this, MapToLocateMess.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    // Error occurred while saving data
                                    Toast.makeText(MessRegistration.this, "Something went wrong", Toast.LENGTH_SHORT).show();                                    });

                        } else {
                            passwordDoNotMatch.setText("Password must contain at least 8 characters, including 1 uppercase, 1 lowercase, 1 number, and 1 special character");
                        }
                    }else{
                        passwordDoNotMatch.setText("Passwords do not match.");
                    }
                }
                else{
                    Toast.makeText(this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(this, "Please fill the all field", Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(this, "Please agree to all the terms and conditions before Registration", Toast.LENGTH_SHORT).show();

        }


    }


    String tokenString="";
    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                tokenString = task.getResult();
            } else {
                // Handle the case when token retrieval fails
                Toast.makeText(MessRegistration.this, "Failed to retrieve token",
                        Toast.LENGTH_LONG).show();
            }
        });
    }




    @Override
    protected void onPause() {
        super.onPause();
        signOut();
    }

    private boolean isPasswordValid(String password) {
        // Password validation regex pattern
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(passwordPattern);
    }

    public void signOut(){
        gsc.signOut().addOnCompleteListener(task -> finish());
    }
}