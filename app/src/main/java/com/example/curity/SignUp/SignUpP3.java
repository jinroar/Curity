package com.example.curity.SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.curity.R;
import com.example.curity.login.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpP3 extends AppCompatActivity {

    private String firstEditText, lastEditText, addressEditText, phoneEditText;
    private String emailEditText, passwordEditText, confirmPassEditText;
    private Button nxtBtn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_p3);

        nxtBtn = findViewById(R.id.button1);


        //get the variables from SignUpP
        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        final String firstTxt = (String) b.get("f_name");
        final String lastTxt = (String) b.get("l_name");
        final String addressTxt = (String) b.get("address");
        final String phoneTxt = (String) b.get("phone");
        final String emailTxt = (String) b.get("email");
        final String passwordTxt = (String) b.get("pass");



        nxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                                        Toast.makeText(SignUpP3.this, "User has been register successfully", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(SignUpP3.this, "Failed to register", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                    Intent intent = new Intent(SignUpP3.this, Login.class);
                                    startActivity(intent);
                                }
                            }
                        });
            }
        });

    }
}