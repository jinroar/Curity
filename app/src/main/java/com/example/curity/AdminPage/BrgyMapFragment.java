package com.example.curity.AdminPage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.curity.Objects.AcceptedAlerts;
import com.example.curity.R;
import com.example.curity.Services.ApiInterface;
import com.example.curity.Services.Result;
import com.example.curity.Services.Route;
import com.example.curity.databinding.ActivityMapsBinding;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
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
import com.google.maps.android.SphericalUtil;

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


public class BrgyMapFragment extends Fragment implements OnMapReadyCallback {

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public BrgyMapFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment BrgyDashboardFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static BrgyMapFragment newInstance(String param1, String param2) {
//        BrgyMapFragment fragment = new BrgyMapFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

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
    private Circle centerCircle;
    private Circle carCircle;
    private int rad;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_brgy_map, container, false);

        addressTextView = view.findViewById(R.id.addressTextView);
        resetLocationButton = view.findViewById(R.id.resetLocationButton);

        //Status bar color
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.red));

        if (isLocationPermissionGranted()) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            geocoder = new Geocoder(getContext());
            retrofitInit();
        } else {
            requestLocationPermisson();
        }
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.curity_map_style));
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
        getDirections(doubToString(adminCurrentLatitude, adminCurrentLongitude) ,doubToString(userCurrentLatitude,userCurrentLongitude));


        //zoom in animation
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(adminCurrentLatitude,adminCurrentLongitude), 17));

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
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
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
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

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
                    rad+=1;
                    getEndLocation();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }



    private void requestLocationPermisson(){
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Intent intent = getActivity().getIntent();
        getActivity().finish();
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
                        polyLineOptions.color(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.red));
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
        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                double templng = adminCurrentLongitude;
                double templat = adminCurrentLatitude;
                getStartLocation();

                if(templat != adminCurrentLatitude || templng != adminCurrentLongitude){
                    Toast.makeText(getContext(),"Location changed",Toast.LENGTH_SHORT).show();
                    setGeocoder();
                    setFirebase();
                    polyline.remove();
                    mMap.clear();
                    getDirections(doubToString(adminCurrentLatitude, adminCurrentLongitude) ,doubToString(userCurrentLatitude,userCurrentLongitude));
                    ori = new LatLng(adminCurrentLatitude, adminCurrentLongitude);
                    desti = new LatLng(userCurrentLatitude,userCurrentLongitude);

                }else {
//                    Toast.makeText(userMapsActivity.this,"Location unchanged, lat: "+lastLocation.getLatitude()+",lng:"+lastLocation.getLongitude(),Toast.LENGTH_SHORT).show();
                }
                locationChange();
            }
        }.start();
    }

    private void brgyAnimation(){

    }
}