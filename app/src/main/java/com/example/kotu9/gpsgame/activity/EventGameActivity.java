package com.example.kotu9.gpsgame.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.kotu9.gpsgame.R;
import com.example.kotu9.gpsgame.model.ClusterMarker;
import com.example.kotu9.gpsgame.model.Event;
import com.example.kotu9.gpsgame.utils.EventTypes;

import java.util.Objects;

public class EventGameActivity extends AppCompatActivity {

    public NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_game);
        setupNavigation();

    }

    private void setupNavigation() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_start_game);
        NavigationUI.setupActionBarWithNavController(this, navController);
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getApplication()));
        builder.setMessage("Do you want to cancel event and back to map ?\n")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent intent = new Intent(getApplicationContext(), UserLocationActivity.class);
                        startActivity(intent);
                    }
                })
                .setNeutralButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        alert.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.WHITE);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.GRAY);
        alert.getButton(AlertDialog.BUTTON_NEUTRAL).setBackgroundColor(Color.GRAY);
    }




}
