package com.example.hikescape;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FindFragment extends Fragment {

    private EditText searchUserEditText;
    private Button searchUserButton;
    private LinearLayout firstUserLayout;
    private LinearLayout secondUserLayout;
    private Button followUser1Button;
    private Button followUser2Button;
    private TextView titleBuscarRutas;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el layout del fragment
        View view = inflater.inflate(R.layout.fragment_find, container, false);

        // Inicializar vistas
        searchUserEditText = view.findViewById(R.id.searchUser);
        searchUserButton = view.findViewById(R.id.buttonSearchUser);
        firstUserLayout = view.findViewById(R.id.firstUser);
        secondUserLayout = view.findViewById(R.id.secondUser);
        followUser1Button = view.findViewById(R.id.followUser1);
        followUser2Button = view.findViewById(R.id.followUser2);
        titleBuscarRutas = view.findViewById(R.id.titleBuscarRutas);

        // Configurar acción para el botón de búsqueda de usuario
        searchUserButton.setOnClickListener(v -> {
            String query = searchUserEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                searchUser(query);
            }
        });

        // Configurar acción para el botón "Seguir" del primer usuario
        followUser1Button.setOnClickListener(v -> followUser1Button.setText("Siguiendo"));

        // Configurar acción para el botón "Seguir" del segundo usuario
        followUser2Button.setOnClickListener(v -> followUser2Button.setText("Siguiendo"));

        return view;
    }

    // Método para realizar la búsqueda de usuarios
    private void searchUser(String query) {
        titleBuscarRutas.setText("Resultados para: " + query);
    }
}
