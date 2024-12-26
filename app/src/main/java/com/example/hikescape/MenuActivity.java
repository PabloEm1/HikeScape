package com.example.hikescape;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Establecer el fragmento inicial directamente
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // Configurar el menú inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Establecer el primer ítem como seleccionado
        bottomNavigationView.setSelectedItemId(R.id.buttonHome); // Inicialmente se selecciona el Home

        bottomNavigationView.findViewById(R.id.buttonHome).setOnClickListener(v -> {
            loadFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.buttonHome);  // Actualizar el estado del item
        });

        bottomNavigationView.findViewById(R.id.buttonRuta).setOnClickListener(v -> {
            loadFragment(new RouteFragment());
            bottomNavigationView.setSelectedItemId(R.id.buttonRuta);  // Actualizar el estado del item
        });

        bottomNavigationView.findViewById(R.id.buttonProfile).setOnClickListener(v -> {
            loadFragment(new ProfileFragment());
            bottomNavigationView.setSelectedItemId(R.id.buttonProfile);  // Actualizar el estado del item
        });

        bottomNavigationView.findViewById(R.id.buttonRutafav).setOnClickListener(v -> {
            loadFragment(new FavoriteRoutesFragment());
            bottomNavigationView.setSelectedItemId(R.id.buttonRutafav);  // Actualizar el estado del item
        });

        bottomNavigationView.findViewById(R.id.buttonBuscar).setOnClickListener(v -> {
            loadFragment(new FindFragment());
            bottomNavigationView.setSelectedItemId(R.id.buttonBuscar);  // Actualizar el estado del item
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
