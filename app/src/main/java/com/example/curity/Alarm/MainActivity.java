//package com.example.curity.Alarm;
//
//import android.app.ActivityManager;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.curity.R;
//
//public class MainActivity extends AppCompatActivity {
//
//    private com.example.curity.Alarm.SensorService sensorService;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Button Initialization
//        Button cameraButton = findViewById(R.id.camera);
//
//        // start the service
//        sensorService = new SensorService();
//
//
//        if (!isMyServiceRunning(sensorService.getClass())) {
//            startService(new Intent(this, sensorService.getClass()));
//        }
//
//        cameraButton.setOnClickListener(view -> {
//            Toast.makeText(this, "Record Video", Toast.LENGTH_SHORT).show();
//        });
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume(); // If notification if clicked. It will go to onResume()
//
//        if(AudioPlay.isplayingAudio){ // this will stop the SOS audio after clicking the notification
//            AudioPlay.stopAudio();
//            Toast.makeText(this, "SOS Stopped", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    // To check if the service is running
//    private boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                Log.i("Service status", "Running");
//                return true;
//            }
//        }
//        Log.i("Service status", "Not running");
//        return false;
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        Intent broadcastIntent = new Intent();
//        broadcastIntent.setAction("restartservice");
//        broadcastIntent.setClass(this, ReactivateService.class);
//        this.sendBroadcast(broadcastIntent);
//        super.onDestroy();
//    }
//
//
//}
