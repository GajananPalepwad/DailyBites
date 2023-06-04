package com.gn4k.dailybites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapToLocateMess extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private MapView mapView;
    private GoogleMap googleMap;
    private Marker marker;
    private EditText addressT;

    private SearchView searchView;

    private double latitude;
    private double longitude;
    private String addressPreference;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_to_locate_mess);


        // Initialize views
        mapView = findViewById(R.id.mapView);
        addressT = findViewById(R.id.address);
        Button submitAndMoveToNext = findViewById(R.id.checkout);
        searchView = findViewById(R.id.addressSearch);
        searchView.setIconifiedByDefault(false);

        // Initialize map view
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(this);

        // Search view listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLocation(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        submitAndMoveToNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save user location and address to shared preferences
                SharedPreferences sharedPreferences = getSharedPreferences("MessOwnerData",MODE_PRIVATE);
                SharedPreferences.Editor preferences = sharedPreferences.edit();

                if(!String.valueOf(latitude).isEmpty() && !String.valueOf(longitude).isEmpty()) {
                    if(latitude !=0 && longitude !=0) {

                        preferences.putString("MessOwnerLatitude", latitude + "");
                        preferences.putString("MessOwnerLongitude", longitude + "");
                        preferences.putString("MessOwnerAddress", addressPreference);
                        preferences.apply();
                        // Update user information in Firebase database
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference dataRef = ref.child("mess").child(sharedPreferences.getString("MessOwnerMobileNo", ""));

                        Map<String, Object> data = new HashMap<>();
                        data.put("latitude", latitude);
                        data.put("longitude", longitude);

                        dataRef.updateChildren(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Move to the home screen
                                        Intent intent = new Intent(MapToLocateMess.this, HomeForMessOwner.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Error occurred while saving data
                                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else {
                        Toast.makeText(MapToLocateMess.this, "Please click on your Location in map", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(MapToLocateMess.this, "Please click on your Location in map", Toast.LENGTH_SHORT).show();
                }
            }
        });


        showInstructionDialogBox();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnMapClickListener((GoogleMap.OnMapClickListener) this);

        // Call a method to request and handle location updates
        requestLocationUpdates();
    }


    @Override
    public void onMapClick(LatLng latLng) {
        latitude = latLng.latitude;
        longitude = latLng.longitude;

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
//        String message = "Clicked location - Latitude: " + latitude + ", Longitude: " + longitude;
//        Toast.makeText(MapToLocateMess.this, message, Toast.LENGTH_SHORT).show();

        // Get the address from the clicked location
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

    // Search for a location based on the query
    private void searchLocation(String query) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(query, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                moveMapToLocation(latLng);
                getAddressFromLatLng(latLng);
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void moveMapToLocation(LatLng latLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        if (marker != null) {
            marker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Searched location")
                .snippet("Latitude: " + latLng.latitude + ", Longitude: " + latLng.longitude);
        marker = googleMap.addMarker(markerOptions);
    }
    private void requestLocationUpdates() {
        // Check for location permissions
        if (ContextCompat.checkSelfPermission(MapToLocateMess.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions
            ActivityCompat.requestPermissions(MapToLocateMess.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Enable location layer on the map
        googleMap.setMyLocationEnabled(true);

        // Get the last known location
        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(MapToLocateMess.this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(MapToLocateMess.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Use the user's last known location
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            LatLng latLng = new LatLng(latitude, longitude);
                            getAddressFromLatLng(latLng);

                            // Create a marker at the current location
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(latLng)
                                    .title("Clicked location")
                                    .snippet("Latitude: " + latitude + ", Longitude: " + longitude);
                            marker = googleMap.addMarker(markerOptions);

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
                addressPreference = fullAddress;
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

    // Show instruction dialog box
    private void showInstructionDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you here?");
        builder.setMessage("Please touch on the map exactly where you live.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}