package com.gn4k.dailybites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.room.Room;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.gn4k.dailybites.Animation.LoadingDialog;
import com.gn4k.dailybites.RoomForRecent.Mess;
import com.gn4k.dailybites.RoomForRecent.MessDao;
import com.gn4k.dailybites.RoomForRecent.MessDatabase;
import com.gn4k.dailybites.RoomForWhishList.Wishlist;
import com.gn4k.dailybites.RoomForWhishList.WishlistDao;
import com.gn4k.dailybites.RoomForWhishList.WishlistDatabase;
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

    private CardView back, infoCard,  diamondCard, goldCard, silverCard, addToWishList;
    TextView tvMessName, tvAddress, tvRatings, tvIsNonVegAvailable, tvIsVerified, priseD, priseG, priseS, mobileNo, email;
    boolean isSPlanPresent = true, isGPlanPresent = true, isDPlanPresent = true;
    ImageView cover, isVeg;
    String messName, address, token ,messLatitude, messLongitude, messMobile, urlCover, verifyString = "Not Verified";
    NestedScrollView nestedScrollView;
    private int previousScrollY = 0;
    private long num;
    private GoogleMap googleMap;
    private MapView mapView;
    private double mlatitude;  // Your latitude value
    private double mlongitude; // Your longitude value
    LoadingDialog loadingDialog;

    Button showMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_info);

        loadingDialog  = new LoadingDialog(this);
        loadingDialog.startLoading();
        showMap = findViewById(R.id.openMap);
        mobileNo = findViewById(R.id.mobileNo);
        email = findViewById(R.id.email);
        back = findViewById(R.id.back);
        addToWishList = findViewById(R.id.like);
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
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY+1 > previousScrollY) {
                // Scrolling down
                slideUp(infoCard);

            } else if (scrollY-1 < previousScrollY) {
                // Scrolling up
                slideDown(infoCard);
            }
            previousScrollY = scrollY;
        });

        mobileNo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + messMobile));
            startActivity(intent);
        });

        showMap.setOnClickListener(v -> {
            String uri = "https://www.google.com/maps/dir/?api=1&destination=" + messLatitude + "," + messLongitude;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        });

        diamondCard.setOnClickListener(v -> {
            if(isDPlanPresent) {
                Intent intent = new Intent(MessInfo.this, PlanInfo.class);
                intent.putExtra("plan", "Diamond");
                intent.putExtra("mobileNo", messMobile);
                intent.putExtra("messName", messName);
                intent.putExtra("token",token);
                startActivity(intent);
            }
        });

        goldCard.setOnClickListener(v -> {
            if(isGPlanPresent) {
                Intent intent = new Intent(MessInfo.this, PlanInfo.class);
                intent.putExtra("plan", "Gold");
                intent.putExtra("mobileNo", messMobile);
                intent.putExtra("messName", messName);
                intent.putExtra("token",token);
                startActivity(intent);
            }
        });

        silverCard.setOnClickListener(v -> {
            if(isSPlanPresent) {
                Intent intent = new Intent(MessInfo.this, PlanInfo.class);
                intent.putExtra("plan", "Silver");
                intent.putExtra("mobileNo", messMobile);
                intent.putExtra("messName", messName);
                intent.putExtra("token",token);
                startActivity(intent);
            }
        });


        back.setOnClickListener(v -> onBackPressed());

        addToWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BgThreadWishlist().start();
                Toast.makeText(MessInfo.this, "Added in wishlist", Toast.LENGTH_SHORT).show();
            }
        });
    }



    class Bgthread extends Thread { // to add a mess in recent list in room database
        public void run() {
            super.run();

            MessDatabase messdb = Room.databaseBuilder(getApplicationContext(),
                    MessDatabase.class, "RecentView_DB").build();

            MessDao messDao = messdb.userDao();

            long lastUid = messDao.getLastMessUid();

            if (lastUid == 0) {
                // Database is empty, set initial uid to 1
                int initialUid = 1;
                messDao.insert(new Mess(initialUid, messName, messMobile, urlCover));
            } else {
                long nextUid = lastUid + 1;

                if (messDao.isExistByMessNo(messMobile)) {
                    Mess existingMess = messDao.getMessByUid(messMobile);
                    messDao.delete(existingMess);
                    messDao.insert(new Mess(nextUid, messName, messMobile, urlCover));
                }else{
                    messDao.insert(new Mess(nextUid, messName, messMobile, urlCover));
                }
            }
        }
    }


    class BgThreadWishlist extends Thread { // to add a mess in Wishlist list in room database
        public void run() {
            super.run();

            WishlistDatabase wishlistViewDb = Room.databaseBuilder(getApplicationContext(),
                    WishlistDatabase.class, "WishlistView_DB").build();

            WishlistDao wishlistDao = wishlistViewDb.userDao();

            long lastUid = wishlistDao.getLastWishlistUid();

            if (lastUid == 0) {
                // Database is empty, set initial uid to 1
                int initialUid = 1;
                wishlistDao.insert(new Wishlist(initialUid, messName, messMobile, urlCover, verifyString));
            } else {
                long nextUid = lastUid + 1;

                if (!wishlistDao.isExistByWishlistNo(messMobile)) {
                    wishlistDao.insert(new Wishlist(nextUid, messName, messMobile, urlCover, verifyString));
                }
            }
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

                            if (messLatitude != null && messLongitude != null) {

                                mlatitude = Double.parseDouble(messLatitude);
                                mlongitude = Double.parseDouble(messLongitude);

                                LatLng latLng = new LatLng(mlatitude, mlongitude);
                                getAddressFromLatLng(latLng);
                            }


                            if (urlCover != null) {
                                Glide.with(MessInfo.this).load(urlCover).centerCrop().placeholder(R.drawable.silver).into(cover);

                                //sending data to room database for recent
                                num = Long.parseLong(messMobile);
                                new Bgthread().start();
                            }
                        }

                        tvRatings.setText((String) data.get("ratings"));
                        mobileNo.setText("Mobile no: +91 "+(String) data.get("mobileNo"));
                        email.setText("Email: "+(String) data.get("email"));
                        token=(String) data.get("token");

                        if(((String) data.get("isVerified")).equals("yes")){
                            verifyString = "Verified";
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
                    loadingDialog.stopLoading();
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