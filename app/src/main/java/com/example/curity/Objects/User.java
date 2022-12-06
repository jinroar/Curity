package com.example.curity.Objects;

public class User {
    public String firstName, lastName, address, phone, email, isAdmin;

    public User(String fname, String lname, String address, String phone, String email, String isAdmin) {
        this.firstName = fname;
        this.lastName = lname;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public User(String fname, String lname, String email, String isAdmin) {
        this.firstName = fname;
        this.lastName = lname;
        this.email = email;
        this.isAdmin = isAdmin;
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
}
