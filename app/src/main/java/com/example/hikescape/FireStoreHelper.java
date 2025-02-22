package com.example.hikescape;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String userId = currentUser.getUid(); // Obtener el UID del usuario
                            Log.d("Register", "UID del nuevo usuario: " + userId);

                            // Crear objeto usuario sin la contraseña
                            User user = new User(username, email);

                            // Guardar usuario en Firestore usando el UID como ID del documento
                            FirebaseFirestore.getInstance().collection("users").document(userId)
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Error al guardar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(context, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void authenticateUser(String emailOrUsername, String password, Context context, final AuthCallback callback) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailOrUsername).matches()) {
            // Intentar iniciar sesión con correo electrónico y contraseña
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailOrUsername, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();
                                // Obtener el nombre de usuario desde Firestore
                                FirebaseFirestore.getInstance().collection("users").document(userId)
                                        .get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {
                                                String username = documentSnapshot.getString("username");
                                                callback.onSuccess(username);
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(context, "Error al obtener datos del usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Toast.makeText(context, "Error en el inicio de sesión: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Si no es un correo electrónico, buscar por nombre de usuario
            FirebaseFirestore.getInstance().collection("users").whereEqualTo("username", emailOrUsername)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            String userId = document.getId();
                            // Intentar iniciar sesión con el correo electrónico asociado al nombre de usuario
                            String email = document.getString("email");
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            callback.onSuccess(emailOrUsername);
                                        } else {
                                            Toast.makeText(context, "Error en el inicio de sesión: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Error al autenticar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }



    public void signInWithGoogle(GoogleSignInAccount account, Context context, final AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    String username = account.getDisplayName();
                    String email = account.getEmail();

                    // Crear o actualizar el documento del usuario en Firestore
                    User user = new User(username, email);
                    FirebaseFirestore.getInstance().collection("users").document(userId)
                            .set(user, com.google.firebase.firestore.SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                callback.onSuccess(username);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Error al guardar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
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

        public User() { }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }

        public String getUsername() { return username; }
        public String getEmail() { return email; }
    }


    // Método para crear una nueva ruta en Firestore
    public void createRoute(String routeName, String routeDescription, String routeDifficulty, String routePhoto, String username, Context context) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(context, "Error: No se encontró un usuario autenticado.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        Route route = new Route(routeName, routeDescription, routeDifficulty, routePhoto, username, userId, 0);

        Log.d(TAG, "Ruta a guardar: " + route.getRouteName() + ", UserID: " + route.getUserId());

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
        private boolean isLiked;
        private boolean isSaved;

        public Route() { }

        public Route(String routeName, String routeDescription, String routeDifficulty, String routePhoto, String username, String userId, int likes) {
            this.routeName = routeName;
            this.routeDescription = routeDescription;
            this.routeDifficulty = routeDifficulty;
            this.routePhoto = routePhoto;
            this.username = username;
            this.userId = userId;
            this.likes = likes;
            this.isLiked = false; // Inicializar a false
            this.isSaved = false; // Inicializar a false
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

        public boolean isLiked() {
            return isLiked;
        }

        public void setLiked(boolean liked) {
            isLiked = liked;
        }

        public boolean isSaved() {
            return isSaved;
        }

        public void setSaved(boolean saved) {
            isSaved = saved;
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


    public void getUserRoutes(FirestoreRoutesCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Obtener UID del usuario autenticado

        if (userId == null) {
            Log.e(TAG, "Error: No se encontró un usuario autenticado.");
            callback.onError(new Exception("No se encontró un usuario autenticado."));
            return;
        }

        CollectionReference routesRef = db.collection(ROUTES_COLLECTION);

        // Filtrar por el userId del usuario actual
        routesRef.whereEqualTo("userId", userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Post> routesList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, "Ruta encontrada: " + document.getId() + " => " + document.getData());
                            try {
                                String postId = document.getId();
                                String username = document.getString("username");
                                String imageUri = document.getString("routePhoto");
                                String postName = document.getString("routeName");
                                String postDescription = document.getString("routeDescription");
                                int likeCount = document.getLong("likes").intValue();

                                // Crear objeto Post con la información obtenida
                                Post post = new Post(postId, username, imageUri, postName, postDescription, likeCount);
                                routesList.add(post);
                            } catch (Exception e) {
                                Log.e(TAG, "Error al procesar la ruta: " + document.getId(), e);
                            }
                        }

                        Log.d(TAG, "Total de rutas del usuario: " + routesList.size());

                        callback.onRoutesLoaded(routesList);
                    } else {
                        Log.e(TAG, "Error al obtener rutas del usuario", task.getException());
                        callback.onError(task.getException());
                    }
                });
    }

    public void saveProfileImageUri(Uri uri, Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e("FireStoreHelper", "No hay usuario autenticado");
            Toast.makeText(context, "Error: No hay usuario autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        Log.d("FireStoreHelper", "userId obtenido: " + userId); // Log para verificar el userId

        DocumentReference userRef = db.collection("users").document(userId);

        // Verifica si el documento existe antes de actualizarlo
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // El documento existe, procede a actualizarlo
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("profileImageUrl", uri.toString());

                    userRef.set(updates, com.google.firebase.firestore.SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Imagen guardada correctamente en Firestore", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FireStoreHelper", "Error al guardar la URI en Firestore: " + e.getMessage(), e);
                                Toast.makeText(context, "Error al guardar la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // El documento no existe
                    Log.e("FireStoreHelper", "El documento del usuario no existe para el userId: " + userId);
                    Toast.makeText(context, "Error al guardar la imagen: usuario no encontrado", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("FireStoreHelper", "Error al obtener el documento del usuario", task.getException());
                Toast.makeText(context, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void loadProfileImage(String userId, ImageView profileImageView, Context context) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imageUrl = documentSnapshot.getString("profileImageUrl");
                        if (imageUrl != null) {
                            Glide.with(context)
                                    .load(Uri.parse(imageUrl))
                                    .circleCrop()
                                    .into(profileImageView);
                        } else {
                            Glide.with(context)
                                    .load(R.drawable.perfil)
                                    .circleCrop()
                                    .into(profileImageView);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FireStoreHelper", "Error al cargar la URI de Firestore", e);
                    Glide.with(context)
                            .load(R.drawable.perfil)
                            .circleCrop()
                            .into(profileImageView);
                });
    }

    public void searchRoutesByName(String routeName, FirestoreRoutesCallback callback) {
        CollectionReference routesRef = db.collection(ROUTES_COLLECTION);
        Query searchQuery = routesRef.whereEqualTo("routeName", routeName);

        searchQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Post> routesList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    try {
                        String postId = document.getId();
                        String username = document.getString("username");
                        String imageUri = document.getString("routePhoto");
                        String postName = document.getString("routeName");
                        String postDescription = document.getString("routeDescription");
                        int likeCount = document.getLong("likes").intValue();

                        Post post = new Post(postId, username, imageUri, postName, postDescription, likeCount);
                        routesList.add(post);
                    } catch (Exception e) {
                        Log.e(TAG, "Error al procesar documento: " + document.getId(), e);
                    }
                }
                callback.onRoutesLoaded(routesList);
            } else {
                Log.e(TAG, "Error al buscar rutas", task.getException());
                callback.onError(task.getException());
            }
        });
    }

    public void searchUsersByName(String username, FirestoreUsersCallback callback) {
        CollectionReference usersRef = db.collection("users");
        Query searchQuery = usersRef.whereEqualTo("username", username);

        searchQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<User> usersList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    try {
                        String username2 = document.getString("username");
                        String email = document.getString("email");

                        User user = new User(username2, email);
                        usersList.add(user);
                    } catch (Exception e) {
                        Log.e(TAG, "Error al procesar documento: " + document.getId(), e);
                    }
                }
                callback.onUsersLoaded(usersList);
            } else {
                Log.e(TAG, "Error al buscar usuarios", task.getException());
                callback.onError(task.getException());
            }
        });
    }
    public interface FirestoreCallback {
        void onCallback(String imageUrl);

    }
    public void getProfileImageUrl(String username, FirestoreCallback callback) {
        db.collection("users")
                .whereEqualTo("username", username) // Buscar por username
                .limit(1) // Solo un resultado
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String imageUrl = queryDocumentSnapshots.getDocuments().get(0).getString("profileImageUrl");
                        callback.onCallback(imageUrl); // Devuelve la URL obtenida
                    } else {
                        callback.onCallback(null); // No hay imagen
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FireStoreHelper", "Error al obtener la imagen de perfil", e);
                    callback.onCallback(null); // Error, devuelve null
                });
    }
    // Método para eliminar publicación (ruta) en Firestore
    public void deleteRoute(String routeDescription, String routeName, FirestoreDeleteCallback callback) {
        db.collection("routes")  // Asumimos que la colección se llama "routes"
                .whereEqualTo("routeDescription", routeDescription)
                .whereEqualTo("routeName", routeName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Si se encuentra el documento, obtenemos el documentId
                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();

                        // Ahora que tenemos el documentId, eliminamos la ruta
                        db.collection("routes")
                                .document(documentId)  // Usamos el ID del documento para eliminarlo
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    callback.onDeleteCallback(true);  // Llamamos al callback con resultado positivo
                                })
                                .addOnFailureListener(e -> {
                                    callback.onDeleteCallback(false);  // En caso de error, callback negativo
                                });
                    } else {
                        callback.onDeleteCallback(false);  // No se encontró la ruta
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FireStoreHelper", "Error al eliminar la ruta", e);
                    callback.onDeleteCallback(false);  // Error al realizar la consulta
                });
    }

    public interface FirestoreDeleteCallback {
        void onDeleteCallback(boolean isSuccess);  // Pasa un valor booleano para indicar si la eliminación fue exitosa
    }

    public interface FirestoreUsersCallback {
        void onUsersLoaded(List<User> users);
        void onError(Exception e);
    }
}
