package com.gn4k.dailybites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MessInfo extends AppCompatActivity implements OnMapReadyCallback{

    private CardView back, infoCard;
    TextView tvMessName, tvAddress, tvRatings, tvIsVegAvailable;
    ImageView cover;
    String messName, address, ratings, isVegAvailable, messLatitude, messLongitude, messMobile;
    NestedScrollView nestedScrollView;
    private int previousScrollY = 0;
    private GoogleMap googleMap;
    private MapView mapView;
    private double mlatitude;  // Your latitude value
    private double mlongitude; // Your longitude value
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_info);

        back = findViewById(R.id.back);
        cover = findViewById(R.id.imageMessFood);
        tvMessName = findViewById(R.id.MessName);
        tvAddress = findViewById(R.id.address);
        tvRatings = findViewById(R.id.ratings);
        infoCard = findViewById(R.id.InfoCardView);
        tvIsVegAvailable = findViewById(R.id.isVegAvailable);
        updateAccordingToFirebase();
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        nestedScrollView = findViewById(R.id.nestedScrollView);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY+1 > previousScrollY) {
                    // Scrolling down
                    slideUp(infoCard);

                } else if (scrollY-1 < previousScrollY) {
                    // Scrolling up
                    slideDown(infoCard);
                }
                previousScrollY = scrollY;
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void updateAccordingToFirebase(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            messName = bundle.getString("messName");
            tvMessName.setText(messName);
            messLongitude = bundle.getString("messLongitude");
            messLatitude = bundle.getString("messLatitude");
            messMobile = bundle.getString("messMobile");

            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            DatabaseReference dbpath = db.child("mess").child(messMobile);
            dbpath.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();
                        String url = (String) data.get("coverImage");
                        Glide.with(MessInfo.this).load(url).centerCrop().placeholder(R.drawable.silver).into(cover);
                    } else {
                        Toast.makeText(MessInfo.this, "Account not found", Toast.LENGTH_LONG).show();
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            if (messLatitude != null && messLongitude != null) {

                mlatitude = Double.parseDouble(messLatitude);
                mlongitude = Double.parseDouble(messLongitude);

                LatLng latLng = new LatLng(mlatitude, mlongitude);
                getAddressFromLatLng(latLng);
            }else{
                tvAddress.setText("Mess Contact no: "+messMobile);
            }

        }
    }


    private void getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                this.address = address.getAddressLine(0); // Get the full address including street, city, etc.
                tvAddress.setText(this.address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Move the camera to the specified location
        LatLng location = new LatLng(mlatitude, mlongitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));

        // Add a marker at the specified location
        googleMap.addMarker(new MarkerOptions().position(location));
    }


    private void slideDown(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0);
        animator.setDuration(50);
        animator.start();
    }

    // Method to show the bottom navigation bar with animation
    private void slideUp(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", -view.getHeight()+(view.getHeight()/2));
        animator.setDuration(50);
        animator.start();
    }



}