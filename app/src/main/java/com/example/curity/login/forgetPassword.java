package com.example.curity.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.curity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class forgetPassword extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPassEditText;
    private Button resetPassBtn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);


        emailEditText = findViewById(R.id.emailEt);
        passwordEditText = findViewById(R.id.passET);
        confirmPassEditText = findViewById(R.id.confirmPassEt);
        resetPassBtn = findViewById(R.id.resetPassBtn);

        firebaseAuth = FirebaseAuth.getInstance();

        resetPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    private void resetPassword(){
        final String email = emailEditText.getText().toString();

        // check if the email field is empty
        if (email.isEmpty()){
            emailEditText.setError("Email is require.");
            emailEditText.requestFocus();
            return;
        }

        // check if the emails was recorded in the database
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Please provide the valid email.");
            emailEditText.requestFocus();
            return;
        }


        // will send a link through email
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(forgetPassword.this, "Check your email", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(forgetPassword.this, "try again :<<", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}