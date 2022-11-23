package com.example.curity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.curity.Chat.MessageChatAdapter;
import com.example.curity.Chat.MessageChatModel;
import com.example.curity.Objects.AcceptedAlerts;
import com.example.curity.Services.ApiInterface;
import com.example.curity.Services.Result;
import com.example.curity.Services.Route;
import com.example.curity.SignUp.User;
import com.example.curity.databinding.ActivityMapsBinding;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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


public class AdminMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_CODE = 101;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    //location
    private LocationManager locationManager;
    private Location lastLocation;
    private Geocoder geocoder;
    private LatLng currentLoc;
    private SupportMapFragment mapFragment;
    private double adminCurrentLongitude, adminCurrentLatitude;
    private double userCurrentLongitude, userCurrentLatitude;
    private LatLng ori;
    private LatLng desti;

    //database
    DatabaseReference databaseReference;
    DatabaseReference userLocRef;
    Task<DataSnapshot> userRef;
    GeoFire geoFire;
    ValueEventListener valueEventListener;

    //UI
    private TextView addressTextView;
    private ImageView resetLocationButton;

    private Retrofit retrofit;
    private ApiInterface apiInterface;
    private List<LatLng> polylinelist;
    private PolylineOptions polyLineOptions;
    private Polyline polyline;
    private GeoQuery geoQuery;
    private String userID;

    private boolean userFound = false, requestType = false;
    private int rad;

    ImageView sendBtn;
    EditText messageET;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        addressTextView = findViewById(R.id.addressTextView);
        resetLocationButton = findViewById(R.id.resetLocationButton);

        //Status bar color
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.red));

        if (isLocationPermissionGranted()) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            geocoder = new Geocoder(this);
            retrofitInit();
        } else {
            requestLocationPermisson();
        }

        //For chat
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_chat);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new MessageChatAdapter(getApplicationContext(), messageChatModels);
        recyclerView.setAdapter(adapter);

        messageET = findViewById(R.id.messageET);
        sendBtn = findViewById(R.id.sendBtn);

        MessageChatModel messageChatModel = new MessageChatModel(messageET.getText().toString(),"",2);
        String adminID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref =  FirebaseDatabase.getInstance().getReference().child("Accepted Alerts").child(userID).child("chat");

        sendBtn.setOnClickListener(view1 -> {

            messageChatModel.id = adminID;
            messageChatModels.add(messageChatModel);

            String key = ref.push().getKey();
            ref.child(key).setValue(messageChatModel);

            adapter.notifyDataSetChanged();
            messageET.setText("");
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageChatModels.clear();
                adapter.notifyDataSetChanged();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    //
                    MessageChatModel messageChatModel1 = new MessageChatModel(
                            "",
                            "",
                            dataSnapshot.child("id").getValue().toString().equals(adminID) ? 2:1);
                    messageChatModels.add(messageChatModel1);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.curity_map_style));
            if (!success) {
                Log.e("EDMT_ERROR", "Map Style Error");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("EDMT_ERROR", e.getMessage());
        }

        getStartLocation();
        getEndLocation();
        setFirebase();
        setGeocoder();
        ori = new LatLng(adminCurrentLatitude, adminCurrentLongitude);
        desti = new LatLng(userCurrentLatitude,userCurrentLongitude);


        //zoom in animation
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(adminCurrentLatitude,adminCurrentLongitude), 17));
        getDirections(doubToString(adminCurrentLatitude, adminCurrentLongitude) ,doubToString(userCurrentLatitude,userCurrentLongitude));

        //for current location
        if (isLocationPermissionGranted()) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        //reset location
