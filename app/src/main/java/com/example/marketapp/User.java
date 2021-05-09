package com.example.marketapp;

import android.app.Application;

public class User extends Application {

    private String userName;
    private String userToken;
    private String userAddress;

    public String getUserAddress() {
        return userAddress;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    private static User instance = null;

    public static synchronized User getInstance() {

        if (null == instance) {

            instance = new User();
        }
        return instance;
    }
}
