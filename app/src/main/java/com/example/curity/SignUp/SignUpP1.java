package com.example.curity.SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.curity.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpP1 extends AppCompatActivity {

    private EditText firstEditText, lastEditText, addressEditText, phoneEditText;
    private EditText emailEditText, passwordEditText, confirmPassEditText;
    private Button register;


    // create the object of DataReference class to access firebase's Realtime Database
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://curity-2247d-default-rtdb.firebaseio.com/").getReference();

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

                //get data from the EditText to Strings
                final String firstTxt = firstEditText.getText().toString();
                final String lastTxt = lastEditText.getText().toString();
                final String addressTxt = addressEditText.getText().toString();
                final String phoneTxt = phoneEditText.getText().toString();
                final String emailTxt = emailEditText.getText().toString();
                final String passwordTxt = passwordEditText.getText().toString();
                final String confirmPassTxt = confirmPassEditText.getText().toString();

                // check if the user fill up all of the fields
                if (firstTxt.isEmpty() || lastTxt.isEmpty() || addressTxt.isEmpty() || emailTxt.isEmpty() ||
                        passwordTxt.isEmpty() || confirmPassTxt.isEmpty()){
                    Toast.makeText(SignUpP1.this, "Please fill Up the empty field(s)", Toast.LENGTH_SHORT).show();
                }
                // check if the password are match
                else if (! passwordTxt.equals(confirmPassTxt)) {
                    Toast.makeText(SignUpP1.this, "Password is not matched", Toast.LENGTH_SHORT).show();
                }

                else {
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // check if the email was not registered (before)
                            if (snapshot.hasChild(phoneTxt)){
                                Toast.makeText(SignUpP1.this, "Phone already registered", Toast.LENGTH_SHORT).show();
                            }

                            else {
                                // will send the data to the firebase realtime database
                                // unique identifier is the email for every user
                                // the other details of the user comes under email
                                databaseReference.child("users").child(phoneTxt).child("firstName").setValue(firstTxt);
                                databaseReference.child("users").child(phoneTxt).child("lastName").setValue(lastTxt);
                                databaseReference.child("users").child(phoneTxt).child("address").setValue(addressTxt);
                                databaseReference.child("users").child(phoneTxt).child("email").setValue(emailTxt);
                                databaseReference.child("users").child(phoneTxt).child("password").setValue(passwordTxt);

                                // shows a success message
                                Toast.makeText(SignUpP1.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
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
