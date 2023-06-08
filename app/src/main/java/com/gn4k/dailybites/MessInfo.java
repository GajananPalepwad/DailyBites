package com.gn4k.dailybites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.gn4k.dailybites.RoomForRecent.Mess;
import com.gn4k.dailybites.RoomForRecent.MessDao;
import com.gn4k.dailybites.RoomForRecent.MessDatabase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
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

    private CardView back, infoCard,  diamondCard, goldCard, silverCard;
    TextView tvMessName, tvAddress, tvRatings, tvIsNonVegAvailable, tvIsVerified, priseD, priseG, priseS;
    boolean isSPlanPresent = true, isGPlanPresent = true, isDPlanPresent = true, isVegOrNot = false;
    ImageView cover, isVeg;
    String messName, address, ratings,messLatitude, messLongitude, messMobile, urlCover;
    NestedScrollView nestedScrollView;
    private int previousScrollY = 0;
    private long num;
    private GoogleMap googleMap;
    private MapView mapView;
    private double mlatitude;  // Your latitude value
    private double mlongitude; // Your longitude value
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_info);

        back = findViewById(R.id.back);
        diamondCard = findViewById(R.id.diamond);
        goldCard = findViewById(R.id.gold);
        silverCard = findViewById(R.id.silver);
        cover = findViewById(R.id.imageMessFood);
        isVeg = findViewById(R.id.isvegimage);
        tvMessName = findViewById(R.id.MessName);
        tvAddress = findViewById(R.id.address);
        tvRatings = findViewById(R.id.ratings);
        tvIsVerified = findViewById(R.id.isVerified);
        infoCard = findViewById(R.id.InfoCardView);
        tvIsNonVegAvailable = findViewById(R.id.isVegAvailable);
        priseD = findViewById(R.id.priceD);
        priseG = findViewById(R.id.priceG);
        priseS = findViewById(R.id.priceS);

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

        diamondCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDPlanPresent) {
                    Intent intent = new Intent(MessInfo.this, PlanInfo.class);
                    intent.putExtra("plan", "Diamond");
                    intent.putExtra("mobileNo", messMobile);
                    intent.putExtra("messName", messName);
                    startActivity(intent);
                }
            }
        });

        goldCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isGPlanPresent) {
                    Intent intent = new Intent(MessInfo.this, PlanInfo.class);
                    intent.putExtra("plan", "Gold");
                    intent.putExtra("mobileNo", messMobile);
                    intent.putExtra("messName", messName);
                    startActivity(intent);
                }
            }
        });

        silverCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSPlanPresent) {
                    Intent intent = new Intent(MessInfo.this, PlanInfo.class);
                    intent.putExtra("plan", "Silver");
                    intent.putExtra("mobileNo", messMobile);
                    intent.putExtra("messName", messName);
                    startActivity(intent);
                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


//        num = Long.parseLong(messMobile);
//        new Bgthread().start();
    }

    class Bgthread extends Thread{

        public void run(){
            super.run();

            MessDatabase messdb = Room.databaseBuilder(getApplicationContext(),
                    MessDatabase.class, "RecentView_DB").build();

            MessDao messDao = messdb.userDao();

            if(messDao.is_exist(num)) {
                Mess existingMess = messDao.getMessByUid(num);
                messDao.delete(existingMess);
            }
            messDao.insert(new Mess(num, messName, messMobile, urlCover));
        }

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

                        if (data.containsKey("coverImage")) {
                            urlCover = (String) data.get("coverImage");

                            if(messLatitude.equals("") || messLongitude.equals("")){
                                messLatitude = String.valueOf(data.get("latitude"));
                                messLongitude = String.valueOf(data.get("longitude"));
                            }

                            if (urlCover != null) {
                                Glide.with(MessInfo.this).load(urlCover).centerCrop().placeholder(R.drawable.silver).into(cover);

                                //sending data to room database for recent
                                num = Long.parseLong(messMobile);
                                new Bgthread().start();
                            }
                        }

                        tvRatings.setText((String) data.get("ratings"));

                        if(((String) data.get("isVerified")).equals("yes")){
                                tvIsVerified.setText("Verified");
                        }

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
            }




            DatabaseReference dbd = FirebaseDatabase.getInstance().getReference();
            DatabaseReference dbpathd = dbd.child("mess").child(messMobile).child("Diamondplan");

            dbpathd.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();

                        priseD.setText("₹"+String.valueOf(data.get("price")));
                        if((String.valueOf(data.get("isNonVegInclude"))).equals("yes")){
                            isVeg.setImageResource(R.drawable.nonveg);
                            tvIsNonVegAvailable.setText("Non-Veg Available");
                        }
                    } else {
                        priseD.setText("Not Available");
                        isDPlanPresent = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });


            DatabaseReference dbg = FirebaseDatabase.getInstance().getReference();
            DatabaseReference dbpathg = dbg.child("mess").child(messMobile).child("Goldplan");

            dbpathg.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();

                        priseG.setText("₹"+String.valueOf(data.get("price")));
                        if(String.valueOf(data.get("isNonVegInclude")).equals("yes")){

                            isVeg.setImageResource(R.drawable.nonveg);
                            tvIsNonVegAvailable.setText("Non-Veg Available");
                        }

                    } else {
                        priseG.setText("Not Available");
                        isGPlanPresent = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });DatabaseReference dbs = FirebaseDatabase.getInstance().getReference();
            DatabaseReference dbpaths = dbs.child("mess").child(messMobile).child("Silverplan");

            dbpaths.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();

                        priseS.setText("₹"+String.valueOf(data.get("price")));
                        if(String.valueOf(data.get("isNonVegInclude")).equals("yes")){

                            isVeg.setImageResource(R.drawable.nonveg);
                            tvIsNonVegAvailable.setText("Non-Veg Available");
                        }

                    } else {
                        priseS.setText("Not Available");
                        isSPlanPresent = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });


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