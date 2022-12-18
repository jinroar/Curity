package com.example.curity.UserPage;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.curity.Alarm.*;
import com.example.curity.R;


public class UserShakeFragment extends Fragment {

    SensorService sensorService;
    Button button;

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public secondFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment BlankFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static secondFragment newInstance(String param1, String param2) {
//        thirdFragment fragment = new thirdFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_shake, container, false);

        // start the service
        sensorService = new SensorService();

        if (!isMyServiceRunning(sensorService.getClass())) {
            getActivity().startService(new Intent(getActivity(), sensorService.getClass()));
        }

        button = view.findViewById(R.id.stopAlarm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AudioPlay.isplayingAudio){ // this will stop the SOS audio after clicking the notification
                    AudioPlay.stopAudio();
                    Toast.makeText(getActivity(), "SOS Stopped", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(AudioPlay.isplayingAudio){ // this will stop the SOS audio after clicking the notification
            AudioPlay.stopAudio();
            Toast.makeText(getActivity(), "SOS Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }


    @Override
    public void onDestroy() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(getContext(), ReactivateService.class);
        getActivity().sendBroadcast(broadcastIntent);
        super.onDestroy();
    }
}