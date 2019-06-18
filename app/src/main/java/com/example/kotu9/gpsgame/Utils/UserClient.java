package com.example.kotu9.gpsgame.Utils;

import android.app.Application;

import com.example.kotu9.gpsgame.Model.User;

public class UserClient extends Application {

    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}