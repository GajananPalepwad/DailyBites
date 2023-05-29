package com.gn4k.dailybites;

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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UserLoginPage extends AppCompatActivity {


    private static final String KEY_NAME = "name";
    private static final String KEY_MOBILE_NO = "mobile no";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button login;
    private TextView pass;
    private TextView email;
    private GoogleSignInClient client;
    LatLng latLng ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login_page);

        login = findViewById(R.id.login);
        pass = findViewById(R.id.password);
        email = findViewById(R.id.email);
        TextView registration = findViewById(R.id.reg);



        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        client = GoogleSignIn.getClient(this, options);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkValidationToLogin();

            }
        });

        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRegistration(v);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UserLoginPage.this,LanguageChooser.class);
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
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(), UserRegistration.class);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(UserLoginPage.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    }

    private void checkValidationToLogin(){

        String getEmail = email.getText().toString();
        String getPassword = pass.getText().toString();

        Map<String, Object> userInfo = new HashMap<>();
        DocumentReference emailRef = db.collection("User").document(getEmail);
        emailRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.getString(KEY_PASSWORD).equals(getPassword)){


                        latLng = new LatLng(documentSnapshot.getDouble(KEY_LATITUDE), documentSnapshot.getDouble(KEY_LONGITUDE));

                        SharedPreferences sharedPreferences = getSharedPreferences("UserData",MODE_PRIVATE);
                        SharedPreferences.Editor preferences = sharedPreferences.edit();

                        preferences.putString("UserEmail",getEmail);
                        preferences.putString("UserPassword",getPassword);
                        preferences.putString("UserMobileNo",documentSnapshot.getString(KEY_MOBILE_NO)+"");
                        preferences.putString("UserName",documentSnapshot.getString(KEY_NAME));
                        preferences.apply();
                        if(documentSnapshot.getDouble(KEY_LATITUDE)== null){
                            Intent intent = new Intent(UserLoginPage.this, MapActivityToChooseLocation.class);
                            startActivity(intent);
                            return;
                        }
                        preferences.putString("UserLatitude",documentSnapshot.getDouble(KEY_LATITUDE)+"");
                        preferences.putString("UserLongitude",documentSnapshot.getDouble(KEY_LONGITUDE)+"");
                        preferences.putString("UserAddress",getAddressFromLatLng(latLng));
                        preferences.apply();

                        Intent intent = new Intent(UserLoginPage.this,Home.class);
                        startActivity(intent);
                        finish();

                    }else{

                        Toast.makeText(UserLoginPage.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(UserLoginPage.this, "Account not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

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


}