package com.example.curity.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.curity.AdminPage.HomePageBrgy;
import com.example.curity.MainActivity.HomePage;
import com.example.curity.R;
import com.example.curity.SignUp.SignUpP1;
import com.example.curity.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.SignUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUpP1.class);
                startActivity(intent);
            }
        });

        binding.forgetTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, forgetPassword.class);
                startActivity(intent);
            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = binding.emailEt.getText().toString();
                final String pass = binding.passET.getText().toString();

                if (!(email.isEmpty() && pass.isEmpty())){
                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(Login.this,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                checkUserAccessLevel(firebaseAuth.getCurrentUser().getUid());
                            } else {
                                try{

                                }catch(Exception e){
                                    Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(Login.this, "Please fill up the empty field(s)", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void checkUserAccessLevel(String uid) {
        FirebaseDatabase.getInstance().getReference("users")
                .child(uid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        // Identify the Access level
                        // 1 - Brgy
                        // 2 - User

                        String val = String.valueOf(dataSnapshot.child("isAdmin").getValue());

                        if (val.equals("1")) {
                            startActivity(new Intent(Login.this, HomePageBrgy.class));
                            finish();
                        } else if (val.equals("2")) {
                            startActivity(new Intent(Login.this, HomePage.class));
                            finish();
                        }
                    }
                });
    }

    public void checkNetworkConnectionStatus(){
        boolean wifiConnected;
        boolean mobileConnected;

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();

        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;

            if(wifiConnected || mobileConnected){ // wifi or data connected
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    checkUserAccessLevel(FirebaseAuth.getInstance().getCurrentUser().getUid());
                }
            }
        } else { // no internet connection
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            View layout_dialog = LayoutInflater.from(this).inflate(R.layout.internet_dialog, null);
            builder.setView(layout_dialog);

            AppCompatButton btnRetry = layout_dialog.findViewById(R.id.btnRe);

            //Show dialog
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);

            dialog.getWindow().setGravity(Gravity.CENTER);

            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    checkNetworkConnectionStatus();
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        checkNetworkConnectionStatus();
    }
}
