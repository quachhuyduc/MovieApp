package com.example.movieapp.models;

public class UserInfo {
    private String userName;
    private String passwords;
    private String email;
    private String phone;

    public UserInfo() {
    }

    public UserInfo(String userName, String passwords, String email, String phone) {
        this.userName = userName;
        this.passwords = passwords;
        this.email = email;
        this.phone = phone;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswords() {
        return passwords;
    }

    public void setPasswords(String passwords) {
        this.passwords = passwords;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
