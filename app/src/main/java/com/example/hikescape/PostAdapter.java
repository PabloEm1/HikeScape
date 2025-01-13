package com.example.hikescape;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private DatabaseHelper databaseHelper;
    private Context context;

    public PostAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.databaseHelper = new DatabaseHelper(context);
        this.context = context;
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

        // Configurar la foto de perfil desde SharedPreferences o Post
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String profileImageUri = sharedPreferences.getString("profileImageUri", null);

        if (profileImageUri != null && !profileImageUri.isEmpty()) {
            Glide.with(context)
                    .load(profileImageUri)
                    .placeholder(R.drawable.perfil)  // Imagen predeterminada mientras se carga
                    .circleCrop()  // Recorte circular
                    .into(holder.profileImageView);
        } else {
            Glide.with(context)
                    .load(R.drawable.perfil)  // Imagen predeterminada si no hay URI
                    .circleCrop()
                    .into(holder.profileImageView);
        }

        // Configurar la imagen de la publicación
        String imageUri = post.getImageUri();
        if (imageUri != null && !imageUri.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUri)  // Usar URI en lugar de recurso local
                    .placeholder(R.drawable.ruta1)  // Mostrar un placeholder mientras se carga
                    .error(R.drawable.ruta2)  // Si hay un error, mostrar una imagen de error
                    .into(holder.imageView);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.ruta1)  // Si no hay URI, mostrar una imagen de placeholder
                    .into(holder.imageView);
        }

        // Obtener el userId de la sesión actual desde SharedPreferences
        int userId = sharedPreferences.getInt("userId", -1);

        // Verificar si el usuario ya le dio like a la ruta
        boolean isLiked = databaseHelper.hasUserLikedRoute(userId, post.getPostId());

        // Configuración del ícono de "me gusta"
        holder.likeIcon.setImageResource(isLiked ? R.drawable.like_red : R.drawable.like);
        holder.likeIcon.setOnClickListener(v -> {
            int rutaId = post.getPostId();

            if (isLiked) {
                // Quitar like
                boolean result = databaseHelper.unlikeRuta(holder.itemView.getContext(), rutaId);
                if (result) {
                    post.setLiked(false);
                    post.decrementLikeCount();
                    Toast.makeText(v.getContext(), "Ya no te gusta esta ruta", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "Error al quitar el me gusta", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Dar like
                boolean result = databaseHelper.likeRuta(holder.itemView.getContext(), rutaId);
                if (result) {
                    post.setLiked(true);
                    post.incrementLikeCount();
                    Toast.makeText(v.getContext(), "¡Te gusta esta ruta!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "Error al dar me gusta", Toast.LENGTH_SHORT).show();
                }
            }

            // Actualizar el ícono de "me gusta"
            holder.likeIcon.setImageResource(post.isLiked() ? R.drawable.like_red : R.drawable.like);
        });

        // Configuración del ícono de comentario
        holder.commentIcon.setOnClickListener(v -> showCommentDialog(holder.itemView.getContext(), post));

        // Verificar si la ruta ya está guardada por el usuario
        final boolean[] isSaved = {databaseHelper.isRutaSavedByUser(userId, post.getPostId())};  // Usar un arreglo

        // Configuración del ícono de guardado basado en isSaved
        holder.saveIcon.setImageResource(isSaved[0] ? R.drawable.guardar2 : R.drawable.guardar1);
        holder.saveIcon.setOnClickListener(v -> {
            int rutaId = post.getPostId();
            if (isSaved[0]) {
                // Quitar guardado
                boolean result = databaseHelper.removeFavorite(holder.itemView.getContext(), rutaId);
                if (result) {
                    Toast.makeText(v.getContext(), "Has eliminado esta ruta de favoritos", Toast.LENGTH_SHORT).show();
                    holder.saveIcon.setImageResource(R.drawable.guardar1);
                    isSaved[0] = false;  // Actualizar el estado
                } else {
                    Toast.makeText(v.getContext(), "Error al eliminar esta ruta de favoritos", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Guardar
                boolean result = databaseHelper.saveFavorite(holder.itemView.getContext(), rutaId);
                if (result) {
                    Toast.makeText(v.getContext(), "Ruta guardada en favoritos", Toast.LENGTH_SHORT).show();
                    holder.saveIcon.setImageResource(R.drawable.guardar2);
                    isSaved[0] = true;  // Actualizar el estado
                } else {
                    Toast.makeText(v.getContext(), "Error al guardar esta ruta en favoritos", Toast.LENGTH_SHORT).show();
                }
            }
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

        // Cargar la imagen de la publicación en el diálogo de comentarios
        Glide.with(context)
                .load(post.getImageUri()) // Usar URI de la imagen
                .into(postImageView);

        postCommentButton.setOnClickListener(v -> {
            String comment = commentEditText.getText().toString().trim();
            if (!comment.isEmpty()) {
                // Obtener el ID del usuario de las preferencias compartidas
                SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                int userId = sharedPreferences.getInt("userId", -1);

                if (userId != -1) {
                    int rutaId = post.getPostId();
                    boolean result = databaseHelper.insertComentario(rutaId, userId, comment);

                    if (result) {
                        Toast.makeText(context, "Comentario agregado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error al agregar comentario", Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
                }
            } else {
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
        ImageView saveIcon;
        ImageView imageView;
        ImageView likeIcon;
        ImageView commentIcon;
        ImageView profileImageView; // Nuevo atributo para la foto de perfil

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            imageView = itemView.findViewById(R.id.postImageView);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            saveIcon = itemView.findViewById(R.id.saveIcon);
            commentIcon = itemView.findViewById(R.id.commentIcon);
            profileImageView = itemView.findViewById(R.id.profileImageView); // Inicializar el nuevo ImageView
        }
    }
}
