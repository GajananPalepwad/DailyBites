package com.gn4k.dailybites.User;

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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.firestore.DocumentReference;
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
    private static final String KEY_PLAN_NAME = "planName";
    private static final String KEY_TO = "to";
    private static final String KEY_FROM = "from";
    private static final String KEY_MESS_NO = "messNo";
    private static final String KEY_MESS_NAME = "messName";
    private static final String KEY_FREEDISH = "freeDish";
    private static final String KEY_RATING = "isRating";
    private static final String KEY_ONE_DAY_MESSNAME = "OneDayMessName";
    private static final String KEY_ONE_DAY_MESSNO = "OneDayMessNo";
    private static final String KEY_ONE_DAY_MESSTOKEN = "OneDayToken";
    private static final String KEY_MESSTOKEN = "messToken";
    private static final String KEY_ONE_DAY_MESS_ORDER_ID= "OneDayOrderId";
    private static final String KEY_WALLET_BALANCE = "WalletBalance";//0
    private static final String KEY_PENDING_BALANCE = "PendingDeposit";//0




    private EditText name, mobileNo, email, password, ConfirmPassword;
    private TextView passwordDoNotMatch, tos;
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
        tos = findViewById(R.id.tos);

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

        tos.setOnClickListener(v -> {
            Intent intent = new Intent(UserRegistration.this, TermAndConditions.class);
            startActivity(intent);
        });

        BTNregistor.setOnClickListener(v -> {
            getToken();
            getRegister();
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
            emailRef.get().addOnSuccessListener(documentSnapshot -> {
                if (!documentSnapshot.exists()) {
                    checkAllParametersToRegistration();
                } else {
                    Toast.makeText(UserRegistration.this, "Account already exists.", Toast.LENGTH_SHORT).show();
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
                            userInfo.put(KEY_MESS_NAME, "");
                            userInfo.put(KEY_FREEDISH, "0");
                            userInfo.put(KEY_RATING, "0");
                            userInfo.put(KEY_MESS_NO, "");
                            userInfo.put(KEY_PLAN_NAME, "");
                            userInfo.put(KEY_FROM, "");
                            userInfo.put(KEY_ONE_DAY_MESSNAME, "");
                            userInfo.put(KEY_ONE_DAY_MESSNO, "");
                            userInfo.put(KEY_ONE_DAY_MESSTOKEN, "");
                            userInfo.put(KEY_TO, "");
                            userInfo.put(KEY_MESSTOKEN, "");
                            userInfo.put(KEY_ONE_DAY_MESS_ORDER_ID, "");
                            userInfo.put(KEY_WALLET_BALANCE, "0");
                            userInfo.put(KEY_PENDING_BALANCE, "0");



                            //Saving the data to SharedPreference so we will not get data from Firestore.
                            SharedPreferences sharedPreferences = getSharedPreferences("UserData",MODE_PRIVATE);
                            SharedPreferences.Editor preferences = sharedPreferences.edit();

                            preferences.putString("UserEmail",getEmail);
                            preferences.putString("UserPassword",getPassword);
                            preferences.putString("UserMobileNo",getMobileNo);
                            preferences.putString("UserName",getName);
                            preferences.putString("UserToken", tokenString);
                            preferences.putString("messName", "");
                            preferences.putString("MessNo", "");
                            preferences.putString("planName", "");
                            preferences.putString("toDate", "");
                            preferences.putString("OneDayMessName","");
                            preferences.putString("OneDayMessNo", "");
                            preferences.putString("OneDayForDate", "");
                            preferences.putString("OneDayToken", "");
                            preferences.apply();

                            db.collection("User").document(getEmail).set(userInfo).

                                    addOnSuccessListener(unused -> {

                                        Toast.makeText(UserRegistration.this, "REGISTRATION SUCCESSFUL", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(UserRegistration.this, MapActivityToChooseLocation.class);
                                        startActivity(intent);
                                        finish();

                                    }).addOnFailureListener(e -> Toast.makeText(UserRegistration.this, "Something went wrong", Toast.LENGTH_SHORT).show());
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
                Toast.makeText(UserRegistration.this, "Failed to retrieve token",
                        Toast.LENGTH_LONG).show();
            }
        });
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
