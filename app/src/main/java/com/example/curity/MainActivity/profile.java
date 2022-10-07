package com.example.curity.MainActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.curity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class profile extends AppCompatActivity {

    EditText fname, lname, homeaddress, emailadd, phonenum;
    Button btn_update, btn_logout;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fname = findViewById(R.id.edittext_fname);
        lname = findViewById(R.id.edittext_lname);
        homeaddress = findViewById(R.id.edittext_address);
        emailadd = findViewById(R.id.edittext_email);
        phonenum = findViewById(R.id.edittext_phone);
        btn_update = findViewById(R.id.btn_update);
        btn_logout = findViewById(R.id.btn_logout);

        btn_update.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                updateProfile();
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        /*String currentuid = user.getUid();*/

        documentReference.collection("users").document(currentuid);
        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){

                            String fnameResult = task.getResult().getString("firstName");
                            String lnameResult = task.getResult().getString("lastName");
                            String addressResult = task.getResult().getString("address");
                            String emailResult = task.getResult().getString("email");
                            String phoneResult = task.getResult().getString("phone");

                            fname.setText(fnameResult);
                            lname.setText(lnameResult);
                            homeaddress.setText(addressResult);
                            emailadd.setText(emailResult);
                            phonenum.setText(phoneResult);

                        }else {
                            Toast.makeText(profile.this, "No profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateProfile() {

        String firstName = fname.getText().toString();
        String lastName = lname.getText().toString();
        String address = homeaddress.getText().toString();
        String email = emailadd.getText().toString();
        String phone = phonenum.getText().toString();


        final DocumentReference sDoc = db.collection("users").document(currentuid);

        db.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        //DocumentSnapshot snapshot = transaction.get(sfDocRef);

                        transaction.update(sDoc, "firstName", fname);
                        transaction.update(sDoc, "lastName", lname);
                        transaction.update(sDoc, "address", homeaddress);
                        transaction.update(sDoc, "email", emailadd);
                        transaction.update(sDoc, "phone", phonenum);

                        // Success
                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(profile.this, "updated", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(profile.this, "failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}