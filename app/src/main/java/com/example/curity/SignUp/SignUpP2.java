package com.example.curity.SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.curity.R;
import com.example.curity.login.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpP2 extends AppCompatActivity {

    private String fullName;
    private EditText emailEditText, passwordEditText, confirmPassEditText;
    private Button nxtBtn;
    private FirebaseAuth firebaseAuth;

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

                if (emailTxt.isEmpty() || passwordTxt.isEmpty() || confirmPassTxt.isEmpty()){
                    Toast.makeText(SignUpP2.this, "Please fill Up the empty field(s)", Toast.LENGTH_SHORT).show();
                }

                // check if the password is not match
                else if (!passwordTxt.equals(confirmPassTxt)){
                    Toast.makeText(SignUpP2.this, "Password is not matched", Toast.LENGTH_SHORT).show();
                }

                else {
                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.createUserWithEmailAndPassword(emailTxt,passwordTxt)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){

                                        User users = new User(firstTxt, lastTxt, addressTxt, phoneTxt, emailTxt);

                                        //going to put the data inside the Firebase Realtime and Firebase Authentication
                                        FirebaseDatabase.getInstance().getReference("users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Toast.makeText(SignUpP2.this, "User has been registered successfully!", Toast.LENGTH_SHORT).show();

                                                            //for display name (full name)
                                                            fullName = firstTxt + " " + lastTxt;
                                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                    .setDisplayName(fullName).build();

                                                            //check if it was updated in the firebase authentication
                                                            user.updateProfile(profileUpdates)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                Log.d("ACTIVITY SIGNUP P2", "User profile updated.");

                                                                                // go to login Page
                                                                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                                                                startActivity(intent);
                                                                                finish();

                                                                            }
                                                                        }
                                                                    });
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