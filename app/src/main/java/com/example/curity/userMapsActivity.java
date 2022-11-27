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
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.curity.Chat.MessageChatAdapter;
import com.example.curity.Chat.MessageChatModel;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    ImageView sendBtn;
    EditText messageET;

    MessageChatModel messageChatModel;
    //attributes needed for chat
    private MessageChatAdapter adapter;
    ArrayList<MessageChatModel> messageChatModels = new ArrayList<>();

    private Uri filepath;
    private String downloadURL;

    ImageButton img_btn_select;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //UI Elements
        addressTextView = findViewById(R.id.addressTextView);
        resetLocationButton = findViewById(R.id.resetLocationButton);

        img_btn_select= findViewById(R.id.img_btn_select);

        img_btn_select.setOnClickListener(view -> {
            imageChooser();
        });


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

        //For chat
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_chat);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new MessageChatAdapter(getApplicationContext(), messageChatModels);
        recyclerView.setAdapter(adapter);

        messageET = findViewById(R.id.messageET);
        sendBtn = findViewById(R.id.sendBtn);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref =  FirebaseDatabase.getInstance().getReference().child("Accepted Alerts").child(userId).child("chat");


        sendBtn.setOnClickListener(view1 -> {
            //encrypt
            String message = encryptMessage(messageET.getText().toString());
            String time = encryptMessage(dtf.format(now));

            if(filepath != null){
                uploadImage(ref,message,time,userId);
            }else{


                //encrypted model
                messageChatModel = new MessageChatModel(message,time,2);
                messageChatModel.id = userId;

                messageChatModels.add(new MessageChatModel(
                        decryptMessage( messageET.getText().toString())
                        ,decryptMessage(time)
                        , 2));

                adapter.notifyDataSetChanged();

                String key = ref.push().getKey();

                ref.child(key).setValue(messageChatModel);

                messageET.setText("");
            }

        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageChatModels.clear();
                adapter.notifyDataSetChanged();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    //
                    String messageFromFB = decryptMessage(dataSnapshot.child("text").getValue().toString());
                    String timeFromFB =decryptMessage( dataSnapshot.child("time").getValue().toString());
                    // check if has image

                    MessageChatModel messageChatModel1 = new MessageChatModel(
                            messageFromFB,
                            timeFromFB,
                            dataSnapshot.child("id").getValue().toString().equals(userId) ? 2:1);
                    if(!dataSnapshot.child("imgUrl").getValue().toString().equals("")){
                        messageChatModel1.imgUrl = dataSnapshot.child("imgUrl").getValue().toString();
                    }
                    messageChatModels.add(messageChatModel1);
                }
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageChatModels.size()-1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getBaseContext(), R.raw.curity_map_style));
            if (!success) {
                Log.e("EDMT_ERROR", "Map Style Error");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("EDMT_ERROR", e.getMessage());
        }

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
                    adminFound = true;
