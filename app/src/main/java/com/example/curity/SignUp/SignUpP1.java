package com.example.curity.SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.curity.login.Login;
import com.example.curity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpP1 extends AppCompatActivity {

    private EditText firstEditText, lastEditText, addressEditText, phoneEditText;
    private EditText emailEditText, passwordEditText, confirmPassEditText;
    private Button register;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_p1);

        firstEditText = findViewById(R.id.f_nameEt);
        lastEditText = findViewById(R.id.l_nameEt);
        addressEditText = findViewById(R.id.addressEt);
        emailEditText = findViewById(R.id.emailEt);
        passwordEditText = findViewById(R.id.passET);
        confirmPassEditText = findViewById(R.id.confirmPassEt);
        phoneEditText = findViewById(R.id.phoneEt);

        register = findViewById(R.id.button1);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String firstTxt = firstEditText.getText().toString();
                final String lastTxt = lastEditText.getText().toString();
                final String addressTxt = addressEditText.getText().toString();
                final String phoneTxt = phoneEditText.getText().toString();
                final String emailTxt = emailEditText.getText().toString();
                final String passwordTxt = passwordEditText.getText().toString();
                final String confirmPassTxt = confirmPassEditText.getText().toString();

                //check if the user fill up all of the field(s)
                if (firstTxt.isEmpty() || lastTxt.isEmpty() || addressTxt.isEmpty() || phoneTxt.isEmpty() ||
                emailTxt.isEmpty() || passwordTxt.isEmpty() || confirmPassTxt.isEmpty()){
                    Toast.makeText(SignUpP1.this, "Please fill Up the empty field(s)", Toast.LENGTH_SHORT).show();
                }

                // check if the password is not match
                else if (!passwordTxt.equals(confirmPassTxt)){
                    Toast.makeText(SignUpP1.this, "Password is not matched", Toast.LENGTH_SHORT).show();
                }

                else {
                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.createUserWithEmailAndPassword(emailTxt,passwordTxt)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                User users = new User(firstTxt, lastTxt, addressTxt, phoneTxt, emailTxt);

                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(SignUpP1.this, "User has been register successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(SignUpP1.this, "Failed to register", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                Intent intent = new Intent(SignUpP1.this, Login.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }
}