package com.example.curity.Objects;

public class UserLogs {
    String userName, email, phoneNumber, date;

    public UserLogs(String userName, String email, String phoneNumber, String date) {
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
