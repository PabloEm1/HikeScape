package com.example.hikescape;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RouteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route); // Asegúrate de que el archivo XML se llame activity_route.xml

        findViewById(R.id.buttonHome).setOnClickListener(v -> {
            // Navegar a la actividad "Crear Ruta" (RouteActivity)
            Intent intent = new Intent(RouteActivity.this, HomeActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.buttonProfile).setOnClickListener(v -> {
            Intent intent = new Intent(RouteActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.buttonAbout).setOnClickListener(v -> {
            // Navegar a la actividad "Crear Ruta" (RouteActivity)
            Intent intent = new Intent(RouteActivity.this, FavoriteRoutesActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.buttonLogout).setOnClickListener(v -> {
            // Navegar a la actividad "Crear Ruta" (RouteActivity)
            Intent intent = new Intent(RouteActivity.this, FindActivity.class);
            startActivity(intent);
        });

    }
}
