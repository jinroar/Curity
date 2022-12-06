package com.example.curity.MainActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.curity.R;
import com.example.curity.databinding.ActivityHomePageBinding;
import com.example.curity.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class profile extends AppCompatActivity {
    ActivityProfileBinding binding;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Retrieve the data first
        FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()) {
                                DataSnapshot dataSnapshot = task.getResult();
                                String fName = String.valueOf(dataSnapshot.child("firstName").getValue());
                                String lName = String.valueOf(dataSnapshot.child("lastName").getValue());
                                String address = String.valueOf(dataSnapshot.child("address").getValue());
                                String email = String.valueOf(dataSnapshot.child("email").getValue());
                                String phone = String.valueOf(dataSnapshot.child("phone").getValue());

                                binding.edittextFname.setText(fName);
                                binding.edittextEmail.setText(email);

                                // check if the last name is null
                                if (lName.equals("null")){
                                    binding.edittextLname.setText(" ");
                                } else {
                                    binding.edittextLname.setText(lName);
                                }

                                // check if the address is null
                                if (address.equals("null")){
                                    binding.edittextAddress.setText(" ");
                                } else {
                                    binding.edittextAddress.setText(address);
                                }

                                // check if the phone is null
                                if (phone.equals("null")){
                                    binding.edittextPhone.setText(" ");
                                } else {
                                    binding.edittextPhone.setText(phone);
                                }
                            }
                        }
                    }
                });

        // Updating the User profile
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fName = binding.edittextFname.getText().toString();
                String lName = binding.edittextLname.getText().toString();
                String address = binding.edittextAddress.getText().toString();
                String phone = binding.edittextPhone.getText().toString();

                updatedata(fName, lName, address, phone);

            }
        });

    }

    private void updatedata(String fName, String lName, String address, String phone) {

        HashMap User = new HashMap();
        User.put("firstName",fName);
        User.put("lastName",lName);
        User.put("address",address);
        User.put("phone",phone);


        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(User)
                .addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if (task.isSuccessful()){
                    Toast.makeText(profile.this,"Successfully Updated",Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(profile.this,"Failed to Update",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}