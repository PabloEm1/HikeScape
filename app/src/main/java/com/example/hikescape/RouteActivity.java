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


    /*
        buttonProfile.setOnClickListener(v -> {
            // Navegar al perfil del usuario
            Intent intent = new Intent(RouteActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        buttonAbout.setOnClickListener(v -> {
            // Navegar a información sobre la app
            Intent intent = new Intent(RouteActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        buttonLogout.setOnClickListener(v -> {
            // Navegar a LoginActivity (cerrar sesión)
            Intent intent = new Intent(RouteActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Acción para subir fotos
        uploadPhotosButton.setOnClickListener(v -> {
            // Aquí puedes implementar funcionalidad para subir fotos
            // Ejemplo: abrir un selector de imágenes
        });


     */
    }
}
