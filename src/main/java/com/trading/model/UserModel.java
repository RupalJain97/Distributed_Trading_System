package com.trading.model;

public class UserModel {
    private String username;
    private String userid;
    private String password;

    public UserModel(String username, String userid, String password) {
        this.username = username;
        this.userid = userid;
        this.password = password;
    }

    public String getUserid() {
        return userid;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
