package com.example.hikescape;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Configurar el RecyclerView del perfil
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewProfile);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2)); // 2 columnas

        // Lista de publicaciones de ejemplo
        List<Post> postList = new ArrayList<>();
        postList.add(new Post(1, "Publicación 1", R.drawable.ruta1, 0));
        postList.add(new Post(2, "Publicación 2", R.drawable.ruta2, 0));
        postList.add(new Post(3, "Publicación 3", R.drawable.ruta3, 0));

        // Configurar el adaptador
        PostAdapter adapter = new PostAdapter(postList, requireContext());
        recyclerView.setAdapter(adapter);



        return view;
    }

}
