package com.example.hikescape;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FindFragment extends Fragment {

    private EditText searchUserEditText, searchRouteEditText;
    private Button searchUserButton, searchRouteButton;
    private RecyclerView resultsRecyclerView;
    private ResultsAdapter resultsAdapter;
    private FireStoreHelper fireStoreHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find, container, false);

        // Inicializar vistas
        searchUserEditText = view.findViewById(R.id.searchUser);
        searchRouteEditText = view.findViewById(R.id.searchRoute);
        searchUserButton = view.findViewById(R.id.buttonSearchUser);
        searchRouteButton = view.findViewById(R.id.buttonSearch);
        resultsRecyclerView = view.findViewById(R.id.resultsRecyclerView);

        fireStoreHelper = new FireStoreHelper();

        // Inicializar el adaptador con una lista vacía
        resultsAdapter = new ResultsAdapter(new ArrayList<>());
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        resultsRecyclerView.setAdapter(resultsAdapter);

        // Configurar acción para buscar rutas
        searchRouteButton.setOnClickListener(v -> searchRoutes());
        searchUserButton.setOnClickListener(v -> searchUsers());

        return view;
    }

    private void searchRoutes() {
        String query = searchRouteEditText.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(getContext(), "Introduce un nombre de ruta", Toast.LENGTH_SHORT).show();
            return;
        }

        fireStoreHelper.searchRoutesByName(query, new FireStoreHelper.FirestoreRoutesCallback() {
            @Override
            public void onRoutesLoaded(List<Post> routes) {
                PostAdapter adapter = new PostAdapter(routes, getContext(), false);
                resultsRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error al buscar rutas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchUsers() {
        String query = searchUserEditText.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(getContext(), "Introduce un nombre de usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        fireStoreHelper.searchUsersByName(query, new FireStoreHelper.FirestoreUsersCallback() {
            @Override
            public void onUsersLoaded(List<FireStoreHelper.User> users) {
                UserAdapter userAdapter = new UserAdapter(users, getContext());
                resultsRecyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error al buscar usuarios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
