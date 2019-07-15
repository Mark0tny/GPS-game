package com.example.kotu9.gpsgame.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.NavHost;

import com.example.kotu9.gpsgame.R;

public class CreateEventActivity extends AppCompatActivity implements NavHost {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
    }

    @NonNull
    @Override
    public NavController getNavController() {
        return null;
    }
}
