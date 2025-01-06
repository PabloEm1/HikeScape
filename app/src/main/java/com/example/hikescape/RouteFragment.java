package com.example.hikescape;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import com.bumptech.glide.Glide;

public class RouteFragment extends Fragment {

    private static final int REQUEST_STORAGE_PERMISSION = 1002;

    private DatabaseHelper databaseHelper;
    private EditText routeNameEditText;
    private EditText routeDescriptionEditText;
    private Spinner routeDifficultySpinner;
    private ImageView uploadIcon;
    private Uri selectedImageUri; // Para almacenar la URI de la imagen seleccionada

    // Lanzador para la galería
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // Solo guardamos la URI, sin cargar la imagen en el ImageView
                    if (selectedImageUri != null) {
                        Log.d("GalleryLauncher", "Imagen seleccionada: " + selectedImageUri);
                        // Aquí guardas la URI para usarla más tarde
                        saveImageUri(selectedImageUri);  // Función para guardar la URI
                        Toast.makeText(requireContext(), "Imagen seleccionada con éxito", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("GalleryLauncher", "No se seleccionó ninguna imagen.");
                }
            });




    // Lanzador para permisos
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openGallery();
                } else {
                    Toast.makeText(requireContext(), "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show();
                }
            });


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        // Inicializar las vistas
        routeNameEditText = view.findViewById(R.id.routeName);
        routeDescriptionEditText = view.findViewById(R.id.routeLocation);
        routeDifficultySpinner = view.findViewById(R.id.routeDifficulty);
        uploadIcon = view.findViewById(R.id.profileImage);

        // Configurar clic en el LinearLayout
        LinearLayout uploadSection = view.findViewById(R.id.uploadSection);
        uploadSection.setOnClickListener(v -> checkPermissionAndOpenGallery());

        // Inicializar la base de datos
        databaseHelper = new DatabaseHelper(requireContext());

        // Configurar el botón para crear la ruta
        Button createRouteButton = view.findViewById(R.id.addRouteButton);
        createRouteButton.setOnClickListener(v -> createRoute());

        return view;
    }

    private void checkPermissionAndOpenGallery() {
        openGallery();
    }
    private void saveImageUri(Uri uri) {
        // Guardar la URI en SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedImageUri", uri.toString());  // Guardamos la URI como String
        editor.apply();
    }


    private Uri getSavedImageUri() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String imageUriString = sharedPreferences.getString("selectedImageUri", null);
        return imageUriString != null ? Uri.parse(imageUriString) : null;  // Si no hay URI guardada, devuelve null
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // Solo imágenes
        Intent chooser = Intent.createChooser(intent, "Selecciona una aplicación para abrir imágenes");
        galleryLauncher.launch(chooser);
    }




    private void createRoute() {
        // Obtener los valores del formulario
        String routeName = routeNameEditText.getText().toString().trim();
        String routeDescription = routeDescriptionEditText.getText().toString().trim();
        String routeDifficulty = routeDifficultySpinner.getSelectedItem().toString();

        if (routeName.isEmpty() || routeDescription.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
        } else {
            // Usa la URI de la imagen seleccionada como ruta para la foto
            String routePhoto = selectedImageUri != null ? selectedImageUri.toString() : "default_photo";

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
