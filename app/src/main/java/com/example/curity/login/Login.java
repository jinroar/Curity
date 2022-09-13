package com.example.curity.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.curity.MainActivity.HomePage;
import com.example.curity.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private EditText phoneEditText, passwordEditText;
    private Button login;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://curity-2247d-default-rtdb.firebaseio.com/").getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneEditText = findViewById(R.id.phoneEt);
        passwordEditText = findViewById(R.id.passET);
        login = findViewById(R.id.button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String phoneTxt = phoneEditText.getText().toString();
                final String passwordTxt = passwordEditText.getText().toString();

                if (phoneTxt.isEmpty() || passwordTxt.isEmpty()) {
                    Toast.makeText(Login.this, "Please fill Up the phone or password", Toast.LENGTH_SHORT).show();
                }

                else {
                 databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                         // check if the phone number exist in the firebase
                         if(snapshot.hasChild(phoneTxt)){

                             // phone exist in the firebase
                             // getting the password from firebase and match with user entered
                             final String getPassword = snapshot.child(phoneTxt).child("password").getValue(String.class);

                             if (getPassword != null && getPassword.equals(passwordTxt)) {
                                 Toast.makeText(Login.this, "Successfully", Toast.LENGTH_SHORT).show();
                                 Intent intent = new Intent(Login.this, HomePage.class);
                                 startActivity(intent);

                             } else {
                                 Toast.makeText(Login.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                             }
                         }
                         else{
                             Toast.makeText(Login.this, "Wrong Phone number", Toast.LENGTH_SHORT).show();
                         }
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError error) {

                     }
                 });
                }
            }
        });

    }
}