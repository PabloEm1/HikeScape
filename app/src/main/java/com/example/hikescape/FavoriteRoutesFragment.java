package com.example.hikescape;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteRoutesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_favorite_routes, container, false);

        // Configuración del RecyclerView
        RecyclerView favoriteRoutesRecyclerView = view.findViewById(R.id.favoriteRoutesRecyclerView);
        favoriteRoutesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Lista de rutas favoritas
        List<FavoriteRoute> favoriteRoutes = new ArrayList<>();
        favoriteRoutes.add(new FavoriteRoute("Usuario 1", "Ruta Montaña"));
        favoriteRoutes.add(new FavoriteRoute("Usuario 2", "Ruta Lago"));
        favoriteRoutes.add(new FavoriteRoute("Usuario 3", "Ruta Bosque"));
        favoriteRoutes.add(new FavoriteRoute("Usuario 4", "Ruta Playa"));
        favoriteRoutes.add(new FavoriteRoute("Usuario 5", "Ruta Cañón"));

        // Configuración del adaptador
        FavoriteRouteAdapter adapter = new FavoriteRouteAdapter(favoriteRoutes);
        favoriteRoutesRecyclerView.setAdapter(adapter);

        return view;
    }
}
