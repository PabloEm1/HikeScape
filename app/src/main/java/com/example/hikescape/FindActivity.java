package com.example.hikescape;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class FindActivity extends AppCompatActivity {

    private EditText searchUserEditText;
    private Button searchUserButton;
    private LinearLayout firstUserLayout;
    private LinearLayout secondUserLayout;
    private Button followUser1Button;
    private Button followUser2Button;
    private TextView titleBuscarRutas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        // Inicializar vistas
        searchUserEditText = findViewById(R.id.searchUser);
        searchUserButton = findViewById(R.id.buttonSearchUser);
        firstUserLayout = findViewById(R.id.firstUser);
        secondUserLayout = findViewById(R.id.secondUser);
        followUser1Button = findViewById(R.id.followUser1);
        followUser2Button = findViewById(R.id.followUser2);
        titleBuscarRutas = findViewById(R.id.titleBuscarRutas);

        // Configurar acción para el botón de búsqueda de usuario
        searchUserButton.setOnClickListener(v -> {
            String query = searchUserEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                searchUser(query);
            }
        });

        // Configurar acción para el botón "Seguir" del primer usuario
        followUser1Button.setOnClickListener(v -> followUser1Button.setText("Siguiendo"));

        // Configurar acción para el botón "Seguir" del segundo usuario
        followUser2Button.setOnClickListener(v -> followUser2Button.setText("Siguiendo"));

        // Menú inferior: configurar navegación entre actividades
        setupBottomMenu();
    }

    private void setupBottomMenu() {
        findViewById(R.id.buttonHome).setOnClickListener(v -> {
            Intent intent = new Intent(FindActivity.this, HomeActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.buttonSettings).setOnClickListener(v -> {
            Intent intent = new Intent(FindActivity.this, RouteActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.buttonProfile).setOnClickListener(v -> {
            Intent intent = new Intent(FindActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.buttonAbout).setOnClickListener(v -> {
            Intent intent = new Intent(FindActivity.this, FavoriteRoutesActivity.class);
            startActivity(intent);
        });

    }

    // Método para realizar la búsqueda de usuarios
    private void searchUser(String query) {
        titleBuscarRutas.setText("Resultados para: " + query);
    }
}