//                    adminCurrentLatitude = Double.parseDouble(String.valueOf(snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("adminCurrentLatitude").getValue()));
//                    adminCurrentLongitude = Double.parseDouble(String.valueOf(snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("adminCurrentLongitude").getValue()));
                      adminCurrentLatitude = 14.807814;
                      adminCurrentLongitude = 121.047431;

                }
                else{
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


    private static final String TAG = "Chilkat123";
    static {
        System.loadLibrary("chilkat");

        // Note: If the incorrect library name is passed to System.loadLibrary,
        // then you will see the following error message at application startup:
        //"The application <your-application-name> has stopped unexpectedly. Please try again."
    }

    private String encryptMessage(String message){
        // This example assumes the Chilkat API to have been previously unlocked.
        // See Global Unlock Sample for sample code.

        com.chilkatsoft.CkCrypt2 crypt = new com.chilkatsoft.CkCrypt2();

        // Set the encryption algorithm = "twofish"
        crypt.put_CryptAlgorithm("twofish");

        // CipherMode may be "ecb" or "cbc"
        crypt.put_CipherMode("cbc");

        // KeyLength may be 128, 192, 256
        crypt.put_KeyLength(256);

        // The padding scheme determines the contents of the bytes
        // that are added to pad the result to a multiple of the
        // encryption algorithm's block size.  Twofish has a block
        // size of 16 bytes, so encrypted output is always
        // a multiple of 16.
        crypt.put_PaddingScheme(0);

        // EncodingMode specifies the encoding of the output for
        // encryption, and the input for decryption.
        // It may be "hex", "url", "base64", or "quoted-printable".
        crypt.put_EncodingMode("hex");

        // An initialization vector is required if using CBC mode.
        // ECB mode does not use an IV.
        // The length of the IV is equal to the algorithm's block size.
        // It is NOT equal to the length of the key.
        String ivHex = "000102030405060708090A0B0C0D0E0F";
        crypt.SetEncodedIV(ivHex,"hex");

        // The secret key must equal the size of the key.  For
        // 256-bit encryption, the binary secret key is 32 bytes.
        // For 128-bit encryption, the binary secret key is 16 bytes.
        String keyHex = "000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F";
        crypt.SetEncodedKey(keyHex,"hex");

        // Encrypt a string...
        // The input string is 44 ANSI characters (i.e. 44 bytes), so
        // the output should be 48 bytes (a multiple of 16).
        // Because the output is a hex string, it should
        // be 96 characters long (2 chars per byte).
        //String encStr = crypt.encryptStringENC("The quick brown fox jumps over the lazy dog.");
         String encStr = crypt.encryptStringENC(message);
         Log.i(TAG, encStr);

        // Now decrypt:
        //String decStr = crypt.decryptStringENC(message);
        //Log.i(TAG, decStr);

        // 0 = enc 1
        return encStr;
    }

    private String decryptMessage(String message){
        // This example assumes the Chilkat API to have been previously unlocked.
        // See Global Unlock Sample for sample code.

        com.chilkatsoft.CkCrypt2 crypt = new com.chilkatsoft.CkCrypt2();

        // Set the encryption algorithm = "twofish"
        crypt.put_CryptAlgorithm("twofish");

        // CipherMode may be "ecb" or "cbc"
        crypt.put_CipherMode("cbc");

        // KeyLength may be 128, 192, 256
        crypt.put_KeyLength(256);

        // The padding scheme determines the contents of the bytes
        // that are added to pad the result to a multiple of the
        // encryption algorithm's block size.  Twofish has a block
        // size of 16 bytes, so encrypted output is always
        // a multiple of 16.
        crypt.put_PaddingScheme(0);

        // EncodingMode specifies the encoding of the output for
        // encryption, and the input for decryption.
        // It may be "hex", "url", "base64", or "quoted-printable".
        crypt.put_EncodingMode("hex");

        // An initialization vector is required if using CBC mode.
        // ECB mode does not use an IV.
        // The length of the IV is equal to the algorithm's block size.
        // It is NOT equal to the length of the key.
        String ivHex = "000102030405060708090A0B0C0D0E0F";
        crypt.SetEncodedIV(ivHex,"hex");

        // The secret key must equal the size of the key.  For
        // 256-bit encryption, the binary secret key is 32 bytes.
        // For 128-bit encryption, the binary secret key is 16 bytes.
        String keyHex = "000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F";
        crypt.SetEncodedKey(keyHex,"hex");

        // Encrypt a string...
        // The input string is 44 ANSI characters (i.e. 44 bytes), so
        // the output should be 48 bytes (a multiple of 16).
        // Because the output is a hex string, it should
        // be 96 characters long (2 chars per byte).
        //String encStr = crypt.encryptStringENC("The quick brown fox jumps over the lazy dog.");
//        String encStr = crypt.encryptStringENC(message);
//        Log.i(TAG, encStr);

        // Now decrypt:
        String decStr = crypt.decryptStringENC(message);
        Log.i(TAG, "decrypt: "+decStr);

        // 0 = enc 1
        return decStr;
    }

    int PICKFILE_REQUEST_CODE = 200;
    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), PICKFILE_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Log.d("selectedImageUri", data.getData() + "");
            if (requestCode == PICKFILE_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                filepath = selectedImageUri;
                if (selectedImageUri != null) {
                    // update the preview image in the layout
//                    Picasso.get().load(selectedImageUri)
//                            .resize(200,200)
//                            .into(img_btn_add_image);

                    Log.d("selectedImageUri", selectedImageUri + "");
                }
            }

        }
    }

    public void uploadImage(DatabaseReference ref, String message, String time,String userId){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        Uri file = this.filepath;
        StorageReference reportFiles = storage.getReference().child("images/"+file.getLastPathSegment());

        UploadTask uploadTask = reportFiles.putFile(file);
        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return reportFiles.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.d("DOWNLOADURK",""+downloadUri);
                    //encrypted model
                    messageChatModel = new MessageChatModel(message,time,2);
                    messageChatModel.id = userId;
                    messageChatModel.imgUrl = downloadUri+"";
                    messageChatModels.add(messageChatModel);

                    adapter.notifyDataSetChanged();

                    String key = ref.push().getKey();

                    ref.child(key).setValue(messageChatModel);

                    messageET.setText("");

                } else {
                    //btn_submit.setEnabled(true);
                    // Handle failures
                    // ...
                }
            }
        });

    }
}
