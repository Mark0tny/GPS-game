package com.example.kotu9.gpsgame.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.kotu9.gpsgame.R;

import static java.lang.String.valueOf;

public class EventGameActivity extends AppCompatActivity {

    public NavController navController;
    Bundle bundleFromLoc = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.ac_start_game);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_game);
        setupNavigation();
        bundleFromLoc = getIntent().getExtras();
        Bundle bundleToGame = new Bundle();
        bundleToGame.putBundle(valueOf(R.string.markerBundleGame), bundleFromLoc);
        navController.navigate(R.id.startGameFragment, bundleToGame);

    }

    private void setupNavigation() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_start_game);
        NavigationUI.setupActionBarWithNavController(this, navController);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, UserLocationActivity.class);
        startActivity(intent);
    }

}
