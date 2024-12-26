package com.example.hikescape;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Configurar el RecyclerView para mostrar publicaciones
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Crear una lista de publicaciones
        List<Post> postList = new ArrayList<>();
        postList.add(new Post(1, "Nombre de Usuario 1", R.drawable.ruta1, 0));
        postList.add(new Post(2, "Nombre de Usuario 2", R.drawable.ruta2, 0));
        postList.add(new Post(3, "Nombre de Usuario 3", R.drawable.ruta3, 0));

        // Configurar el adaptador del RecyclerView
        PostAdapter adapter = new PostAdapter(postList, getContext());
        recyclerView.setAdapter(adapter);
        // Configurar el botón logout_icon
        ImageView logoutIcon = view.findViewById(R.id.logout_icon);
        logoutIcon.setOnClickListener(v -> {
            // Redirigir a LogoutActivity
            Intent intent = new Intent(getActivity(), LogoutActivity.class);
            startActivity(intent);

            // Opcional: Finalizar la actividad actual
            requireActivity().finish();
        });
        return view;

    }
}
