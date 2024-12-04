package com.example.hikescape;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        // Configurar el RecyclerView del perfil
        RecyclerView recyclerView = findViewById(R.id.recyclerViewProfile);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columnas

        // Lista de publicaciones de ejemplo
        List<Post> postList = new ArrayList<>();
        postList.add(new Post(1,"Publicación 1", R.drawable.image1,0));
        postList.add(new Post(2,"Publicación 2", R.drawable.image2,0));
        postList.add(new Post(3,"Publicación 3", R.drawable.image3,0));

        // Configurar el adaptador
        PostAdapter adapter = new PostAdapter(postList);
        recyclerView.setAdapter(adapter);
        // Menú inferior: navegar a otras actividades según los clics en los botones
        findViewById(R.id.buttonHome).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.buttonRuta).setOnClickListener(v -> {
            // Navegar a la actividad "Crear Ruta" (RouteActivity)
            Intent intent = new Intent(ProfileActivity.this, RouteActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.buttonRutafav).setOnClickListener(v -> {
            // Navegar a la actividad "Crear Ruta" (RouteActivity)
            Intent intent = new Intent(ProfileActivity.this, FavoriteRoutesActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.buttonBuscar).setOnClickListener(v -> {
            // Navegar a la actividad "Crear Ruta" (RouteActivity)
            Intent intent = new Intent(ProfileActivity.this, FindActivity.class);
            startActivity(intent);
        });


    }
}
