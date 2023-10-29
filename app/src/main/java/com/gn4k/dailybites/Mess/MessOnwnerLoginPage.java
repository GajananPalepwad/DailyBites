package com.gn4k.dailybites.Mess;

import androidx.annotation.NonNull;
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
import com.gn4k.dailybites.LanguageChooser;
import com.gn4k.dailybites.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MessOnwnerLoginPage extends AppCompatActivity {

    private GoogleSignInClient client;
    private Button login;
    private TextView pass;
    private TextView mobile_No;
    LatLng latLng ;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_onwner_login_page);
        loadingDialog = new LoadingDialog(MessOnwnerLoginPage.this);
        TextView registration = findViewById(R.id.reg);
        login = findViewById(R.id.login);
        pass = findViewById(R.id.password);
        mobile_No = findViewById(R.id.mobileNo);



        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        client = GoogleSignIn.getClient(this, options);



        registration.setOnClickListener(v -> {
            loadingDialog.startLoading();
            setRegistration(v);
        });

        login.setOnClickListener(v -> {
            if(!mobile_No.getText().toString().isEmpty() && !pass.getText().toString().isEmpty()) {
                loadingDialog.startLoading();
                getToken();
                checkValidationToLogin();
            }else{
                Toast.makeText(MessOnwnerLoginPage.this, "Please enter mobile number and password", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MessOnwnerLoginPage.this, LanguageChooser.class);
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
                                Intent intent = new Intent(getApplicationContext(), MessRegistration.class);
                                startActivity(intent);

                            } else {
                                loadingDialog.stopLoading();
                                Toast.makeText(MessOnwnerLoginPage.this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    }

    private void checkValidationToLogin(){
        if (!pass.getText().toString().isEmpty()) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            DatabaseReference dbpath = db.child("mess").child(mobile_No.getText().toString());
            dbpath.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();
                        String firepass = (String) data.get("password");
                        if (pass.getText().toString().equals(firepass)) {

                            SharedPreferences sharedPreferences = getSharedPreferences("MessOwnerData", MODE_PRIVATE);
                            SharedPreferences.Editor preferences = sharedPreferences.edit();

                            preferences.putString("MessOwnerEmail", (String) data.get("email"));
                            preferences.putString("MessName", (String) data.get("messName"));
                            preferences.putString("MessOwnerPassword", pass.getText().toString());
                            preferences.putString("MessOwnerMobileNo", mobile_No.getText().toString());
                            preferences.putString("MessOwnerName", (String) data.get("ownerName"));
                            preferences.putString("MessToken", tokenString);
                            preferences.apply();
                            if (!snapshot.hasChild("latitude")) {
                                loadingDialog.stopLoading();
                                Intent intent = new Intent(MessOnwnerLoginPage.this, MapToLocateMess.class);
                                startActivity(intent);
                                return;
                            } else {
                                if (String.valueOf(data.get("latitude")).equals("0")) {
                                    loadingDialog.stopLoading();
                                    Intent intent = new Intent(MessOnwnerLoginPage.this, MapToLocateMess.class);
                                    startActivity(intent);
                                    return;
                                }
                            }
                            latLng = new LatLng((double) data.get("latitude"), (double) data.get("longitude"));
                            preferences.putString("MessOwnerLatitude", String.valueOf(data.get("latitude")));
                            preferences.putString("MessOwnerLongitude", String.valueOf(data.get("longitude")));
                            preferences.putString("MessOwnerAddress", getAddressFromLatLng(latLng));
                            preferences.apply();

                            // Toast.makeText(MessOnwnerLoginPage.this, getAddressFromLatLng(latLng)+"", Toast.LENGTH_SHORT).show();

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference dataRef = ref.child("mess").child(mobile_No.getText().toString());


                            Map<String, Object> dataAdd = new HashMap<>();
                            dataAdd.put("token", tokenString);
                            dataRef.updateChildren(dataAdd).addOnFailureListener(e -> {
                                // Error occurred while saving data
                                loadingDialog.stopLoading();
                                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            });

                            loadingDialog.stopLoading();
                            Intent intent = new Intent(MessOnwnerLoginPage.this, HomeForMessOwner.class);
                            startActivity(intent);
                            finish();
                        } else {
                            loadingDialog.stopLoading();
                            Toast.makeText(MessOnwnerLoginPage.this, "Please enter correct password", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        loadingDialog.stopLoading();
                        Toast.makeText(MessOnwnerLoginPage.this, "Account not found", Toast.LENGTH_LONG).show();
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
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

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    tokenString = task.getResult();
                } else {
                    // Handle the case when token retrieval fails
                    Toast.makeText(MessOnwnerLoginPage.this, "Failed to retrieve token",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}