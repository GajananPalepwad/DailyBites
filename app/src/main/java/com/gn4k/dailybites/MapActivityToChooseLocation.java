package com.gn4k.dailybites;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivityToChooseLocation extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private MapView mapView;
    private GoogleMap googleMap;
    private Marker marker;
    private EditText addressT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_to_choose_location);

        mapView = findViewById(R.id.mapView);
        addressT = findViewById(R.id.address);

        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnMapClickListener(this);
        // Call a method to request and handle location updates
        requestLocationUpdates();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

        // Remove existing marker if it exists
        if (marker != null) {
            marker.remove();
        }

        // Create a new marker at the clicked location
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Clicked location")
                .snippet("Latitude: " + latitude + ", Longitude: " + longitude);
        marker = googleMap.addMarker(markerOptions);

        // Move the camera to the clicked location
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        // Display a Toast message
        String message = "Clicked location - Latitude: " + latitude + ", Longitude: " + longitude;
        Toast.makeText(MapActivityToChooseLocation.this, message, Toast.LENGTH_SHORT).show();
        getAddressFromLatLng(latLng);
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

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private void requestLocationUpdates() {
        // Check for location permissions
        if (ContextCompat.checkSelfPermission(MapActivityToChooseLocation.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions
            ActivityCompat.requestPermissions(MapActivityToChooseLocation.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Enable location layer on the map
        googleMap.setMyLocationEnabled(true);

        // Get the last known location
        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(MapActivityToChooseLocation.this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(MapActivityToChooseLocation.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Use the user's last known location
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            LatLng latLng = new LatLng(latitude, longitude);

                            // Move the camera to the user's location
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                        } else {
                            // If the user's location is not available, set a default location
                            LatLng defaultLatLng = new LatLng(20.5937, 78.9629); // Default location (India)
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 5f));
                        }
                    }
                });
    }


    private void getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String fullAddress = address.getAddressLine(0); // Get the full address including street, city, etc.
                //Toast.makeText(this, fullAddress, Toast.LENGTH_SHORT).show();
                addressT.setText(fullAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permissions granted
                requestLocationUpdates();
            } else {
                // Location permissions denied
                // Handle this case as needed
            }
        }
    }
}
