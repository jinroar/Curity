package com.example.curity.Objects;

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

    public String getContactName1() {
        return contactName1;
    }

    public void setContactName1(String contactName1) {
        this.contactName1 = contactName1;
    }

    public String getContactNumber1() {
        return contactNumber1;
    }

    public void setContactNumber1(String contactNumber1) {
        this.contactNumber1 = contactNumber1;
    }

    //contact person second
    public String getContactName2() {
        return contactName2;
    }

    public void setContactName2(String contactName2) {
        this.contactName2 = contactName2;
    }

    public String getContactNumber2() {
        return contactNumber2;
    }

    public void setContactNumber2(String contactNumber2) {
        this.contactNumber2 = contactNumber2;
    }

    //contact person third
    public String getContactName3() {
        return contactName3;
    }

    public void setContactName3(String contactName3) {
        this.contactName3 = contactName3;
    }

    public String getContactNumber3() {
        return contactNumber3;
    }

    public void setContactNumber3(String contactNumber3) {
        this.contactNumber3 = contactNumber3;
    }
}





