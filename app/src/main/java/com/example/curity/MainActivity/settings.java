package com.example.curity.MainActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.curity.R;

public class settings extends AppCompatActivity {

    SwitchCompat switch_1;
    Boolean stateSwitch1;
    SharedPreferences preferences;
    ImageButton shakeDis, shakeAlarm;
    TextView shakeDisTxt, shakeAlarmTxt;
    String[] listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        // for the Notifications
        preferences = getSharedPreferences("PREFS", 0);
        stateSwitch1 = preferences.getBoolean("switch1", false);
        switch_1 = (SwitchCompat) findViewById(R.id.switch_1);
        switch_1.setChecked(stateSwitch1);

        switch_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stateSwitch1 = !stateSwitch1;
                switch_1.setChecked(stateSwitch1);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("switch1", stateSwitch1);
                editor.apply();
            }
        });

        // for the shake Disable method
        shakeDis = (ImageButton) findViewById(R.id.shakeDisableMethod_btn);
        shakeDisTxt = (TextView) findViewById(R.id.shakeDisableMethod_txt);
        shakeDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating list of items
                listItems = new String[]{"Method 1", "Method 2", "Method 3"};
                AlertDialog.Builder mbuilder = new AlertDialog.Builder(settings.this);
                mbuilder.setTitle("Choose a Method to disable");
                mbuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //put here the item that was choosen
                        shakeDisTxt.setText(listItems[i]);

                        dialogInterface.dismiss();
                    }
                });

                mbuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });


                //show the alert Dialog
                AlertDialog mDialog = mbuilder.create();
                mDialog.show();
            }
        });


        //for the shake alarm sound
        shakeAlarm = (ImageButton) findViewById(R.id.shakeAlarmSound_btn);
        shakeAlarmTxt = (TextView) findViewById(R.id.shakeAlarmSound_txt);
        shakeAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating list of items
                listItems = new String[]{"ringtone 1", "ringtone 2", "ringtone 3"};
                AlertDialog.Builder mbuilder = new AlertDialog.Builder(settings.this);
                mbuilder.setTitle("Choose an alarm sound");
                mbuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //put here the item that was choosen
                        shakeAlarmTxt.setText(listItems[i]);

                        dialogInterface.dismiss();
                    }
                });

                mbuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });


                //show the alert Dialog
                AlertDialog mDialog = mbuilder.create();
                mDialog.show();
            }
        });

    }
}