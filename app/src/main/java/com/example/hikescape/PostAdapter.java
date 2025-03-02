package com.example.hikescape;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private Context context;
    private boolean isProfile; // Nuevo campo para indicar si estamos en el perfil

    // Modifica el constructor para aceptar el parámetro isProfile
    public PostAdapter(List<Post> postList, Context context, boolean isProfile) {
        this.postList = postList;
        this.context = context;
        this.isProfile = isProfile; // Asignar el valor del parámetro
    }


    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar la vista de cada ítem del RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        // Configurar datos dinámicos
        holder.userNameTextView.setText(post.getUserName());
        holder.postName.setText(post.getPostName());
        holder.postDescription.setText(post.getPostDescription());
        holder.difficultyText.setText(post.gettdifficultyText());

        // Instancia de FireStoreHelper
        FireStoreHelper fireStoreHelper = new FireStoreHelper();

        // Obtener el username del post
        String username = post.getUserName();

        // Obtener la URL de la imagen de perfil desde Firestore
        fireStoreHelper.getProfileImageUrl(username, imageUrl -> {
            Glide.with(context)
                    .load(imageUrl != null ? imageUrl : R.drawable.perfil) // Si no hay URL, usa imagen por defecto
                    .placeholder(R.drawable.perfil)
                    .circleCrop()
                    .into(holder.profileImageView);
        });

        // Mostrar/ocultar botón de menú según la pantalla (perfil o feed)
        holder.menuButton.setVisibility(isProfile ? View.VISIBLE : View.GONE);

        // Cargar la imagen de la publicación
        String imageUri = post.getImageUri();
        Glide.with(holder.itemView.getContext())
                .load(imageUri != null && !imageUri.isEmpty() ? imageUri : R.drawable.ruta1)
                .placeholder(R.drawable.ruta1)
                .error(R.drawable.ruta2)
                .into(holder.imageView);


        String routeName = post.getPostName();

