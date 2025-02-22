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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private ImageView profileImageView;
    private Uri selectedImageUri;
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Nombre de Usuario");
        TextView usernameTextView = view.findViewById(R.id.userName);
        usernameTextView.setText(username);

        recyclerView = view.findViewById(R.id.recyclerViewProfile);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        profileImageView = view.findViewById(R.id.profileImage);

        // Obtener UID del usuario autenticado y cargar su imagen de perfil
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            FireStoreHelper fireStoreHelper = new FireStoreHelper();
            fireStoreHelper.loadProfileImage(currentUser.getUid(), profileImageView, requireContext());
        }

        profileImageView.setOnClickListener(v -> openGallery());

        // Cargar rutas del usuario
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
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            FireStoreHelper fireStoreHelper = new FireStoreHelper();
                            // Llama al método actualizado sin pasar el userId
                            fireStoreHelper.saveProfileImageUri(selectedImageUri, requireContext());
                            profileImageView.setImageURI(selectedImageUri);
                            Toast.makeText(requireContext(), "Imagen seleccionada con éxito", Toast.LENGTH_SHORT).show();
                        } else {
                            // Maneja el caso en que no hay un usuario autenticado
                            Toast.makeText(requireContext(), "Error: No hay usuario autenticado", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        Intent chooser = Intent.createChooser(intent, "Selecciona una aplicación para abrir imágenes");
        galleryLauncher.launch(chooser);
    }
}
