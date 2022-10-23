package com.example.curity.SignUp;

import android.util.Log;

public class Contacts {

    public String contactName1,contactName2,contactName3;
    public String contactNumber1,contactNumber2,contactNumber3;

    public Contacts(String cName1,String cName2,String cName3,String cNum1,String cNum2,String cNum3 ){
        this.contactName1 = cName1;
        this.contactName2 = cName2;
        this.contactName3 = cName3;
        this.contactNumber1 = cNum1;
        this.contactNumber2 = cNum2;
        this.contactNumber3 = cNum3;

    }

    public void displayAll(){

        Log.d("Contact", contactName1);
        Log.d("Contact", contactName2);
        Log.d("Contact", contactName2);
        Log.d("Contact", contactNumber1);
        Log.d("Contact", contactNumber2);
        Log.d("Contact", contactNumber3);

    }

}





