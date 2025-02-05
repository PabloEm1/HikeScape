package com.example.hikescape;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ProfileFragment extends Fragment {

    private ImageView profileImageView;
    private Uri selectedImageUri; // URI para la imagen seleccionada

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Recuperar el nombre del usuario desde SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Nombre de Usuario");
        TextView usernameTextView = view.findViewById(R.id.userName);

        // Establecer el nombre en el TextView
        usernameTextView.setText(username);

        // Configurar el RecyclerView del perfil
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewProfile);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2)); // 2 columnas

        // Obtener el ID del usuario actual desde SharedPreferences
        int userId = sharedPreferences.getInt("userId", -1); // Valor por defecto -1

        if (userId != -1) {
            // Recuperar publicaciones del usuario desde la base de datos
            DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
            List<Post> userPosts = databaseHelper.getPostsByUserId(userId); // Método para recuperar publicaciones del usuario

            // Configurar el adaptador con las publicaciones del usuario
            PostAdapter adapter = new PostAdapter(userPosts, requireContext(), true);
            recyclerView.setAdapter(adapter);
        }

        // Configurar la imagen de perfil
        profileImageView = view.findViewById(R.id.profileImage);

        // Cargar la imagen de perfil guardada
        loadProfileImage(userId); // Ahora pasamos el ID del usuario

        // Configurar clic en la imagen de perfil para abrir la galería
        profileImageView.setOnClickListener(v -> checkPermissionAndOpenGallery(userId)); // Pasamos el ID del usuario

        return view;
    }

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // Solo guardamos la URI, sin cargar la imagen en el ImageView
                    if (selectedImageUri != null) {
                        Log.d("GalleryLauncher", "Imagen seleccionada: " + selectedImageUri);
                        int userId = requireContext()
                                .getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                                .getInt("userId", -1);
                        if (userId != -1) {
                            saveProfileImageUri(userId, selectedImageUri); // Guardar la URI asociada al usuario
                            profileImageView.setImageURI(selectedImageUri); // Mostrar la imagen en el ImageView
                            Toast.makeText(requireContext(), "Imagen seleccionada con éxito", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.d("GalleryLauncher", "No se seleccionó ninguna imagen.");
                }
            });

    private void checkPermissionAndOpenGallery(int userId) {
        openGallery(userId);
    }

    private void saveProfileImageUri(int userId, Uri uri) {
        DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
        boolean isUpdated = databaseHelper.updateUserProfileImage(userId, uri.toString());

        if (isUpdated) {
            Toast.makeText(requireContext(), "Imagen guardada correctamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Error al actualizar la imagen en la base de datos", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadProfileImage(int userId) {
        DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
        String uriString = databaseHelper.getProfileImageUri(userId); // Método para obtener la URI

        if (uriString != null) {
            Uri savedUri = Uri.parse(uriString);
            try {
                // Cargar la imagen con Glide y aplicar CircleCrop
                Glide.with(this)
                        .load(savedUri)
                        .circleCrop() // Hace que la imagen sea circular
                        .into(profileImageView);
            } catch (Exception e) {
                Log.e("ProfileFragment", "Error al cargar la imagen de perfil", e);
                // Si hay un error, establecer una imagen predeterminada
                Glide.with(this)
                        .load(R.drawable.perfil)
                        .circleCrop()
                        .into(profileImageView);
            }
        } else {
            // Si no hay URI guardada, establecer una imagen predeterminada
            Glide.with(this)
                    .load(R.drawable.perfil)
                    .circleCrop()
                    .into(profileImageView);
        }
    }


    private void openGallery(int userId) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // Solo imágenes
        Intent chooser = Intent.createChooser(intent, "Selecciona una aplicación para abrir imágenes");
        galleryLauncher.launch(chooser);
    }

}