//        binding.resetLocationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));
//            }
//        });
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
            List<Address> address = geocoder.getFromLocation(adminCurrentLatitude, adminCurrentLongitude, 1);
            String addressStr = address.get(0).getAddressLine(0);
            if(addressStr != null){
                addressTextView.setText(addressStr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getStartLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }else if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            lastLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        adminCurrentLatitude = lastLocation.getLatitude();
        adminCurrentLongitude = lastLocation.getLongitude();
        currentLoc = new LatLng(adminCurrentLatitude, adminCurrentLongitude);


        if (currentLoc == null){
            getStartLocation();
        }else{

        }

    }


    //attributes needed for chat
    private MessageChatAdapter adapter;
    ArrayList<MessageChatModel> messageChatModels = new ArrayList<>();
    private void getEndLocation(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User's Location");
        geoFire = new GeoFire(databaseReference);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(adminCurrentLatitude,adminCurrentLongitude),rad);
        geoQuery .removeAllListeners();



        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!userFound){
                    userFound = true;
                    userID = key;
                    Log.d("Location",": "+userID);

                    FirebaseDatabase.getInstance().getReference().child("User's Location")
                            .child(userID).child("l").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().exists()) {
                                            DataSnapshot dataSnapshot = task.getResult();
                                            userCurrentLatitude = Double.parseDouble(String.valueOf(dataSnapshot.child("0").getValue()));
                                            userCurrentLongitude = Double.parseDouble(String.valueOf(dataSnapshot.child("1").getValue()));
                                            updateFirebase();
                                        }
                                    }
                                }
                            });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(!userFound){
                    if(rad>5){
                        rad=1;
                    }else{
                        rad+=1;
                    }
                    getEndLocation();

                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }

    private void updateFirebase(){
        String adminID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("User's Location").child(userID).removeValue();
        AcceptedAlerts acceptedAlerts = new AcceptedAlerts(userID,adminID,
                userCurrentLatitude,userCurrentLongitude,adminCurrentLatitude,adminCurrentLongitude);

        FirebaseDatabase.getInstance().getReference().child("Accepted Alerts")
                .child(userID)
                .setValue(acceptedAlerts).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                DataSnapshot dataSnapshot = task.getResult();
                                String fName = String.valueOf(dataSnapshot.child("firstName").getValue());
                                String lName = String.valueOf(dataSnapshot.child("lastName").getValue());
                                String address = String.valueOf(dataSnapshot.child("address").getValue());
                                String phone = String.valueOf(dataSnapshot.child("phone").getValue());
                                String email = String.valueOf(dataSnapshot.child("email").getValue());
                                String isAdmin = String.valueOf(dataSnapshot.child("isAdmin").getValue());


                                User user = new User(fName,lName, address,phone,email,isAdmin);
                                FirebaseDatabase.getInstance().getReference().child("Accepted Alerts")
                                        .child(userID).child("user")
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });

                            }
                        }
                    }
                });

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                DataSnapshot dataSnapshot = task.getResult();
                                String fName = String.valueOf(dataSnapshot.child("firstName").getValue());
                                String lName = String.valueOf(dataSnapshot.child("lastName").getValue());
                                String address = String.valueOf(dataSnapshot.child("address").getValue());
                                String phone = String.valueOf(dataSnapshot.child("phone").getValue());
                                String email = String.valueOf(dataSnapshot.child("email").getValue());
                                String isAdmin = String.valueOf(dataSnapshot.child("isAdmin").getValue());


                                User user = new User(fName,lName, address,phone,email,isAdmin);
                                FirebaseDatabase.getInstance().getReference().child("Accepted Alerts")
                                        .child(userID).child("admin")
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });

                            }
                        }
                    }
                });


    }

    private void requestLocationPermisson(){
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void setFirebase(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Admin's Location");
        geoFire = new GeoFire(databaseReference);

        geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(),new GeoLocation(adminCurrentLatitude, adminCurrentLongitude));
    }

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
                        polyLineOptions.color(ContextCompat.getColor(getApplicationContext(),R.color.red));
                        polyLineOptions.width(8);
                        polyLineOptions.startCap(new ButtCap());
                        polyLineOptions.jointType(JointType.ROUND);
                        polyLineOptions.addAll(polylinelist);
                        polyline = mMap.addPolyline(polyLineOptions);

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(ori);
                        builder.include(desti);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(adminCurrentLatitude,adminCurrentLongitude), 17));
//                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),100));

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("directions: ","On Error" );
                    }
                });
    }

    private void retrofitInit(){
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://maps.googleapis.com/")
                .build();

        apiInterface = retrofit.create(ApiInterface.class);
    }

    //decoder
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

    private void locationChange(){
        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                double templng = adminCurrentLongitude;
                double templat = adminCurrentLatitude;
                getStartLocation();

                if(templat != adminCurrentLatitude || templng != adminCurrentLongitude){
//                    Toast.makeText(AdminMapsActivity.this,"Location changed",Toast.LENGTH_SHORT).show();
                    setGeocoder();
                    setFirebase();
                    polyline.remove();
                    mMap.clear();
                    getDirections(doubToString(adminCurrentLatitude, adminCurrentLongitude) ,doubToString(userCurrentLatitude,userCurrentLongitude));
                    ori = new LatLng(adminCurrentLatitude, adminCurrentLongitude);
                    desti = new LatLng(userCurrentLatitude,userCurrentLongitude);
                    mMap.addMarker(new MarkerOptions().position(new LatLng(userCurrentLatitude,userCurrentLongitude)));


                }else {
//                    Toast.makeText(userMapsActivity.this,"Location unchanged, lat: "+lastLocation.getLatitude()+",lng:"+lastLocation.getLongitude(),Toast.LENGTH_SHORT).show();
                }
                locationChange();
            }
        }.start();
    }

//    private void brgyAnimation(){
//
//    }


}