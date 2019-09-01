package com.example.kotu9.gpsgame.activity.administration;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.kotu9.gpsgame.R;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.ac_admin);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        TextView hello = findViewById(R.id.AdminHello);
    }
}
