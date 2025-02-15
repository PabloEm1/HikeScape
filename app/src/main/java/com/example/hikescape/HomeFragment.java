package com.example.hikescape;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private FireStoreHelper fireStoreHelper;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Configurar el RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar FireStoreHelper
        fireStoreHelper = new FireStoreHelper();

        // Cargar rutas desde Firestore
        loadRoutes();

        // Configurar el botón logout_icon
        ImageView logoutIcon = view.findViewById(R.id.logout_icon);
        logoutIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LogoutActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    private void loadRoutes() {
        fireStoreHelper.getAllRoutes(new FireStoreHelper.FirestoreRoutesCallback() {
            @Override
            public void onRoutesLoaded(List<Post> routes) {
                Log.d("HomeFragment", "Total de rutas recibidas en HomeFragment: " + routes.size());
                if (routes.isEmpty()) {
                    Toast.makeText(getContext(), "No hay rutas disponibles", Toast.LENGTH_SHORT).show();
                } else {
                    adapter = new PostAdapter(routes, getContext(), false);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("HomeFragment", "Error al obtener rutas", e);
                Toast.makeText(getContext(), "Error al cargar rutas", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
