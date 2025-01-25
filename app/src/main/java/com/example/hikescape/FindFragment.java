package com.example.hikescape;

import android.database.Cursor;
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
    private DatabaseHelper databaseHelper;

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

        databaseHelper = new DatabaseHelper(getContext());

        // Configurar RecyclerView
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        resultsAdapter = new ResultsAdapter(new ArrayList<>());
        resultsRecyclerView.setAdapter(resultsAdapter);

        // Configurar acción para buscar usuarios
        searchUserButton.setOnClickListener(v -> {
            String query = searchUserEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                searchUsers(query);
            } else {
                Toast.makeText(getContext(), "Introduce un nombre de usuario", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar acción para buscar rutas
        searchRouteButton.setOnClickListener(v -> {
            String query = searchRouteEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                searchRoutes(query);
            } else {
                Toast.makeText(getContext(), "Introduce un nombre de ruta", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // Método para buscar usuarios
    private void searchRoutes(String query) {
        Cursor cursor = databaseHelper.searchRoutes(query);
        List<ListItem> results = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String routeName = cursor.getString(cursor.getColumnIndexOrThrow("nombre_ruta"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
                results.add(new ListItem(ListItem.TYPE_ROUTE, routeName, description, R.drawable.image1));
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            results.add(new ListItem(ListItem.TYPE_ROUTE, "No se encontraron rutas.", "", 0));
        }
        resultsAdapter.updateResults(results);
    }

    private void searchUsers(String query) {
        Cursor cursor = databaseHelper.searchUsers(query);
        List<ListItem> results = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                results.add(new ListItem(ListItem.TYPE_USER, username, "", R.drawable.perfil));
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            results.add(new ListItem(ListItem.TYPE_USER, "No se encontraron usuarios.", "", 0));
        }
        resultsAdapter.updateResults(results);
    }

}
