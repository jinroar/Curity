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
import com.google.firebase.database.FirebaseDatabase;

public class SelectContact extends AppCompatActivity {

    TextView conNameTV1, conNameTV2 ,conNameTV3;
    TextView conNumTV1, conNumTV2, conNumTV3;
    ImageView add1, add2, add3;
    ConstraintLayout contactCL1, contactCL2, contactCL3;
    Button enterButton;

    int box;
    String cName1,cName2,cName3;
    String cNumber1,cNumber2,cNumber3;

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

        //retrieve the saved data
        retrieveSavedContacts();

        enterButton = findViewById(R.id.select_contact);

        //intent for the contacts
        Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        source = getIntent().getStringExtra("Source");

        //Constraints Listener
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

        //permissions
        requestContactsPermission();

//        if(checkSource() == 1){
            //Enter button listener
            //here yung firebase
            enterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Contacts contact = new Contacts(cName1,cName2,cName3,cNumber1,cNumber2,cNumber3);

                    // insert contacts to the firebase realtime database
                    FirebaseDatabase.getInstance().getReference("contacts")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(contact).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(SelectContact.this, "Contacts Input successful!", Toast.LENGTH_SHORT).show();
                                    } else  {
                                        Toast.makeText(SelectContact.this, "Contacts Input Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
//        }

    }

    private void retrieveSavedContacts() {
        FirebaseDatabase.getInstance().getReference("contacts")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                DataSnapshot dataSnapshot = task.getResult();
                                String cName1 = String.valueOf(dataSnapshot.child("contactName1").getValue());
                                String cName2 = String.valueOf(dataSnapshot.child("contactName2").getValue());
                                String cName3 = String.valueOf(dataSnapshot.child("contactName3").getValue());

                                String cNum1 = String.valueOf(dataSnapshot.child("contactNumber1").getValue());
                                String cNum2 = String.valueOf(dataSnapshot.child("contactNumber2").getValue());
                                String cNum3 = String.valueOf(dataSnapshot.child("contactNumber3").getValue());

                                conNameTV1.setText(cName1);
                                conNameTV2.setText(cName2);
                                conNameTV3.setText(cName3);

                                conNumTV1.setText(cNum1);
                                conNumTV2.setText(cNum2);
                                conNumTV3.setText(cNum3);

                                add1.setVisibility(View.INVISIBLE);
                                add2.setVisibility(View.INVISIBLE);
                                add3.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });
    }

    //check where this activity was created
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

    // permissions
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

    //getting data from phone contacts
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

                        //pass values
                        //print values in box
                        switch(box){
                            case 1:
                                conNameTV1.setText(name);
                                conNumTV1.setText(num);
                                add1.setVisibility(View.INVISIBLE);
                                cName1 = name;
                                cNumber1 = num;
                                break;

                            case 2:
                                conNameTV2.setText(name);
                                conNumTV2.setText(num);
                                add2.setVisibility(View.INVISIBLE);
                                cName2 = name;
                                cNumber2 = num;
                                break;

                            case 3:
                                conNameTV3.setText(name);
                                conNumTV3.setText(num);
                                add3.setVisibility(View.INVISIBLE);
                                cName3 = name;
                                cNumber3 = num;
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



