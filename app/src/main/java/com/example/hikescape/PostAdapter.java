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

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private DatabaseHelper databaseHelper;  // Agregar una referencia al DatabaseHelper

    // Constructor del adaptador
    public PostAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.databaseHelper = new DatabaseHelper(context);  // Inicializar el DatabaseHelper
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el layout para cada item del RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        // Establecer los datos del post (usuario, imagen, etc.)
        holder.userNameTextView.setText(post.getUserName());
        holder.imageView.setImageResource(post.getImageResource());
        holder.likeIcon.setImageResource(post.isLiked() ? R.drawable.like_red : R.drawable.like);

        // Configuración del clic en el ícono de comentario
        holder.commentIcon.setOnClickListener(v -> {
            showCommentDialog(holder.itemView.getContext(), post);
        });

        // Configuración del clic en el ícono de "me gusta"
        holder.likeIcon.setOnClickListener(v -> {
            int rutaId = post.getPostId();

            if (post.isLiked()) {
                // Quitar el like
                boolean result = databaseHelper.unlikeRuta(holder.itemView.getContext(), rutaId);
                if (result) {
                    post.setLiked(false);
                    post.decrementLikeCount();
                    Toast.makeText(v.getContext(), "Ya no te gusta esta ruta", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "Error al quitar el me gusta", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Agregar el like
                boolean result = databaseHelper.likeRuta(holder.itemView.getContext(), rutaId);
                if (result) {
                    post.setLiked(true);
                    post.incrementLikeCount();
                    Toast.makeText(v.getContext(), "¡Te gusta esta ruta!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "Error al dar me gusta", Toast.LENGTH_SHORT).show();
                }
            }

            // Actualizar el ícono del like
            holder.likeIcon.setImageResource(post.isLiked() ? R.drawable.like_red : R.drawable.like);
        });

    }


    // Método para mostrar el cuadro de diálogo de comentario
    private void showCommentDialog(Context context, Post post) {
        // Inflar el layout del diálogo
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_comment, null);

        // Crear el cuadro de diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Referencias a los elementos del cuadro de diálogo
        ImageView postImageView = dialogView.findViewById(R.id.commentPostImageView);
        EditText commentEditText = dialogView.findViewById(R.id.commentEditText);
        Button postCommentButton = dialogView.findViewById(R.id.postCommentButton);

        // Establecer la imagen del post en el cuadro de diálogo
        postImageView.setImageResource(post.getImageResource());

        // Configurar el clic en el botón de "Publicar comentario"
        postCommentButton.setOnClickListener(v -> {
            String comment = commentEditText.getText().toString().trim();
            if (!comment.isEmpty()) {
                // Obtener el userId desde SharedPreferences
                SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                int userId = sharedPreferences.getInt("userId", -1); // Devuelve -1 si no está autenticado

                if (userId != -1) {
                    // Guardar el comentario en la base de datos
                    int rutaId = post.getPostId();  // Obtener el ID del post
                    boolean result = databaseHelper.insertComentario(rutaId, userId, comment);

                    if (result) {
                        // Mostrar un mensaje de éxito
                        Toast.makeText(context, "Comentario agregado", Toast.LENGTH_SHORT).show();
                    } else {
                        // Mostrar un mensaje de error
                        Toast.makeText(context, "Error al agregar comentario", Toast.LENGTH_SHORT).show();
                    }

                    // Cerrar el cuadro de diálogo
                    dialog.dismiss();
                } else {
                    // Mostrar un mensaje si el usuario no está autenticado
                    Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Si el campo de comentario está vacío, mostrar un mensaje de error
                Toast.makeText(context, "Por favor, escribe un comentario", Toast.LENGTH_SHORT).show();
            }
        });

        // Mostrar el cuadro de diálogo
        dialog.show();
    }


    @Override
    public int getItemCount() {
        // Retorna el tamaño de la lista de publicaciones
        return postList.size();
    }

    // ViewHolder que mantiene las referencias a las vistas
    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        ImageView imageView;
        ImageView likeIcon;
        ImageView commentIcon; // Ícono para comentar

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializar las vistas del layout
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            imageView = itemView.findViewById(R.id.postImageView);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            commentIcon = itemView.findViewById(R.id.commentIcon); // Inicializar el ícono de comentario
        }
    }
}
