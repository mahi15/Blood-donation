package com.nullvoid.blooddonation.beans;


/**
 * Created by sanath on 07/06/17.
 */

public class User{
    //A basic container class for holding the user details
    String name, email, password, mobile;

    public User(){}

    public User(String name, String email, String password, String mobile) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "Name = " + name + '\n' +
                ", Email = " + email + '\n' +
                ", Password = " + password + '\n' +
                ", Mobile = " + mobile ;
    }
}