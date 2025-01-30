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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Configurar el RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Obtener rutas desde la base de datos
        DatabaseHelper db = new DatabaseHelper(getContext());
        List<Post> postList = db.getAllRutas();  // Asumiendo que obtienes rutas con URIs

        // Configurar el adaptador del RecyclerView
        PostAdapter adapter = new PostAdapter(postList, getContext(), false);
        recyclerView.setAdapter(adapter);

        // Configurar el botón logout_icon
        ImageView logoutIcon = view.findViewById(R.id.logout_icon);
        logoutIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LogoutActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }
}
