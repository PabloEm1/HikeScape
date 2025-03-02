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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Comment;

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

        public User() {
            // Constructor vacío
        }

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
        private int likescount;

        public Route() { }

        public Route(String routeName, String routeDescription, String routeDifficulty, String routePhoto, String username, String userId, int likescount) {
            this.routeName = routeName;
            this.routeDescription = routeDescription;
            this.routeDifficulty = routeDifficulty;
            this.routePhoto = routePhoto;
            this.username = username;
            this.userId = userId;
            this.likescount = likescount;
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
            return likescount;
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
                        String dificultad = document.getString("routeDifficulty");
                        int likeCount = document.getLong("likes").intValue();  // OK

                        // Crear objeto Post (Elimina userId ya que no existe en Firestore)
                        Post post = new Post(postId, username, imageUri, postName, postDescription, likeCount,dificultad);
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


    private ListenerRegistration listenerRegistration;

    public void getFavoriteRoutesByUID(String userUID, final FavoriteRoutesCallback callback) {
        db.collection("favorites")
                .whereEqualTo("username", userUID) // Filtramos por el usuario
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> favoriteRoutes = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String routeName = document.getString("routeName");
                            if (routeName != null) {
                                favoriteRoutes.add(routeName);
                            }
                        }

                        if (!favoriteRoutes.isEmpty()) {
                            db.collection("routes")
                                    .whereIn("routeName", favoriteRoutes) // Buscamos las rutas en la colección "routes"
                                    .get()
                                    .addOnCompleteListener(routeTask -> {
                                        if (routeTask.isSuccessful()) {
                                            List<Post> posts = new ArrayList<>();
                                            for (QueryDocumentSnapshot routeDocument : routeTask.getResult()) {
                                                try {
                                                    String postId = routeDocument.getId();
                                                    String username = routeDocument.getString("username");
                                                    String imageUri = routeDocument.getString("routePhoto");
                                                    String routeName = routeDocument.getString("routeName");
                                                    String postDescription = routeDocument.getString("routeDescription");
                                                    String dificultad = routeDocument.getString("routeDifficulty");

                                                    Post post = new Post(postId, username, imageUri, routeName, postDescription, 0, dificultad);
                                                    posts.add(post);
                                                } catch (Exception ex) {
                                                    Log.e("Firestore", "Error al procesar la ruta", ex);
                                                }
                                            }

                                            if (!posts.isEmpty()) {
                                                callback.onSuccess(posts);
                                            } else {
                                                callback.onFailure("No se encontraron rutas favoritas.");
                                            }
                                        } else {
                                            callback.onFailure("Error al obtener las rutas.");
                                        }
                                    });
                        } else {
                            callback.onFailure("No tienes rutas favoritas.");
                        }
                    } else {
                        callback.onFailure("Error al obtener favoritos.");
                    }
                });
    }

    // Interfaz para callback
    public interface FavoriteRoutesCallback {
        void onSuccess(List<Post> posts);
        void onFailure(String errorMessage);
    }

    // Método para cancelar el listener
    public void removeListener() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
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
                                String dificultad = document.getString("routeDifficulty");

                                // Crear objeto Post con la información obtenida
                                Post post = new Post(postId, username, imageUri, postName, postDescription, likeCount,dificultad);
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
                        String dificultad = document.getString("routeDifficulty");

                        Post post = new Post(postId, username, imageUri, postName, postDescription, likeCount, dificultad);
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
    public void hasUserLikedRoute(String routeName, String userName, OnLikeCheckListener listener) {
        // Verificar que los parámetros no sean nulos o vacíos
        if (routeName == null || routeName.isEmpty() || userName == null || userName.isEmpty()) {
            listener.onCheck(false); // Si alguno es nulo o vacío, retornamos false directamente
            return;
        }

        // Crear la referencia al documento
        DocumentReference likeRef = db.collection("likes")
                .document(routeName)
                .collection("users")
                .document(userName);

        // Realizar la consulta
        likeRef.get().addOnSuccessListener(documentSnapshot -> {
            // Si el documento existe, significa que ha dado like
            listener.onCheck(documentSnapshot.exists());
        }).addOnFailureListener(e -> {
            // En caso de fallo, retornamos false
            listener.onCheck(false);
        });
    }


    // Dar like a una ruta (almacena por nombre de usuario)
    public void likeRoute(String routeName, String userName, OnLikeActionListener listener) {
        Log.d("FireStoreHelper", "Intentando dar like...");
        Log.d("FireStoreHelper", "routeName: " + routeName);
        Log.d("FireStoreHelper", "userName: " + userName);

        if (routeName == null || userName == null || routeName.isEmpty() || userName.isEmpty()) {
            Log.e("FireStoreHelper", "Error: routeName o userName es nulo o vacío.");
            listener.onAction(false);
            return;
        }

        DocumentReference likeRef = db.collection("likes").document(routeName)
                .collection("users").document(userName); // Guardamos por nombre de usuario

        likeRef.set(new Like(userName)) // Registramos el like
                .addOnSuccessListener(aVoid -> {
                    Log.d("FireStoreHelper", "Like registrado con éxito para " + userName + " en la ruta " + routeName);
                    listener.onAction(true);
                })
                .addOnFailureListener(e -> {
                    Log.e("FireStoreHelper", "Error al registrar el like: ", e);
                    listener.onAction(false);
                });
    }
    public void getUsernameFromFirestore(String userId, OnUsernameCallback callback) {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String username = documentSnapshot.getString("username"); // Asegúrate de que el campo se llama "username"
                callback.onCallback(username);
            } else {
                callback.onCallback(null);
            }
        }).addOnFailureListener(e -> {
            Log.e("FireStoreHelper", "Error al obtener el username", e);
            callback.onCallback(null);
        });
    }

    // Interfaz para callback
    public interface OnUsernameCallback {
        void onCallback(String username);
    }


    public void unlikeRoute(String routeName, String userName, OnLikeActionListener listener) {
        DocumentReference likeRef = db.collection("likes").document(routeName)
                .collection("users").document(userName); // Eliminamos por nombre de usuario

        likeRef.delete()
                .addOnSuccessListener(aVoid -> listener.onAction(true))
                .addOnFailureListener(e -> listener.onAction(false));
    }

    // Interfaz para verificar "me gusta"
    public interface OnLikeCheckListener {
        void onCheck(boolean isLiked);
    }

    // Interfaz para acciones de like/unlike
    public interface OnLikeActionListener {
        void onAction(boolean success);
    }

    public interface FirestoreDeleteCallback {
        void onDeleteCallback(boolean isSuccess);  // Pasa un valor booleano para indicar si la eliminación fue exitosa
    }

    public interface FirestoreUsersCallback {
        void onUsersLoaded(List<User> users);
        void onError(Exception e);
    }

    private void saveFavorite(String routeName, String username, OnFavoriteActionListener listener) {
        CollectionReference favoriteRef = db.collection("favorites");

        // Crear el comentario
        Map<String, Object> comment = new HashMap<>();
        comment.put("routeName", routeName);
        comment.put("username", username);

        // Guardar en Firestore
        favoriteRef.add(comment)
                .addOnSuccessListener(documentReference -> listener.onAction(true));
    }

    // Verificar si la ruta está guardada por el usuario utilizando el nombre
    public void hasUserFavoritedRoute(String routeName, OnFavoriteCheckListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            listener.onCheck(false);
            return;
        }

        String username = user.getUid(); // Usamos UID

        db.collection("favorites")
                .whereEqualTo("routeName", routeName)
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listener.onCheck(!queryDocumentSnapshots.isEmpty()); // Si hay documentos, está en favoritos
                })
                .addOnFailureListener(e -> listener.onCheck(false));
    }



    public void favoriteRoute(String routeName, OnFavoriteActionListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            listener.onAction(false);
            return;
        }

        String username = user.getUid(); // Usamos UID en vez del nombre

        // Verificar si ya está en favoritos antes de guardarlo
        hasUserFavoritedRoute(routeName, isFavorited -> {
            if (isFavorited) {
                listener.onAction(true); // Ya está en favoritos, no hacemos nada
            } else {
                saveFavorite(routeName, username, listener);
            }
        });
    }



    public void unfavoriteRoute(String routeName, OnFavoriteActionListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            listener.onAction(false);
            return;
        }

        String username = user.getUid(); // Usamos UID

        removeFavorite(routeName, username, listener);
    }



    private void removeFavorite(String routeName, String username, OnFavoriteActionListener listener) {
        db.collection("favorites")
                .whereEqualTo("routeName", routeName)
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> listener.onAction(true))
                                    .addOnFailureListener(e -> listener.onAction(false));
                        }
                    } else {
                        listener.onAction(true); // No estaba en favoritos, pero consideramos éxito
                    }
                })
                .addOnFailureListener(e -> listener.onAction(false));
    }



    // Interfaces para la verificación y las acciones de los favoritos
    public interface OnFavoriteCheckListener {
        void onCheck(boolean isFavorited);
    }

    public interface OnFavoriteActionListener {
        void onAction(boolean success);
    }



    public void addComment(String routeName, String username, String commentText, OnCommentAddedListener listener) {
        CollectionReference commentsRef = db.collection("comments");

        // Crear el comentario
        Map<String, Object> comment = new HashMap<>();
        comment.put("routeName", routeName);
        comment.put("username", username);
        comment.put("commentText", commentText);
        comment.put("timestamp", System.currentTimeMillis()); // Marca de tiempo

        // Guardar en Firestore
        commentsRef.add(comment)
                .addOnSuccessListener(documentReference -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }
    public void getCommentsForRoute(String routeName, OnCommentsLoadedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("comments")
                .whereEqualTo("routeName", routeName)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Comment> comments = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String username = document.getString("username");
                        String text = document.getString("commentText");
                        if (username != null && text != null) {
                            comments.add(new Comment(username, text));
                        }
                    }
                    listener.onSuccess(comments);
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }


    // Interfaz para manejar los comentarios obtenidos
    public interface OnCommentsLoadedListener {
        void onSuccess(List<Comment> comments);
        void onFailure(String error);
    }

    public interface OnCommentAddedListener {
        void onSuccess();
        void onFailure(String error);
    }


    public class Comment {
        private String username;
        private String text;

        // Constructor vacío requerido por Firestore
        public Comment() {
        }

        public Comment(String username, String text) {
            this.username = username;
            this.text = text;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }





    public class Like {
        private String userName;

        // Constructor vacío requerido por Firestore
        public Like() {
        }

        // Constructor con parámetro
        public Like(String userName) {
            this.userName = userName;
        }

        // Getter y Setter
        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
    public static class Favorite {
        private String userName;

        // Constructor vacío requerido por Firestore
        public Favorite() {
        }

        // Constructor con parámetro
        public Favorite(String userName) {
            this.userName = userName;
        }

        // Getter y Setter
        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}