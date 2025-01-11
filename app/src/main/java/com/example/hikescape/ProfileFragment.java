package com.example.hikescape;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ProfileFragment extends Fragment {

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
            PostAdapter adapter = new PostAdapter(userPosts, requireContext());
            recyclerView.setAdapter(adapter);
        }

        return view;
    }
}
