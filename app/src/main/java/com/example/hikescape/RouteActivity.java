package com.example.hikescape;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RouteActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private EditText routeNameEditText;
    private EditText routeDescriptionEditText;
    private Spinner routeDifficultySpinner;
    //private EditText routeLocationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route); // Asegúrate de que el archivo XML se llame activity_route.xml

        // Inicializar las vistas
        routeNameEditText = findViewById(R.id.routeName);
        routeDescriptionEditText = findViewById(R.id.routeLocation); // O usar otro campo si es necesario
        routeDifficultySpinner = findViewById(R.id.routeDifficulty);
        //routeLocationEditText = findViewById(R.id.routeLocation); // Aquí va la ubicación o comentarios

        // Inicializar la base de datos
        databaseHelper = new DatabaseHelper(this);

        // Configurar el botón para crear la ruta
        Button createRouteButton = findViewById(R.id.addRouteButton);
        createRouteButton.setOnClickListener(v -> createRoute());

        findViewById(R.id.buttonHome).setOnClickListener(v -> {
            Intent intent = new Intent(RouteActivity.this, HomeActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.buttonProfile).setOnClickListener(v -> {
            Intent intent = new Intent(RouteActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.buttonRutafav).setOnClickListener(v -> {
            Intent intent = new Intent(RouteActivity.this, FavoriteRoutesActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.buttonBuscar).setOnClickListener(v -> {
            Intent intent = new Intent(RouteActivity.this, FindActivity.class);
            startActivity(intent);
        });
    }

    private void createRoute() {
        // Obtener los valores del formulario
        String routeName = routeNameEditText.getText().toString().trim();
        String routeDescription = routeDescriptionEditText.getText().toString().trim();
        String routeDifficulty = routeDifficultySpinner.getSelectedItem().toString();

        if (routeName.isEmpty() || routeDescription.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
        } else {
            // Asignar un valor para la foto. Por ahora, pongamos un valor fijo o manejarlo según sea necesario
            String routePhoto = "casa"; // Esto es solo un ejemplo

            // Llamar a la función insertRuta para agregar la ruta en la base de datos
            int userId = 1; // Puedes obtener el ID del usuario según tu aplicación
            boolean isInserted = databaseHelper.insertRuta(userId, routeName, routeDescription, routePhoto, routeDifficulty);

            // Comprobar si la ruta fue insertada correctamente
            if (isInserted) {
                Toast.makeText(this, "Ruta creada con éxito", Toast.LENGTH_SHORT).show();
                // Navegar a otra actividad, si es necesario
                Intent intent = new Intent(RouteActivity.this, HomeActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Error al crear la ruta", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
