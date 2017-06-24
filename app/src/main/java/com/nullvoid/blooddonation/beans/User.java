package com.nullvoid.blooddonation.beans;


/**
 * Created by sanath on 07/06/17.
 */

public class User{
    //A basic container class for holding the user details
    private String name, email, password, mobile;
    private boolean isAdmin = false;
    private int registrationCount = 0;

    public User(){}

    public User(String name, String email, String password, String mobile) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
        this.isAdmin = false;
    }

    public int getRegistrationCount() {
        return registrationCount;
    }

    public void setRegistrationCount(int registrationCount) {
        this.registrationCount = registrationCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

}