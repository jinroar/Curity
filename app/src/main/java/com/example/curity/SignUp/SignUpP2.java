package com.example.curity.SignUp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.curity.Objects.User;
import com.example.curity.R;
import com.example.curity.login.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class SignUpP2 extends AppCompatActivity {

    private String fullName;
    private EditText emailEditText, passwordEditText, confirmPassEditText;
    private Button nxtBtn, uploadBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore fStore;
    private TextView filePath;
    String fileName;


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

        uploadBtn = findViewById(R.id.button3);
        filePath = (TextView) findViewById(R.id.fileName);
        storage = FirebaseStorage.getInstance();
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

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

                if(fileName == "Filename"){
                    Toast.makeText(SignUpP2.this, "Please Select a Valid ID", Toast.LENGTH_SHORT).show();
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

                                        User users = new User(firstTxt, lastTxt, addressTxt, phoneTxt, emailTxt, "2",imageUri.getLastPathSegment().toString());
                                        //going to put the data inside the Firebase Realtime and Firebase Authentication
                                        uploadPicture();
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

    public Uri imageUri;


    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            imageUri = data.getData();
//            imageView.setImageURI(imageUri);
            Cursor cursor = getContentResolver().query(imageUri, null, null, null, null);
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            filePath.setText(cursor.getString(nameIndex));
            fileName = filePath.getText().toString();
        }
    }

    private void uploadPicture() {
        StorageReference ref = storage.getReference().child("images/"+imageUri.getLastPathSegment());
        UploadTask uploadTask = ref.putFile(imageUri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d("Upload:", " "+exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("Upload:", "Sucessful");
            }
        });
    }


}