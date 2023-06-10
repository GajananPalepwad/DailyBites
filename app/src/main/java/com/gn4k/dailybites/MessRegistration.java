package com.gn4k.dailybites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MessRegistration extends AppCompatActivity {

    private EditText name, mobileNo, email,messName, password, ConfirmPassword;
    private TextView passwordDoNotMatch;
    private CheckBox checkBox;
    private Button BTNregistor;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String personEmail = account.getEmail();
            email.setText(personEmail);
            email.setFocusable(false);
            email.setClickable(false);
        }

        BTNregistor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRegister();
            }
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
                        preferences.apply();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference dataRef = ref.child("mess").child(getMobileNo);


                        Map<String, Object> data = new HashMap<>();
                        data.put("ownerName", getName);
                        data.put("email", getEmail);
                        data.put("messName", getMessName);
                        data.put("mobileNo", getMobileNo);
                        data.put("password", getPassword);
                        data.put("isVerified","");

                        dataRef.setValue(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Data saved successfully
                                        Toast.makeText(MessRegistration.this, "REGISTRATION SUCCESSFUL", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MessRegistration.this,MapToLocateMess.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Error occurred while saving data
                                        Toast.makeText(MessRegistration.this, "Something went wrong", Toast.LENGTH_SHORT).show();                                    }
                                });

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
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
            }
        });
    }
}