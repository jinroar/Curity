package com.example.curity.AdminPage;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.curity.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link BrgyHomeFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class BrgyHomeFragment extends Fragment {


//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public BrgyHomeFragment() {
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
//    public static BrgyHomeFragment newInstance(String param1, String param2) {
//        BrgyHomeFragment fragment = new BrgyHomeFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
    private static final int LOCATION_PERMISSION_CODE = 101;

    private String userID;
    private boolean userFound;
    private int msgNum = 1;

    private DatabaseReference databaseReference;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private Geocoder geocoder;
    private TextView homeMessage;
    private LocationManager locationManager;
    private Location lastLocation;
    private double adminCurrentLatitude;
    private double adminCurrentLongitude;
    private LatLng currentLoc;
    private String fName;
    private int rad=1;
    private int alertCounter = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_brgy_home, container, false);

        homeMessage = view.findViewById(R.id.message);


        if (isLocationPermissionGranted()) {
            getStartLocation();
            locationChange();
        } else {
            requestLocationPermisson();
            Fragment frg = null;
            frg = getActivity().getSupportFragmentManager().findFragmentByTag("BrgyHomeFragment.java");
            final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.detach(frg);
            ft.attach(frg);
            ft.commit();
        }

        return view;
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

    public void getUserInfo() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User's Location");
        geoFire = new GeoFire(databaseReference);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(adminCurrentLatitude,adminCurrentLongitude),rad);
        geoQuery .removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!userFound) {
                    userFound = true;
                    userID = key;

                    FirebaseDatabase.getInstance().getReference("users")
                            .child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().exists()) {
                                            DataSnapshot dataSnapshot = task.getResult();
                                            fName = String.valueOf(dataSnapshot.child("firstName").getValue());
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
                if (!userFound) {
                    rad++;
                    getUserInfo();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }

        });

        if (userFound && alertCounter == 0){
            alertBox();
            alertCounter+=1;
        }

    }

    private void alertBox(){
        if(userFound && getContext() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View layout_dialog = LayoutInflater.from(getContext()).inflate(R.layout.alert_notif_design, null);
            builder.setView(layout_dialog);

            AppCompatButton btnLocate = layout_dialog.findViewById(R.id.btnLocate);
            ImageView exit = layout_dialog.findViewById(R.id.exit);
            TextView message = layout_dialog.findViewById(R.id.message);

            message.setText(fName +" Needs Help!");
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);

            dialog.getWindow().setGravity(Gravity.CENTER);

            btnLocate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    userFound = false;
                    alertCounter = 0;
                    Intent intent = new Intent(BrgyHomeFragment.this.requireContext(), AdminMapsActivity.class);
                    Log.d("USERIDFROMBRGY",userID);
                    intent.putExtra("userID",userID);
                    startActivity(intent);
                }
            });

            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        }
    }



    private void locationChange(){
        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                switch (msgNum){
                    case 1:
                        homeMessage.setText("Notifications will appear, Please standby");
                        msgNum++;
                        break;
                    case 2:
                        homeMessage.setText("Press locate when the notifications appear to locate the user");
                        msgNum++;
                        break;
                    case 3:
                        homeMessage.setText("Respond Immediately, A person needs help ");
                        msgNum = 1;
                        break;
                }
                getUserInfo();
                locationChange();
            }
        }.start();
    }

    private boolean isLocationPermissionGranted() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestLocationPermisson(){
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_CODE);
    }

}