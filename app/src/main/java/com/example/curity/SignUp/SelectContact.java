package com.example.curity.SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.curity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

public class SelectContact extends AppCompatActivity {

    TextView conNameTV1, conNameTV2 ,conNameTV3;
    TextView conNumTV1, conNumTV2, conNumTV3;
    ImageView add1, add2, add3;
    ConstraintLayout contactCL1, contactCL2, contactCL3;
    Button enterButton;

    int box;
    String contactName1,contactName2,contactName3;
    String contactNumber1,contactNumber2,contactNumber3;
//
    public FirebaseDatabase firebaseDatabase;
    public DatabaseReference databaseReference;

    private String source;

    private static final int REQUEST_READ_CONTACTS_PERMISSION = 0;
    private static final int REQUEST_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        conNameTV1 = findViewById(R.id.contactName1);
        conNameTV2 = findViewById(R.id.contactName2);
        conNameTV3 = findViewById(R.id.contactName3);

        conNumTV1 = findViewById(R.id.contactNumber1);
        conNumTV2 = findViewById(R.id.contactNumber2);
        conNumTV3 = findViewById(R.id.contactNumber3);

        add1 = findViewById(R.id.plus1);
        add2 = findViewById(R.id.plus2);
        add3 = findViewById(R.id.plus3);

        contactCL1 = findViewById(R.id.contact1);
        contactCL2 = findViewById(R.id.contact2);
        contactCL3 = findViewById(R.id.contact3);

        enterButton = findViewById(R.id.select_contact);

        Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        source = getIntent().getStringExtra("Source");

        contactCL1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(pickContact, REQUEST_CONTACT);
                box = 1;
            }
        });

        contactCL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(pickContact, REQUEST_CONTACT);
                box = 2;
            }
        });

        contactCL3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(pickContact, REQUEST_CONTACT);
                box = 3;
            }
        });

        requestContactsPermission();

//        if(checkSource() == 1){
            enterButton.setOnClickListener(new View.OnClickListener() {
                Contacts contacts = new Contacts(contactName1,contactName2,contactName3,contactNumber1,contactNumber2,contactNumber3);

                @Override
                public void onClick(View v) {
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseReference = firebaseDatabase.getReference("contacts")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            databaseReference.setValue(contacts);
                            Toast.makeText(SelectContact.this, "Contacts input successful", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


//                    FirebaseDatabase.getInstance()
//                            .getReference("contacts")
//                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                            .setValue(contacts).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()){
//                                        Toast.makeText(SelectContact.this, "Contacts input successful", Toast.LENGTH_SHORT).show();
//                                        finish();
//
//                                    } else {
//                                        Toast.makeText(SelectContact.this, "Contacts input unsuccessful", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
                }
            });
//        }

    }

    public int checkSource() {
        int sourceInt;
        switch (source){
            case "from Homepage":
                sourceInt = 1;
                break;
            case "from SignUp 2":
                sourceInt = 2;
                break;
            default:
                sourceInt = 0;
        }
        return sourceInt;
    }


    private void requestContactsPermission() {
        if (!hasContactsPermission())
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS_PERMISSION);
        }
    }

    private boolean hasContactsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_CONTACT && data != null)
        {
            Uri contactUri = data.getData();

            // Specify which fields you want your
            // query to return values for
            String[] queryFields = new String[]{
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER
            };

            // Perform your query - the contactUri
            // is like a "where" clause here
            Cursor cursor1 = this.getContentResolver()
                    .query(contactUri, queryFields, null, null, null);
            try
            {
                // Double-check that you
                // actually got results
                if (cursor1.getCount() == 0) return;

                // Pull out the first column
                // of the first row of data
                // that is your contact's name
                cursor1.moveToFirst();

                String id = cursor1.getString(0);
                String name = cursor1.getString(1);
                String hasNumber = cursor1.getString(2);
                int numResult = Integer.parseInt(hasNumber);

                if(numResult == 1){
                    Cursor cursor2 = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = "+id,
                            null,
                            null);

                    while(cursor2.moveToNext()){
                        String num = cursor2.getString(cursor2.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        switch(box){
                            case 1:
                                conNameTV1.setText(name);
                                conNumTV1.setText(num);
                                add1.setVisibility(View.INVISIBLE);
                                contactName1 = name;
                                contactNumber1 = num;
                                break;
                            case 2:

                                conNameTV2.setText(name);
                                conNumTV2.setText(num);
                                add2.setVisibility(View.INVISIBLE);
                                contactName2 = name;
                                contactNumber2 = num;
                                break;
                            case 3:

                                conNameTV3.setText(name);
                                conNumTV3.setText(num);
                                add3.setVisibility(View.INVISIBLE);
                                contactName3 = name;
                                contactNumber3 = num;
                                break;

                        }
                    }
                }

            }
            finally
            {
                cursor1.close();
            }
        }
    }

}



