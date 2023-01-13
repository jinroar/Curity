package com.example.curity.Objects;

public class User {
    public String firstName, lastName, address, phone, email, isAdmin, date;
    public Boolean userFound;
    public String imgURL;

    public User(String fname, String lname, String address, String phone, String email, String isAdmin, String imgURL) {
        this.firstName = fname;
        this.lastName = lname;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.isAdmin = isAdmin;
        this.imgURL = imgURL;
    }

    public User(String fname, String lname, String address, String phone, String email, String isAdmin) {
        this.firstName = fname;
        this.lastName = lname;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public User(String fname, String lname, String email, String phone, String date) {
        this.firstName = fname;
        this.lastName = lname;
        this.email = email;
        this.phone = phone;
        this.date = date;
    }

    public User() {
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setUserFound(Boolean userFound) {
        this.userFound = userFound;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public Boolean getUserFound() {
        return userFound;
    }

    public String getDate() {
        return date;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
