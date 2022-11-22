package com.example.curity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.curity.Services.ApiInterface;
import com.example.curity.Services.Result;
import com.example.curity.Services.Route;
import com.example.curity.databinding.ActivityMapsBinding;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class userMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_CODE = 101;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    //location
    private LocationManager locationManager;
    private Location lastLocation;
    private Geocoder geocoder;
    private LatLng currentLoc;
    private SupportMapFragment mapFragment;
    private double userCurrentLongitude, userCurrentLatitude;
    private double adminCurrentLongitude, adminCurrentLatitude;
    private LatLng ori;
    private LatLng desti;

    //database
    DatabaseReference databaseReference;
    GeoFire geoFire;
    ValueEventListener valueEventListener;

    //UI
    private TextView addressTextView;
    private ProgressBar progressBar;
    private ImageView resetLocationButton;

    private Retrofit retrofit;
    private ApiInterface apiInterface;
    private List<LatLng> polylinelist;
    private PolylineOptions polyLineOptions;
    private Polyline polyline;
    private boolean adminFound = false;
    private int counter = 0;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //UI Elements
        addressTextView = findViewById(R.id.addressTextView);
        resetLocationButton = findViewById(R.id.resetLocationButton);


        //Status bar color
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.red));


        if (isLocationPermissionGranted()) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            geocoder = new Geocoder(this);
            retrofitInit();
        } else {
            requestLocationPermisson();
        }

        getLocation();
        setFirebase();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //14.60650190264927, 121.00234099730302 -> coordinates ng police station
        mMap = googleMap;


        //Map Style
//        try {
//            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getBaseContext(), R.raw.curity_map_style));
//            if (!success) {
//                Log.e("EDMT_ERROR", "Map Style Error");
//            }
//        } catch (Resources.NotFoundException e) {
//            Log.e("EDMT_ERROR", e.getMessage());
//        }

        //to get lat and long

        setGeocoder();
        ori = new LatLng(userCurrentLatitude,userCurrentLongitude);
        desti = new LatLng(14.60650190264927,121.00234099730302);


        //zoom in animation
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));

        //for current location
        if (isLocationPermissionGranted()) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        //reset location
        binding.resetLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));
            }
        });
        locationChange();
    }

    private boolean isLocationPermissionGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void setGeocoder() {
        try {
            List<Address> address = geocoder.getFromLocation(userCurrentLatitude, userCurrentLongitude, 1);
            String addressStr = address.get(0).getAddressLine(0);
            addressTextView.setText(addressStr);
        } catch (IOException e) {
            e.printStackTrace();
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
        currentLoc = new LatLng(userCurrentLatitude, userCurrentLongitude);

        if (currentLoc == null){
            getLocation();
        }else{

        }
    }

    private void requestLocationPermisson(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_CODE);
    }

//    @Override
//    public void onLocationChanged(@NonNull Location location) {
//        Toast.makeText(this, "Location Changed", Toast.LENGTH_SHORT).show();
//        lastLocation = location;
//        userCurrentLatitude = lastLocation.getLatitude();
//        userCurrentLongitude = lastLocation.getLongitude();
//
//        setGeocoder();
//        polyline.remove();
//        mMap.clear();
//        getDirections(doubToString(userCurrentLatitude,userCurrentLongitude) ,"14.60650190264927,121.00234099730302");
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    //sending location
    private void setFirebase(){
        isAdminFound();
        if(!adminFound){
            Log.d("Set Firebase Admin Found:", "True");
            databaseReference = FirebaseDatabase.getInstance().getReference("User's Location");
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

    //check if paired with admin
    private void isAdminFound() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Accepted Alerts");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Log.d("Admin Found:", "True");
                    adminFound = true;
                    adminCurrentLatitude = Double.parseDouble(String.valueOf(snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("adminCurrentLatitude").getValue()));
                    adminCurrentLongitude = Double.parseDouble(String.valueOf(snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("adminCurrentLongitude").getValue()));

                }
                else{
                    Log.d("Admin Found:", "False");
                    adminFound = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //getting directions from api
    private void getDirections(String origin, String destination){

        apiInterface.getDirection("driving",
                        "less_driving",
                        origin,
                        destination,
                        getString(R.string.api_key)
                ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Result>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("directions: ","Subscribe" );
                    }

                    @Override
                    public void onSuccess(Result result) {
                        Log.d("directions: ","On Success" );
                        polylinelist = new ArrayList<>();
                        List<Route> routeList = result.getRoutes();
                        Log.d("directions: "," "+routeList );
                        for(Route route: routeList){
                            Log.d("route", " "+route);
                            String polyline = route.getOverviewPolyline().getPoints();
                            polylinelist.addAll(decodePoly(polyline));
                        }

                        polyLineOptions = new PolylineOptions();
                        polyLineOptions.color(ContextCompat.getColor(getApplicationContext(),R.color.black));
                        polyLineOptions.width(8);
                        polyLineOptions.startCap(new ButtCap());
                        polyLineOptions.jointType(JointType.ROUND);
                        polyLineOptions.addAll(polylinelist);
                        polyline = mMap.addPolyline(polyLineOptions);

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(ori);
                        builder.include(desti);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),100));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("directions: ","On Error" );
                    }
                });
    }

    //init for api
    private void retrofitInit(){
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://maps.googleapis.com/")
                .build();

        apiInterface = retrofit.create(ApiInterface.class);
    }

    //decoder of api
    private List<LatLng> decodePoly(String encoded){
        List<LatLng> poly = new ArrayList<>();
        int index = 0;
        int len = encoded.length();
        int lat = 0;
        int lng = 0;

        while (index < len){
            int b;
            int shift = 0;
            int result = 0;

            do{
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }while(b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;

            do{
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }while(b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng/ 1E5)));

            poly.add(p);
        }
        return poly;
    }

    private String doubToString(double lat, double lng){
        return lat + "," + lng;
    }

    //update location every 5 minutes
    private void locationChange(){
        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                double templng = userCurrentLongitude;
                double templat = userCurrentLatitude;
                getLocation();

                if(templat != userCurrentLatitude || templng != userCurrentLongitude){
//                    Toast.makeText(userMapsActivity.this,"Location changed",Toast.LENGTH_SHORT).show();
                    setGeocoder();
                    setFirebase();
                    if(adminFound && counter == 0){
                        Log.d("counter:", "0");
                        mMap.clear();
                        desti = (new LatLng(adminCurrentLatitude,adminCurrentLongitude));
                        getDirections(doubToString(userCurrentLatitude,userCurrentLongitude) ,doubToString(adminCurrentLatitude,adminCurrentLongitude));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(adminCurrentLatitude,adminCurrentLongitude)));
                        Toast.makeText(userMapsActivity.this, "We have tracked your location, Help is on the way! ", Toast.LENGTH_SHORT).show();
                        counter++;
                    }else if(adminFound && counter == 1){
                        Log.d("counter:", "1");
                        polyline.remove();
                        mMap.clear();
                        desti = (new LatLng(adminCurrentLatitude,adminCurrentLongitude));
                        getDirections(doubToString(userCurrentLatitude,userCurrentLongitude) ,doubToString(adminCurrentLatitude,adminCurrentLongitude));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(adminCurrentLatitude,adminCurrentLongitude)));
                    }

                }else {
//                    Toast.makeText(userMapsActivity.this,"Location unchanged, lat: "+lastLocation.getLatitude()+",lng:"+lastLocation.getLongitude(),Toast.LENGTH_SHORT).show();
                }
                locationChange();
            }
        }.start();
    }

}
