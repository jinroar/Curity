package com.example.curity.Alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.curity.R;
import com.example.curity.secondFragment;

import java.util.Timer;
import java.util.TimerTask;

public class SensorService extends Service {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    protected NotificationManager manager;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        // start the foreground service
        startMyOwnForeground();

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(count -> {
            // check if the user has shacked
            // the phone for 3 time in a row
            if (count == 3) {
                // vibrate the phone
                vibrate();
                Toast.makeText(SensorService.this, "SOS!", Toast.LENGTH_SHORT).show();

                // SOS-Sound
                // VOLUME
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 150, 0); // -> Volume: 20/150 (Max: 150)

                // Play SOS
                AudioPlay.playAudio(SensorService.this, R.raw.emergency);

            }
        });

        // register the listener
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    // method to vibrate the phone
    public void vibrate() {

        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        VibrationEffect vibEff;

        // Android Q and above have some predefined vibrating patterns
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibEff = VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK);
            vibrator.cancel();
            vibrator.vibrate(vibEff);
        } else {
            vibrator.vibrate(500);
        }
    }


    // For Build versions higher than Android Oreo, we launch
    // a foreground service in a different way. This is due to the newly
    // implemented strict notification rules, which require us to identify
    // our own notification channel in order to view them correctly.
    private void startMyOwnForeground() {

        Timer t = new Timer();
        t.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {

                        if(AudioPlay.isplayingAudio){
                            String NOTIFICATION_CHANNEL_ID = "example.permanence";
                            String channelName = "Background Service";
                            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_MIN);
                            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            assert manager != null;
                            manager.createNotificationChannel(chan);
                            Intent notificationIntent = new Intent(getApplicationContext(), secondFragment.class);
                            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(SensorService.this, NOTIFICATION_CHANNEL_ID);
                            Notification notification = notificationBuilder
                                    .setOngoing(true)
                                    .setContentTitle("STOP SOS")
                                    .setContentText("Press to Stop!")

                                    // this is important, otherwise the notification will show the way
                                    // you want i.e. it will show some default notification
                                    .setContentIntent(pendingIntent)
                                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                                    .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                                    .setCategory(Notification.CATEGORY_SERVICE)
                                    .build();
                            startForeground(1, notification);
                            notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
                        }else{
                            stopForeground(true);
                        }
                    }
                },0,
                500);
    }

    @Override
    public void onDestroy() {
        // create an Intent to call the Broadcast receiver
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, ReactivateService.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

}



