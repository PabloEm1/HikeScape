package com.example.hikescape;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Cambiar Button a ImageButton
        ImageButton buttonHome = findViewById(R.id.buttonHome);
        ImageButton buttonProfile = findViewById(R.id.buttonProfile);
        ImageButton buttonSettings = findViewById(R.id.buttonSettings);
        ImageButton buttonAbout = findViewById(R.id.buttonAbout);
        ImageButton buttonLogout = findViewById(R.id.buttonLogout);

        Intent intent1 = new Intent(MenuActivity.this, HomeActivity.class);
        startActivity(intent1);
        finish();
        
        buttonHome.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        buttonProfile.setOnClickListener(v -> {
            // Navegar a Profile
        });

        buttonSettings.setOnClickListener(v -> {
            // Navegar a Settings
        });

        buttonAbout.setOnClickListener(v -> {
            // Navegar a About
        });
    }
}
