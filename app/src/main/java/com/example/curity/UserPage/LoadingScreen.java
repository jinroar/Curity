package com.example.curity.UserPage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.curity.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoadingScreen extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_CODE = 101;

    private boolean adminFound = false;
    private double adminCurrentLatitude;
    private double adminCurrentLongitude;
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private GeoFire geoFire;
    private LocationManager locationManager;
    private Location lastLocation;
    private double userCurrentLatitude, userCurrentLongitude;
    private LottieAnimationView animationView;
    private TextView message;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        animationView = findViewById(R.id.loading_animation);
        message = findViewById(R.id.message);

        if (isLocationPermissionGranted()) {
            getLocation();
            setFirebase();
            isAdminFound();
        } else {
            requestLocationPermission();
            finish();
            startActivity(getIntent());
        }
    }


    private void isAdminFound() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Accepted Alerts");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(userID)){
                    if(!adminFound) {
                        adminFound = true;
                        adminCurrentLatitude = Double.parseDouble(String.valueOf(snapshot.child(userID).child("adminCurrentLatitude").getValue()));
                        adminCurrentLongitude = Double.parseDouble(String.valueOf(snapshot.child(userID).child("adminCurrentLongitude").getValue()));
                        animationView.pauseAnimation();
                        Intent intent = new Intent(LoadingScreen.this, userMapsActivity.class);
                        intent.putExtra("adminFound", adminFound);
                        finish();
                        startActivity(intent);
                    }
                }
                else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setFirebase(){
        if(!adminFound){
            Log.d("Set Firebase Admin Found:", "True");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User's Location");
            geoFire = new GeoFire(databaseReference);

            geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(),new GeoLocation(userCurrentLatitude,userCurrentLongitude));
        }else{
            Log.d("Set Firebase Admin Found:", "False");
            FirebaseDatabase.getInstance().getReference("Accepted Alerts").child(FirebaseAuth.getInstance()
                    .getCurrentUser().getUid()).child("userCurrentLatitude").setValue(userCurrentLatitude);

            FirebaseDatabase.getInstance().getReference("Accepted Alerts").child(FirebaseAuth.getInstance()
                    .getCurrentUser().getUid()).child("userCurrentLongitude").setValue(userCurrentLongitude);
        }
    }

    private void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }else if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            lastLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        userCurrentLatitude = lastLocation.getLatitude();
        userCurrentLongitude = lastLocation.getLongitude();
    }

    private boolean isLocationPermissionGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_CODE);
    }

    private void countTimer(){
        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                switch (counter){
                    case 1:
                        message.setText("Keep Calm, Help will be there soon.");
                        counter++;
                        break;
                    case 2:
                        message.setText("Please remain Calm. Breath in, Breath out.");
                        counter++;
                        break;
                    case 3:
                        message.setText("Help is on the way please standby!");
                        counter = 1;
                        break;
                }
                countTimer();
            }
        }.start();


    }
}