package com.example.curity.SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.curity.Objects.User;
import com.example.curity.R;
import com.example.curity.login.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignUpP2 extends AppCompatActivity {

    private String fullName;
    private EditText emailEditText, passwordEditText, confirmPassEditText;
    private Button nxtBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_p2);


        emailEditText = findViewById(R.id.emailEt);
        passwordEditText = findViewById(R.id.passET);
        confirmPassEditText = findViewById(R.id.confirmPassEt);


        //get the variables from SignUpP1
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        final String firstTxt = (String) b.get("f_name");
        final String lastTxt = (String) b.get("l_name");
        final String addressTxt = (String) b.get("address");
        final String phoneTxt = (String) b.get("phone");


        nxtBtn = findViewById(R.id.button1);
        nxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailTxt = emailEditText.getText().toString();
                final String passwordTxt = passwordEditText.getText().toString();
                final String confirmPassTxt = confirmPassEditText.getText().toString();

                if (emailTxt.isEmpty()){
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();
                }

                // check if the password box is empty
                if (passwordTxt.isEmpty() && confirmPassTxt.isEmpty()){
                    passwordEditText.setError("password is required");
                    passwordEditText.requestFocus();

                    confirmPassEditText.setError("password is required");
                    confirmPassEditText.requestFocus();
                }

                // check email valid
                if (!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()){
                    emailEditText.setError("Please provide valid email");
                    emailEditText.requestFocus();
                }

                // check if the length of the password more than 6
                if (passwordTxt.length() < 6){
                    passwordEditText.setError("Minimum password length should be 6 characters.");
                    passwordEditText.requestFocus();
                }

                // check if the password is not match
                if (!passwordTxt.equals(confirmPassTxt)){
                    Toast.makeText(SignUpP2.this, "Password is not matched", Toast.LENGTH_SHORT).show();
                }

                if (!(emailTxt.isEmpty() || passwordTxt.isEmpty() || confirmPassTxt.isEmpty()) && passwordTxt.equals(confirmPassTxt) ) {
                    firebaseAuth = FirebaseAuth.getInstance();
                    fStore = FirebaseFirestore.getInstance();

                    firebaseAuth.createUserWithEmailAndPassword(emailTxt, passwordTxt)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Identify the Access level
                                        // 1 - Brgy
                                        // 2 - User

                                        User users = new User(firstTxt, lastTxt, addressTxt, phoneTxt, emailTxt, "2");
                                        //going to put the data inside the Firebase Realtime and Firebase Authentication
                                        FirebaseDatabase.getInstance().getReference("users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(SignUpP2.this, "User has been registered successfully!", Toast.LENGTH_SHORT).show();

                                                            // go to login Page
                                                            Intent intent = new Intent(getApplicationContext(), Login.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Toast.makeText(SignUpP2.this, "Failed to register! Please Try Again", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                }
            }
        });
    }
}