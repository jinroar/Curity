package com.example.curity.model;

public class UserModel {
    public String uid = "";
    public String fullname = "";

//    public UserModel() {
//        this.uid = uid;
//        this.fullname = fullname;
//    }

    public UserModel(String uid, String fullname) {
        this.uid = uid;
        this.fullname = fullname;
    }

    public String getUid() {
        return uid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
