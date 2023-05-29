package com.gn4k.dailybites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MessInfo extends AppCompatActivity {

    private CardView back;
    TextView tvMessName, tvAddress, tvRatings, tvIsVegAvailable;
    String messName, address, ratings, isVegAvailable, messLatitude, messLongitude, messMobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_info);

        back = findViewById(R.id.back);
        tvMessName = findViewById(R.id.MessName);
        tvAddress = findViewById(R.id.address);
        tvRatings = findViewById(R.id.ratings);
        tvIsVegAvailable = findViewById(R.id.isVegAvailable);
        updateAccordingToFirebase();
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

            LatLng latLng = new LatLng(Double.parseDouble(messLatitude), Double.parseDouble(messLongitude));
            getAddressFromLatLng(latLng);

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


    }