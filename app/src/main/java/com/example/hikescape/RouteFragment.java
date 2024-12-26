package com.example.hikescape;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RouteFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private EditText routeNameEditText;
    private EditText routeDescriptionEditText;
    private Spinner routeDifficultySpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        // Inicializar las vistas
        routeNameEditText = view.findViewById(R.id.routeName);
        routeDescriptionEditText = view.findViewById(R.id.routeLocation);
        routeDifficultySpinner = view.findViewById(R.id.routeDifficulty);

        // Inicializar la base de datos
        databaseHelper = new DatabaseHelper(requireContext());

        // Configurar el botón para crear la ruta
        Button createRouteButton = view.findViewById(R.id.addRouteButton);
        createRouteButton.setOnClickListener(v -> createRoute());

        return view;
    }

    private void createRoute() {
        // Obtener los valores del formulario
        String routeName = routeNameEditText.getText().toString().trim();
        String routeDescription = routeDescriptionEditText.getText().toString().trim();
        String routeDifficulty = routeDifficultySpinner.getSelectedItem().toString();

        if (routeName.isEmpty() || routeDescription.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
        } else {
            // Asignar un valor para la foto. Por ahora, pongamos un valor fijo o manejarlo según sea necesario
            String routePhoto = "default_photo"; // Esto es solo un ejemplo

            // Obtener el userId desde SharedPreferences
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt("userId", -1); // Devuelve -1 si no está autenticado

            if (userId != -1) {
                // Llamar a la función insertRuta para agregar la ruta en la base de datos
                boolean isInserted = databaseHelper.insertRuta(userId, routeName, routeDescription, routePhoto, routeDifficulty);

                // Comprobar si la ruta fue insertada correctamente
                if (isInserted) {
                    Toast.makeText(requireContext(), "Ruta creada con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Error al crear la ruta", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Si el usuario no está autenticado, mostrar un mensaje
                Toast.makeText(requireContext(), "Usuario no autenticado. Por favor, inicie sesión.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
