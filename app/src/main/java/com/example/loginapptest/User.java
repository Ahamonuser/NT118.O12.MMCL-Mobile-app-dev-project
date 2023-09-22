package com.example.loginapptest;

public class User {
    public String username;
    public String password;
    public String fullname;
    public User()
    {

    }
    public User(String name, String pass, String fname) {
        this.username = name;
        this.password = pass;
        this.fullname = fname;
    }
}
