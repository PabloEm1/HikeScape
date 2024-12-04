package com.example.hikescape;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;  // Agregar para usar los logs
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SavedRoutesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SavedRoutesAdapter adapter;
    private List<SavedRoute> savedRoutesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_routes);

        // Intentar acceder al RecyclerView y loguear su estado
        recyclerView = findViewById(R.id.recyclerViewSavedRoutes);

        // Agregar un log para verificar si el RecyclerView fue encontrado correctamente
        if (recyclerView == null) {
            Log.e("SavedRoutesActivity", "RecyclerView no encontrado.");
        } else {
            Log.d("SavedRoutesActivity", "RecyclerView encontrado.");
        }

        // Verifica si recyclerView es nulo antes de continuar
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Initialize the data
            savedRoutesList = new ArrayList<>();
            loadSavedRoutes();

            // Set up the adapter
            adapter = new SavedRoutesAdapter(savedRoutesList);
            recyclerView.setAdapter(adapter);

            // Set up the bottom menu buttons
            setupBottomMenu();
        } else {
            Log.e("SavedRoutesActivity", "RecyclerView sigue siendo nulo. Verifica tu archivo de layout.");
        }
    }

    private void loadSavedRoutes() {
        // Dummy data for saved routes. Replace this with your real data source.
        savedRoutesList.add(new SavedRoute("Ruta 1"));
        savedRoutesList.add(new SavedRoute("Ruta 2"));
        savedRoutesList.add(new SavedRoute("Ruta 3"));
        savedRoutesList.add(new SavedRoute("Ruta 4"));
    }

    private void setupBottomMenu() {
        // Home button
        ImageButton buttonHome = findViewById(R.id.buttonHome);
        buttonHome.setOnClickListener(v -> {
            Intent intent = new Intent(SavedRoutesActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Profile button
        ImageButton buttonProfile = findViewById(R.id.buttonProfile);
        buttonProfile.setOnClickListener(v -> {
            Intent intent = new Intent(SavedRoutesActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Settings button (if needed)
       /*ImageButton buttonSettings = findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(SavedRoutesActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // Logout button
        ImageButton buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(v -> {
            // Implement logout logic, such as clearing user session
            finish();
        });*/
    }
}
