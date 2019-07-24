package com.example.kotu9.gpsgame.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.kotu9.gpsgame.Fragment.EventCreation.CreateEventInfo;
import com.example.kotu9.gpsgame.R;

public class CreateEventActivity extends AppCompatActivity implements CreateEventInfo.SpinnerListener {

    public NavController navController;
    public BottomNavigationView navView;
    public static int spinnerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        setupNavigation();
    }
    private void setupNavigation() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController);

    }
    @Override
    public void onSpinnerEventSelected(Integer position) {
        if (position != 0) spinnerPosition = position;
        switch (position) {
            case 1:
                navController.navigate(R.id.createEventLocation);
                break;
            case 2:
                navController.navigate(R.id.createEventQRcode);
                break;
            case 3:
                navController.navigate(R.id.createEventQuiz);
                break;
            case 4:
                navController.navigate(R.id.createEventPhotoCompare);
                break;
        }
    }


}
