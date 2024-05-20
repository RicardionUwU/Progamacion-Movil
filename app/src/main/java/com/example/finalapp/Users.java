package com.example.finalapp;

public class Users {

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    private String Uid;
    private String Email;
    private String Password;
    private int userType;


    public Users() {

    }

    public Users(String uid,String email, String password, int userType) {
        Uid = uid;
        Email = email;
        Password = password;
        userType = userType;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }


}
