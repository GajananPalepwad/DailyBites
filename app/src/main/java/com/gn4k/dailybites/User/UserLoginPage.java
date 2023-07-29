package com.gn4k.dailybites.User;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gn4k.dailybites.Animation.LoadingDialog;
import com.gn4k.dailybites.Home;
import com.gn4k.dailybites.LanguageChooser;
import com.gn4k.dailybites.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UserLoginPage extends AppCompatActivity {


    private static final String KEY_NAME = "name";
    private static final String KEY_MOBILE_NO = "mobile no";
    private static final String KEY_MESSNAME = "messName";
    private static final String KEY_MESSNO = "messNo";
    private static final String KEY_PLANNAME = "planName";
    private static final String KEY_TODATE = "to";
    private static final String KEY_FROMDATE = "from";
    private static final String KEY_FREEDISH = "freeDish";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_MESS_TOKEN = "messToken";
    private static final String KEY_ONE_DAY_MESSNAME = "OneDayMessName";
    private static final String KEY_ONE_DAY_MESSNO = "OneDayMessNo";
    private static final String KEY_RATING = "isRating";
    private static final String KEY_ONE_DAY_MESS_TOKEN = "OneDayToken";
    private static final String KEY_ONE_DAY_ORDER_ID = "OneDayOrderId";


    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button login;
    private TextView pass;
    private TextView email;
    private GoogleSignInClient client;
    LatLng latLng ;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login_page);
        loadingDialog = new LoadingDialog(this);

        login = findViewById(R.id.login);
        pass = findViewById(R.id.password);
        email = findViewById(R.id.email);
        TextView registration = findViewById(R.id.reg);



        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        client = GoogleSignIn.getClient(this, options);


        login.setOnClickListener(v -> {
            loadingDialog.startLoading();
            getToken();
            checkValidationToLogin();

        });

        registration.setOnClickListener(v -> {
            loadingDialog.startLoading();
            getToken();
            setRegistration(v);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UserLoginPage.this, LanguageChooser.class);
        startActivity(intent);
        finish();
    }

    public void setRegistration(View view){

        Intent i = client.getSignInIntent();
        startActivityForResult(i, 1234);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1234) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            loadingDialog.stopLoading();
            loadingDialog.startLoading();
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(task1 -> {

                            if (task1.isSuccessful()) {
                                loadingDialog.stopLoading();
                                Intent intent = new Intent(getApplicationContext(), UserRegistration.class);
                                startActivity(intent);

                            } else {
                                loadingDialog.stopLoading();
                                Toast.makeText(UserLoginPage.this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    }



    String getEmail="";
    String getPassword="";
    private void checkValidationToLogin(){


        if(!email.getText().toString().equals("")) {
            getEmail = email.getText().toString();
            getPassword = pass.getText().toString();

            DocumentReference emailRef = db.collection("User").document(getEmail);
            emailRef.get().addOnSuccessListener(documentSnapshot -> {
                if(documentSnapshot.exists()){

                    if(documentSnapshot.getString(KEY_PASSWORD).equals(getPassword)){

                        SharedPreferences sharedPreferences = getSharedPreferences("UserData",MODE_PRIVATE);
                        SharedPreferences.Editor preferences = sharedPreferences.edit();

                        preferences.putString("UserEmail", getEmail);
                        preferences.putString("UserPassword", getPassword);
                        preferences.putString("UserMobileNo", documentSnapshot.getString(KEY_MOBILE_NO)+"");
                        preferences.putString("UserName", documentSnapshot.getString(KEY_NAME));
                        preferences.putString("messName", documentSnapshot.getString(KEY_MESSNAME));
                        preferences.putString("MessNo", documentSnapshot.getString(KEY_MESSNO));
                        preferences.putString("planName", documentSnapshot.getString(KEY_PLANNAME));
                        preferences.putString("toDate", documentSnapshot.getString(KEY_TODATE));
                        preferences.putString("fromDate", documentSnapshot.getString(KEY_FROMDATE));
                        preferences.putString("freeDish", documentSnapshot.getString(KEY_FREEDISH));
                        preferences.putString("messToken", documentSnapshot.getString(KEY_MESS_TOKEN));
                        preferences.putString("OneDayMessName", documentSnapshot.getString(KEY_ONE_DAY_MESSNAME));
                        preferences.putString("OneDayMessNo", documentSnapshot.getString(KEY_ONE_DAY_MESSNO));
                        preferences.putString("OneDayToken", documentSnapshot.getString(KEY_ONE_DAY_MESS_TOKEN));
                        preferences.putString("OneDayOrderId", documentSnapshot.getString(KEY_ONE_DAY_ORDER_ID));
                        preferences.putString("isRating", documentSnapshot.getString(KEY_RATING));
                        preferences.putString("UserToken", tokenString);
                        preferences.apply();

                        if(documentSnapshot.getDouble(KEY_LATITUDE)== null ){
                            loadingDialog.stopLoading();
                            Intent intent = new Intent(UserLoginPage.this, MapActivityToChooseLocation.class);
                            startActivity(intent);
                            return;
                        }
                        else{
                            if(documentSnapshot.getDouble(KEY_LATITUDE)== 0 ){
                                loadingDialog.stopLoading();
                                Intent intent = new Intent(UserLoginPage.this, MapActivityToChooseLocation.class);
                                startActivity(intent);
                                return;
                            }
                        }
                        latLng = new LatLng(documentSnapshot.getDouble(KEY_LATITUDE), documentSnapshot.getDouble(KEY_LONGITUDE));

                        preferences.putString("UserLatitude",documentSnapshot.getDouble(KEY_LATITUDE)+"");
                        preferences.putString("UserLongitude",documentSnapshot.getDouble(KEY_LONGITUDE)+"");
                        preferences.putString("UserAddress",getAddressFromLatLng(latLng));
                        preferences.apply();

                        String plan = "";
                        String documentPlanName = documentSnapshot.getString(KEY_PLANNAME);
                        if (!documentPlanName.equals("")) {
                            if (documentPlanName.equals("Gold Plan")) {
                                plan = "Goldplan";
                            } else if (documentPlanName.equals("Silver Plan")) {
                                plan = "Silverplan";
                            } else if (documentPlanName.equals("Diamond Plan")) {
                                plan = "Diamondplan";
                            }
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference dataRef = ref.child("mess").
                                    child(documentSnapshot.getString(KEY_MESSNO)).
                                    child(plan).
                                    child("Users").
                                    child(sharedPreferences.getString("UserMobileNo", ""));

                            Map<String, Object> data = new HashMap<>();
                            data.put("token", tokenString);
                            dataRef.updateChildren(data)
                                    .addOnFailureListener(e -> {
                                        // Error occurred while saving data
                                        Toast.makeText(UserLoginPage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    });
                        }


                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put(KEY_TOKEN, tokenString);
                        db.collection("User").document(sharedPreferences.getString("UserEmail", "")).update(userInfo).
                                addOnSuccessListener(unused -> {
                                    // Move to the home screen
                                    loadingDialog.stopLoading();
                                    Intent intent = new Intent(UserLoginPage.this, Home.class);
                                    startActivity(intent);
                                    finish();
                                }).addOnFailureListener(e -> Toast.makeText(UserLoginPage.this, "Something went wrong", Toast.LENGTH_SHORT).show());

                    }else{
                        loadingDialog.stopLoading();
                        Toast.makeText(UserLoginPage.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    loadingDialog.stopLoading();
                    Toast.makeText(UserLoginPage.this, "Account not found", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            loadingDialog.stopLoading();
            Toast.makeText(this, "Please provide credentials", Toast.LENGTH_SHORT).show();
        }

    }

    private String getAddressFromLatLng(LatLng latLng) {
        String addressReturn = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String fullAddress = address.getAddressLine(0); // Get the full address including street, city, etc.
                addressReturn = fullAddress;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressReturn;
    }

    String tokenString="";
    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                tokenString = task.getResult();
            } else {
                // Handle the case when token retrieval fails
                Toast.makeText(UserLoginPage.this, "Failed to retrieve token", Toast.LENGTH_LONG).show();
            }
        });
    }


}