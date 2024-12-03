package com.example.hikescape;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Configurar el RecyclerView para mostrar publicaciones
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Crear una lista de publicaciones
        List<Post> postList = new ArrayList<>();
        postList.add(new Post(1,"Nombre de Usuario 1", R.drawable.image1,0));
        postList.add(new Post(2,"Nombre de Usuario 2", R.drawable.image2,0));
        postList.add(new Post(3,"Nombre de Usuario 3", R.drawable.image3,0));

        // Configurar el adaptador del RecyclerView
        PostAdapter adapter = new PostAdapter(postList);
        recyclerView.setAdapter(adapter);

        // Menú inferior: navegar a otras actividades según los clics en los botones
        findViewById(R.id.buttonHome).setOnClickListener(v -> {
            // Home ya está activo, no se realiza ninguna acción

        });
/*
        findViewById(R.id.buttonStar).setOnClickListener(v -> {
            // Ejemplo: Navegar a una actividad de favoritos
            Intent intent = new Intent(HomeActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.buttonAdd).setOnClickListener(v -> {
            // Ejemplo: Navegar a una actividad para agregar contenido
            Intent intent = new Intent(HomeActivity.this, AddContentActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.buttonSettings).setOnClickListener(v -> {
            // Ejemplo: Navegar a la configuración
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
*/
        findViewById(R.id.buttonProfile).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });


    }


}
