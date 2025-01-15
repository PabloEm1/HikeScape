package com.example.hikescape;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoriteRoutesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_routes, container, false);

        RecyclerView favoriteRoutesRecyclerView = view.findViewById(R.id.favoriteRoutesRecyclerView);
        favoriteRoutesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Obtener el ID del usuario desde SharedPreferences (o cualquier otro método de autenticación)
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);

        if (userId != -1) {
            // Obtener rutas favoritas desde la base de datos
            DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
            List<FavoriteRoute> favoriteRoutes = databaseHelper.getFavoriteRoutes(userId);

            // Configuración del adaptador
            FavoriteRouteAdapter adapter = new FavoriteRouteAdapter(favoriteRoutes);
            favoriteRoutesRecyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

}
