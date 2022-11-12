package com.example.curity.SignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.curity.R;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpP1 extends AppCompatActivity {

    private EditText firstEditText, lastEditText, addressEditText, phoneEditText;
    private Button nxtBtn;
    private FirebaseAuth firebaseAuth;
    private Boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_p1);

        firstEditText = findViewById(R.id.f_nameEt);
        lastEditText = findViewById(R.id.l_nameEt);
        addressEditText = findViewById(R.id.addressEt);
        phoneEditText = findViewById(R.id.phoneEt);

        nxtBtn = findViewById(R.id.button1);

        nxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String firstTxt = firstEditText.getText().toString();
                final String lastTxt = lastEditText.getText().toString();
                final String addressTxt = addressEditText.getText().toString();
                final String phoneTxt = phoneEditText.getText().toString();

                if (firstTxt.isEmpty()){
                    firstEditText.setError("First name is required");
                    firstEditText.requestFocus();
                }

                if (lastTxt.isEmpty()){
                    lastEditText.setError("last name is required");
                    lastEditText.requestFocus();
                }

                if (addressTxt.isEmpty()){
                    addressEditText.setError("Address is required");
                    addressEditText.requestFocus();
                }

                if (phoneTxt.isEmpty()){
                    phoneEditText.setError("Cellphone number is required");
                    phoneEditText.requestFocus();
                }

                if (phoneTxt.length() != 11){
                    phoneEditText.setError("Enter the 11 digits of your cellphone number.");
                    phoneEditText.requestFocus();
                }

                if (!(firstTxt.isEmpty() || lastTxt.isEmpty() || addressTxt.isEmpty() || phoneTxt.isEmpty()) && phoneTxt.length() == 11) {
                    //pass the data to the next page
                    Intent intent = new Intent(getApplicationContext(), SignUpP2.class);
                    intent.putExtra("f_name", firstTxt);
                    intent.putExtra("l_name", lastTxt);
                    intent.putExtra("address", addressTxt);
                    intent.putExtra("phone", phoneTxt);

                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}