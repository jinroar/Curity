package com.example.curity.UserPage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.curity.R;
import com.skyfishjy.library.RippleBackground;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserButtonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserButtonFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RippleBackground rippleBackground;
    ImageView button;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserButtonFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment firstFragmentNew.
     */
    // TODO: Rename and change types and number of parameters
    public static UserButtonFragment newInstance(String param1, String param2) {
        UserButtonFragment fragment = new UserButtonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_button, container, false);

//        Button button = view.findViewById(R.id.button2);
//        button.setOnClickListener(this);

        rippleBackground = view.findViewById(R.id.content);
        button = view.findViewById(R.id.button2);
        button.setOnClickListener(this);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!rippleBackground.isRippleAnimationRunning()) {
//                    button.setColorFilter(Color.argb(255, 255, 255, 255)); //change the logo color while staring animation
//                    rippleBackground.startRippleAnimation(); //starting the animation
//                } else {
//                    button.setColorFilter(null); //get back to previous logo color while stopping animation
//                    rippleBackground.stopRippleAnimation(); //stopping the animation
//                }
//            }
//        });

        return view;
    }

    //Ask Question for 5 Seconds
    @Override
    public void onClick(View v) {

        if (!rippleBackground.isRippleAnimationRunning()) {
                    button.setColorFilter(Color.argb(255, 255, 255, 255)); //change the logo color while staring animation
                    rippleBackground.startRippleAnimation(); //starting the animation
        } else {
                    button.setColorFilter(null); //get back to previous logo color while stopping animation
                    rippleBackground.stopRippleAnimation(); //stopping the animation
        }

        AlertDialog dialog = new AlertDialog.Builder(UserButtonFragment.this.getContext())
                .setTitle("Alert")
                .setMessage("Are you OK?")
                .setPositiveButton("yes", null)
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startIntent();
                    }
                })
                .create();

        //Countdown Timer on Dialog
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button noButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                new CountDownTimer(5000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        noButton.setText("No (" + ((millisUntilFinished / 1000) + 1) + ")");
                    }

                    @Override
                    public void onFinish() {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                            startIntent();
                        }
                    }
                }.start();
            }
        });
        dialog.show();
    }

    private void startIntent(){
//        Intent intent = new Intent(firstFragment.this.requireContext(), userMapsActivity.class);
        Intent intent = new Intent(UserButtonFragment.this.requireContext(),LoadingScreen.class);
        startActivity(intent);
    }
}