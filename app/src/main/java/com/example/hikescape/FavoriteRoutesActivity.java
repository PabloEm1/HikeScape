package com.example.hikescape;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteRoutesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_routes);

        // Configuración del RecyclerView
        RecyclerView favoriteRoutesRecyclerView = findViewById(R.id.favoriteRoutesRecyclerView);
        favoriteRoutesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lista de rutas favoritas
        List<FavoriteRoute> favoriteRoutes = new ArrayList<>();
        favoriteRoutes.add(new FavoriteRoute("Usuario 1", "Ruta Montaña"));
        favoriteRoutes.add(new FavoriteRoute("Usuario 2", "Ruta Lago"));
        favoriteRoutes.add(new FavoriteRoute("Usuario 3", "Ruta Bosque"));
        favoriteRoutes.add(new FavoriteRoute("Usuario 4", "Ruta Playa"));
        favoriteRoutes.add(new FavoriteRoute("Usuario 5", "Ruta Cañón"));

        // Configuración del adaptador
        FavoriteRouteAdapter adapter = new FavoriteRouteAdapter(favoriteRoutes);
        favoriteRoutesRecyclerView.setAdapter(adapter);

        // Configuración de navegación en el menú inferior
        findViewById(R.id.buttonHome).setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteRoutesActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.buttonProfile).setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteRoutesActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.buttonSettings).setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteRoutesActivity.this, RouteActivity.class);
            startActivity(intent);
        });
    }
}
