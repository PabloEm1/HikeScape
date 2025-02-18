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
import java.util.List;

public class ProfileFragment extends Fragment {

    private ImageView profileImageView;
    private Uri selectedImageUri;
    private RecyclerView recyclerView;
    private PostAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Nombre de Usuario");
        TextView usernameTextView = view.findViewById(R.id.userName);
        usernameTextView.setText(username);

        recyclerView = view.findViewById(R.id.recyclerViewProfile);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        profileImageView = view.findViewById(R.id.profileImage);

        // Obtener ID del usuario
        int userId = sharedPreferences.getInt("userId", -1);
        if (userId != -1) {
            loadProfileImage(userId);
        }

        profileImageView.setOnClickListener(v -> checkPermissionAndOpenGallery(userId));

        // Llamar al método para obtener las rutas del usuario desde Firestore
        loadUserRoutes();

        return view;
    }

    private void loadUserRoutes() {
        FireStoreHelper fireStoreHelper = new FireStoreHelper();
        fireStoreHelper.getUserRoutes(new FireStoreHelper.FirestoreRoutesCallback() {
            @Override
            public void onRoutesLoaded(List<Post> routesList) {
                requireActivity().runOnUiThread(() -> {
                    adapter = new PostAdapter(routesList, requireContext(), true);
                    recyclerView.setAdapter(adapter);
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e("ProfileFragment", "Error al obtener rutas del usuario", e);
                Toast.makeText(requireContext(), "Error al cargar las rutas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        int userId = requireContext()
                                .getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                                .getInt("userId", -1);
                        if (userId != -1) {
                            saveProfileImageUri(userId, selectedImageUri);
                            profileImageView.setImageURI(selectedImageUri);
                            Toast.makeText(requireContext(), "Imagen seleccionada con éxito", Toast.LENGTH_SHORT).show();
                        }
                    }
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
        String uriString = databaseHelper.getProfileImageUri(userId);

        if (uriString != null) {
            Uri savedUri = Uri.parse(uriString);
            try {
                Glide.with(this)
                        .load(savedUri)
                        .circleCrop()
                        .into(profileImageView);
            } catch (Exception e) {
                Log.e("ProfileFragment", "Error al cargar la imagen de perfil", e);
                Glide.with(this)
                        .load(R.drawable.perfil)
                        .circleCrop()
                        .into(profileImageView);
            }
        } else {
            Glide.with(this)
                    .load(R.drawable.perfil)
                    .circleCrop()
                    .into(profileImageView);
        }
    }

    private void openGallery(int userId) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        Intent chooser = Intent.createChooser(intent, "Selecciona una aplicación para abrir imágenes");
        galleryLauncher.launch(chooser);
    }
}
