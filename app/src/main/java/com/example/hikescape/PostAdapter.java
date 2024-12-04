package com.example.hikescape;

import android.content.Context;
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

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.userNameTextView.setText(post.getUserName());
        holder.imageView.setImageResource(post.getImageResource());

        // Configurar clic en el ícono de comentario
        holder.commentIcon.setOnClickListener(v -> {
            // Mostrar el cuadro de diálogo para comentar
            showCommentDialog(holder.itemView.getContext(), post);
        });

        // Configurar clic en el ícono de "me gusta"
        holder.likeIcon.setOnClickListener(v -> {
            boolean currentLikeStatus = post.isLiked();
            post.setLiked(!currentLikeStatus);
            holder.likeIcon.setImageResource(post.isLiked() ? R.drawable.like_red : R.drawable.like);
        });
    }
    private void showCommentDialog(Context context, Post post) {
        // Inflar el layout del diálogo
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_comment, null);

        // Crear el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Configurar los elementos del diálogo
        ImageView postImageView = dialogView.findViewById(R.id.commentPostImageView);
        EditText commentEditText = dialogView.findViewById(R.id.commentEditText);
        Button postCommentButton = dialogView.findViewById(R.id.postCommentButton);

        // Establecer la imagen del post en el diálogo
        postImageView.setImageResource(post.getImageResource());

        // Configurar el botón para agregar un comentario
        postCommentButton.setOnClickListener(v -> {
            String comment = commentEditText.getText().toString().trim();
            if (!comment.isEmpty()) {
                post.addComment(comment); // Agregar el comentario al post
                dialog.dismiss();
                Toast.makeText(context, "Comentario agregado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Por favor, escribe un comentario", Toast.LENGTH_SHORT).show();
            }
        });

        // Mostrar el diálogo
        dialog.show();
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        ImageView imageView;
        ImageView likeIcon;
        ImageView commentIcon; // Ícono de comentario

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            imageView = itemView.findViewById(R.id.postImageView);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            commentIcon = itemView.findViewById(R.id.commentIcon); // Inicializamos el ícono
        }
    }

}