// Verificar si la ruta tiene "me gusta" por el usuario
        fireStoreHelper.hasUserLikedRoute(routeName, isLiked -> {
            // Guardamos el estado en un array para que sea final y modificable dentro del listener
            final boolean[] isLikedState = {isLiked};

            holder.likeIcon.setImageResource(isLikedState[0] ? R.drawable.like_red : R.drawable.like);

            holder.likeIcon.setOnClickListener(v -> {
                if (isLikedState[0]) {
                    // Intentar quitar el "me gusta"
                    fireStoreHelper.unlikeRoute(routeName, success -> {
                        if (success) {
                            isLikedState[0] = false; // Actualizamos el estado localmente
                            holder.likeIcon.setImageResource(R.drawable.like);
                            post.setLiked(false);
                            post.decrementLikeCount();
                            Toast.makeText(v.getContext(), "Ya no te gusta esta ruta", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(), "Error al quitar el me gusta", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Intentar agregar el "me gusta"
                    fireStoreHelper.likeRoute(routeName, success -> {
                        if (success) {
                            isLikedState[0] = true; // Actualizamos el estado localmente
                            holder.likeIcon.setImageResource(R.drawable.like_red);
                            post.setLiked(true);
                            post.incrementLikeCount();
                            Toast.makeText(v.getContext(), "¡Te gusta esta ruta!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(), "Error al dar me gusta", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });


// Verificar si la ruta está guardada por el usuario en favoritos
        fireStoreHelper.hasUserFavoritedRoute(routeName, isFavorited -> {
            // Guardamos el estado en un array para que sea final y modificable dentro del listener
            final boolean[] isFavoriteState = {isFavorited};

            holder.saveIcon.setImageResource(isFavoriteState[0] ? R.drawable.guardar2 : R.drawable.guardar1);

            holder.saveIcon.setOnClickListener(v -> {
                if (isFavoriteState[0]) {
                    // Intentar eliminar de favoritos
                    fireStoreHelper.unfavoriteRoute(routeName, success -> {
                        if (success) {
                            isFavoriteState[0] = false; // Actualizamos el estado localmente
                            holder.saveIcon.setImageResource(R.drawable.guardar1);
                            Toast.makeText(v.getContext(), "Has eliminado esta ruta de favoritos", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(), "Error al eliminar esta ruta de favoritos", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Intentar agregar a favoritos
                    fireStoreHelper.favoriteRoute(routeName, success -> {
                        if (success) {
                            isFavoriteState[0] = true; // Actualizamos el estado localmente
                            holder.saveIcon.setImageResource(R.drawable.guardar2);
                            Toast.makeText(v.getContext(), "Ruta guardada en favoritos", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(), "Error al guardar esta ruta en favoritos", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });



        // Configuración del ícono de comentario
        holder.commentIcon.setOnClickListener(v -> showCommentDialog(holder.itemView.getContext(), post));

        // Configurar el listener para el botón de tres puntos (eliminar publicación)
        holder.menuButton.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Eliminar publicación")
                    .setMessage("¿Estás seguro de que deseas eliminar esta publicación?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        // Obtener los valores necesarios del objeto Post
                        String routeDescription = post.getPostDescription();  // Usamos el método getPostDescription() de Post
                        String routeName2 = post.getPostName();                // Usamos el método getPostName() de Post

                        // Llamamos a FirestoreHelper para eliminar la ruta
                        fireStoreHelper.deleteRoute(routeDescription, routeName2, isSuccess -> {
                            if (isSuccess) {
                                postList.remove(position);
                                notifyItemRemoved(position);  // Actualizar el RecyclerView
                                Toast.makeText(context, "Publicación eliminada", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Error al eliminar la publicación", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

    // Configuración del ícono de descarga
        holder.downloadIcon.setOnClickListener(v -> {
            // Guardar el PDF
            PDFGenerator.createPdf(post, v.getContext());

            // Abrir el PDF
            PDFGenerator.openPdf(v.getContext(), post.getPostName());

            // Mostrar un mensaje de éxito
            Toast.makeText(v.getContext(), "Ruta exportada a PDF", Toast.LENGTH_SHORT).show();
        });



    }

    private void showCommentDialog(Context context, Post post) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_comment, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        ImageView postImageView = dialogView.findViewById(R.id.commentPostImageView);
        EditText commentEditText = dialogView.findViewById(R.id.commentEditText);
        Button postCommentButton = dialogView.findViewById(R.id.postCommentButton);
        LinearLayout commentsContainer = dialogView.findViewById(R.id.showComments);

        String imageUri = post.getImageUri();
        Glide.with(dialogView.getContext()) // Usa el contexto del diálogo
                .load(imageUri)
                .error(R.drawable.ruta2)
                .into(postImageView);


        FireStoreHelper fireStoreHelper = new FireStoreHelper();

        fireStoreHelper.getCommentsForRoute(post.getPostName(), new FireStoreHelper.OnCommentsLoadedListener() {
            @Override
            public void onSuccess(List<FireStoreHelper.Comment> comments) {
                Log.d("PostAdapter", "Comments loaded successfully: " + comments.size());
                commentsContainer.removeAllViews();
                for (FireStoreHelper.Comment comment : comments) {
                    TextView textView = new TextView(context);
                    textView.setText(comment.getUsername() + ": " + comment.getText());
                    textView.setPadding(8, 8, 8, 8);
                    textView.setTextSize(16);
                    commentsContainer.addView(textView);
                    Log.d("PostAdapter", "Comment displayed: " + comment.getUsername() + ": " + comment.getText());
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e("PostAdapter", "Error loading comments: " + error);
                Toast.makeText(context, "Error al cargar comentarios: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        postCommentButton.setOnClickListener(v -> {
            String comment = commentEditText.getText().toString().trim();
            if (!comment.isEmpty()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String username = user.getDisplayName();
                    fireStoreHelper.addComment(post.getPostName(), username, comment, new FireStoreHelper.OnCommentAddedListener() {
                        @Override
                        public void onSuccess() {
                            Log.d("PostAdapter", "Comment added successfully");
                            Toast.makeText(context, "Comentario agregado", Toast.LENGTH_SHORT).show();
                            commentEditText.setText("");

                            fireStoreHelper.getCommentsForRoute(post.getPostName(), new FireStoreHelper.OnCommentsLoadedListener() {
                                @Override
                                public void onSuccess(List<FireStoreHelper.Comment> comments) {
                                    Log.d("PostAdapter", "Comments reloaded after adding a new one");
                                    commentsContainer.removeAllViews();
                                    for (FireStoreHelper.Comment comment : comments) {
                                        TextView textView = new TextView(context);
                                        textView.setText(comment.getUsername() + ": " + comment.getText());
                                        textView.setPadding(8, 8, 8, 8);
                                        textView.setTextSize(16);
                                        commentsContainer.addView(textView);
                                    }
                                }

                                @Override
                                public void onFailure(String error) {
                                    Log.e("PostAdapter", "Error reloading comments: " + error);
                                    Toast.makeText(context, "Error al cargar comentarios: " + error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(String error) {
                            Log.e("PostAdapter", "Error adding comment: " + error);
                            Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.w("PostAdapter", "User not authenticated");
                    Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.w("PostAdapter", "Comment text is empty");
                Toast.makeText(context, "Por favor, escribe un comentario", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }






    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        TextView postName;
        TextView postDescription;
        TextView difficultyText;
        ImageView saveIcon;
        ImageView imageView;
        ImageView likeIcon;
        ImageView commentIcon;
        ImageView profileImageView; // Foto de perfil
        ImageView menuButton; // Botón de tres puntos (eliminar publicación)
        ImageView downloadIcon;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            postName = itemView.findViewById(R.id.postName);
            postDescription = itemView.findViewById(R.id.postDescription);
            imageView = itemView.findViewById(R.id.postImageView);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            saveIcon = itemView.findViewById(R.id.saveIcon);
            commentIcon = itemView.findViewById(R.id.commentIcon);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            menuButton = itemView.findViewById(R.id.menuButton); // Inicializar el botón de tres puntos
            downloadIcon = itemView.findViewById(R.id.downloadIcon);
            difficultyText = itemView.findViewById(R.id.difficultyText);


        }
    }

}
