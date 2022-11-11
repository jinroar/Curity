package com.example.curity.login;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.curity.R;
import com.google.firebase.auth.FirebaseAuth;


public class LoginBrgy extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    //UI
    TextView signUpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_brgy);

        firebaseAuth = FirebaseAuth.getInstance();

        signUpView = findViewById(R.id.SignUpView);

        signUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }
}
