package com.example.hikescape;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FireStoreHelper {
    private static final String TAG="FireStoreHelper";
    private static final String ROUTES_COLLECTION = "routes";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public FireStoreHelper() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void registerUser(String username, String email, String password, Context context) {
        User user = new User(username, email, password);
        db.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error al registrar usuario en Firebase", Toast.LENGTH_SHORT).show());
    }

    public void authenticateUser(String emailOrUsername, String password, Context context, final AuthCallback callback) {
        Query query;
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailOrUsername).matches()) {
            query = db.collection("users").whereEqualTo("email", emailOrUsername);
        } else {
            query = db.collection("users").whereEqualTo("username", emailOrUsername);
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String storedPassword = document.getString("password");
                            if (storedPassword != null && storedPassword.equals(password)) {
                                String username = document.getString("username");
                                callback.onSuccess(username);
                                return;
                            }
                        }
                        Toast.makeText(context, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error al autenticar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void signInWithGoogle(GoogleSignInAccount account, Context context, final AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(account.getDisplayName());
            } else {
                Toast.makeText(context, "Error al autenticar con Google", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface AuthCallback {
        void onSuccess(String username);
    }

    // Clase interna para representar un usuario
    public static class User {
        private String username;
        private String email;
        private String password;

        public User() { }

        public User(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }

        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
    }

    // Método para crear una nueva ruta en Firestore
    public void createRoute(String routeName, String routeDescription, String routeDifficulty, String routePhoto, String username, Context context) {
        // Obtener el usuario actualmente autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(context, "Error: No se encontró un usuario autenticado.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el UID del usuario autenticado
        String userId = currentUser.getUid();

        // Crear la ruta con el ID del usuario
        Route route = new Route(routeName, routeDescription, routeDifficulty, routePhoto, username, userId, 0);

        // Log para depuración
        Log.d(TAG, "Ruta a guardar: " + route.getRouteName() + ", UserID: " + route.getUserId());

        // Guardar la ruta en Firestore
        db.collection("routes")
                .add(route)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(context, "Ruta creada con éxito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al crear la ruta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    // Clase interna para representar la ruta
    public static class Route {
        private String routeName;
        private String routeDescription;
        private String routeDifficulty;
        private String routePhoto;
        private String username;
        private String userId;
        private int likes;

        public Route() { }

        public Route(String routeName, String routeDescription, String routeDifficulty, String routePhoto, String username, String userId, int likes) {
            this.routeName = routeName;
            this.routeDescription = routeDescription;
            this.routeDifficulty = routeDifficulty;
            this.routePhoto = routePhoto;
            this.username = username;
            this.userId = userId;
            this.likes = likes;
        }

        public String getRouteName() {
            return routeName;
        }

        public String getRouteDescription() {
            return routeDescription;
        }

        public String getRouteDifficulty() {
            return routeDifficulty;
        }

        public String getRoutePhoto() {
            return routePhoto;
        }

        public String getUsername() {
            return username;
        }

        public String getUserId() {
            return userId;
        }

        public int getLikes() {
            return likes;
        }
    }


    public interface FirestoreRoutesCallback {
        void onRoutesLoaded(List<Post> routes);
        void onError(Exception e);
    }

    public void getAllRoutes(FirestoreRoutesCallback callback) {
        CollectionReference routesRef = db.collection(ROUTES_COLLECTION);
        routesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Post> routesList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d(TAG, "Documento encontrado: " + document.getId() + " => " + document.getData());
                    try {
                        // Extraer el ID generado por Firestore
                        String postId = document.getId();
                        String username = document.getString("username");  // OK
                        String imageUri = document.getString("routePhoto");  // OK
                        String postName = document.getString("routeName");  // OK
                        String postDescription = document.getString("routeDescription");  // OK
                        int likeCount = document.getLong("likes").intValue();  // OK

                        // Crear objeto Post (Elimina userId ya que no existe en Firestore)
                        Post post = new Post(postId, username, imageUri, postName, postDescription, likeCount);
                        routesList.add(post);
                    } catch (Exception e) {
                        Log.e(TAG, "Error al procesar documento: " + document.getId(), e);
                    }
                }

                Log.d(TAG, "Total de rutas obtenidas: " + routesList.size());

                if (!routesList.isEmpty()) {
                    callback.onRoutesLoaded(routesList);
                } else {
                    callback.onRoutesLoaded(new ArrayList<>());  // Lista vacía si no hay rutas
                }
            } else {
                Log.e(TAG, "Error al obtener rutas", task.getException());
                callback.onError(task.getException());
            }
        });
    }



}
