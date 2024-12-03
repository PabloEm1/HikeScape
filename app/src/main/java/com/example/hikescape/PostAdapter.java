package com.example.hikescape;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

        // Setear el ícono del "me gusta" según el estado de liked
        if (post.isLiked()) {
            holder.likeIcon.setImageResource(R.drawable.like_red);  // Corazón rojo
        } else {
            holder.likeIcon.setImageResource(R.drawable.like);  // Corazón blanco
        }

        // Manejo del clic en el ícono del corazón
        holder.likeIcon.setOnClickListener(v -> {
            // Cambiar el estado de "me gusta" cuando el usuario hace clic
            boolean currentLikeStatus = post.isLiked();
            post.setLiked(!currentLikeStatus);  // Cambiar el estado de "me gusta"

            // Actualizar el ícono según el nuevo estado
            if (post.isLiked()) {
                holder.likeIcon.setImageResource(R.drawable.like_red);  // Corazón rojo
            } else {
                holder.likeIcon.setImageResource(R.drawable.like);  // Corazón blanco
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        ImageView imageView;
        ImageView likeIcon;  // Agregamos la referencia al ícono de "me gusta"

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            imageView = itemView.findViewById(R.id.postImageView);
            likeIcon = itemView.findViewById(R.id.likeIcon);  // Inicializamos el ícono
        }
    }
}
