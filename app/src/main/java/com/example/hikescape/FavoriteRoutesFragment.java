package com.example.hikescape;

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

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class FavoriteRoutesFragment extends Fragment {

    private FireStoreHelper firestoreHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_routes, container, false);

        firestoreHelper = new FireStoreHelper();

        RecyclerView favoriteRoutesRecyclerView = view.findViewById(R.id.favoriteRoutesRecyclerView);
        favoriteRoutesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Obtener el UID del usuario autenticado desde FirebaseAuth
        String userUID = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userUID != null) {
            // Obtenemos las rutas favoritas desde Firestore usando el UID
            getFavoriteRoutesFromFirestore(userUID, favoriteRoutesRecyclerView);
        } else {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void getFavoriteRoutesFromFirestore(String userUID, RecyclerView recyclerView) {
        firestoreHelper.getFavoriteRoutesByUID(userUID, new FireStoreHelper.FavoriteRoutesCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                // Configuración del adaptador con los posts
                PostAdapter postAdapter = new PostAdapter(posts, requireContext(), false);  // Pasa 'false' si no estás en el perfil
                recyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        firestoreHelper.removeListener();
    }
}
