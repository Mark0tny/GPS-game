package com.example.kotu9.gpsgame.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.kotu9.gpsgame.Fragment.EventCreation.CreateEventInfo;
import com.example.kotu9.gpsgame.R;

public class CreateEventActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, CreateEventInfo.SpinnerListener {

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
        navView = findViewById(R.id.nav_view_bottom);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController);
        NavigationUI.setupWithNavController(navView, navController);
        navView.setOnNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_step1:
                navController.navigate(R.id.createEventInfo);
                break;
            case R.id.navigation_step2:
                switch (spinnerPosition) {
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
                navView.getMenu().getItem(menuItem.getOrder() + 1).setEnabled(true);
                break;
            case R.id.navigation_step3:
                navView.getMenu().getItem(menuItem.getOrder() + 1).setEnabled(true);
                navView.getMenu().getItem(menuItem.getOrder()).setChecked(true);
                navController.navigate(R.id.createEventHints);
                break;
            case R.id.navigation_step4:
                navController.navigate(R.id.createEventMarker);
                navView.getMenu().setGroupEnabled(menuItem.getGroupId(), true);
                break;
        }
        return true;

    }

    @Override
    public void onSpinnerEventSelected(Integer position) {
        if (position != 0) spinnerPosition = position;
        switch (position) {
            case 1:
                navController.navigate(R.id.createEventLocation);
                navView.getMenu().getItem(1).setEnabled(true);
                navView.setSelectedItemId(R.id.navigation_step2);
                break;
            case 2:
                navController.navigate(R.id.createEventQRcode);
                navView.getMenu().getItem(1).setEnabled(true);
                navView.setSelectedItemId(R.id.navigation_step2);
                break;
            case 3:
                navController.navigate(R.id.createEventQuiz);
                navView.getMenu().getItem(1).setEnabled(true);
                navView.setSelectedItemId(R.id.navigation_step2);
                break;
            case 4:
                navController.navigate(R.id.createEventPhotoCompare);
                navView.getMenu().getItem(1).setEnabled(true);
                navView.setSelectedItemId(R.id.navigation_step2);
                break;
        }
    }


}
