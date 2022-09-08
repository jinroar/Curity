package com.example.curity.SignUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;

import com.example.curity.R;

public class SignUpP2 extends AppCompatActivity {

    Cursor cursor;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_p2);

        // declaring listView using findViewById()
        listView = findViewById(R.id.ListView);

        // call for the Contacts
        getContacts();
    }

    public void getContacts() {

        // create cursor and query the data
        cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );

        startManagingCursor(cursor);

        // data is a array of String type which is
        // used to store Number and Names.
        String[] data = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        int[] to = {android.R.id.text1, android.R.id.text2};

        // creation of adapter using SimpleCursorAdapter class
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                cursor,
                data,
                to
        );

        // Calling setAdaptor() method to set created adapter
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }
}