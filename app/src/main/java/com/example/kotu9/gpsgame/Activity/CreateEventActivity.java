package com.example.kotu9.gpsgame.Activity;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.NavHost;

import com.example.kotu9.gpsgame.Fragment.EventCreation.CreateEventInfo;
import com.example.kotu9.gpsgame.R;

public class CreateEventActivity extends AppCompatActivity implements CreateEventInfo.OnFragmentInteractionListener, NavHost {

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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
