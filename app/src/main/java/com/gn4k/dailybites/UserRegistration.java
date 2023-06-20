package com.gn4k.dailybites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gn4k.dailybites.Mess.SendNotificationToUser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class UserRegistration extends AppCompatActivity {

    private static final String KEY_NAME = "name";
    private static final String KEY_MOBILE_NO = "mobile no";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TOKEN = "token";


    private EditText name, mobileNo, email, password, ConfirmPassword;
    private TextView passwordDoNotMatch;
    private CheckBox checkBox;
    private Button BTNregistor;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        name = findViewById(R.id.name);
        mobileNo = findViewById(R.id.phoneNo);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        ConfirmPassword = findViewById(R.id.passwordConfi);
        passwordDoNotMatch = findViewById(R.id.passwarning);
        checkBox = findViewById(R.id.checkBox);
        BTNregistor = findViewById(R.id.registration);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);




        // Check if a Google account is available
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
                getToken();
                getRegister();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        signOut();
    }

    private void getRegister(){

            String getEmail = email.getText().toString();
            DocumentReference emailRef = db.collection("User").document(getEmail);
            emailRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (!documentSnapshot.exists()) {
                        checkAllParametersToRegistration();
                    } else {
                        Toast.makeText(UserRegistration.this, "Account already exists.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void checkAllParametersToRegistration(){ //method to save data in database after checking of all parameters

        String getName = name.getText().toString();
        String getEmail = email.getText().toString();
        String getMobileNo = mobileNo.getText().toString();
        String getPassword = password.getText().toString();
        String getConfirmPassword = ConfirmPassword.getText().toString();


        if(checkBox.isChecked()){

            if(!getName.isEmpty() && !getEmail.isEmpty() && !getMobileNo.isEmpty() && !getPassword.isEmpty() && !getConfirmPassword.isEmpty()){

                if(getMobileNo.length()==10){

                    if(getPassword.equals(getConfirmPassword)){

                        if (getPassword.length() >= 8 && isPasswordValid(getPassword)) {

                            Map<String, Object> userInfo = new HashMap<>();

                            userInfo.put(KEY_NAME, getName);
                            userInfo.put(KEY_EMAIL, getEmail);
                            userInfo.put(KEY_MOBILE_NO, getMobileNo);
                            userInfo.put(KEY_PASSWORD, getPassword);
                            userInfo.put(KEY_TOKEN, tokenString);


                            //Saving the data to SharedPreference so we will not get data from Firestore.
                            SharedPreferences sharedPreferences = getSharedPreferences("UserData",MODE_PRIVATE);
                            SharedPreferences.Editor preferences = sharedPreferences.edit();

                            preferences.putString("UserEmail",getEmail);
                            preferences.putString("UserPassword",getPassword);
                            preferences.putString("UserMobileNo",getMobileNo);
                            preferences.putString("UserName",getName);
                            preferences.putString("UserToken", tokenString);
                            preferences.apply();

                            db.collection("User").document(getEmail).set(userInfo).

                                    addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            Toast.makeText(UserRegistration.this, "REGISTRATION SUCCESSFUL", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(UserRegistration.this,MapActivityToChooseLocation.class);
                                            startActivity(intent);
                                            finish();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(UserRegistration.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                                        }
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

    String tokenString="";
    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    tokenString = task.getResult();
                } else {
                    // Handle the case when token retrieval fails
                    Toast.makeText(UserRegistration.this, "Failed to retrieve token",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
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
