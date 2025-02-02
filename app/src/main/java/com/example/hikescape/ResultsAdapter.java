package com.example.hikescape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ListItem> items;

    public ResultsAdapter(List<ListItem> items) {
        this.items = items;
    }
    public void updateResults(List<ListItem> newItems) {
        this.items.clear(); // Limpia la lista actual
        this.items.addAll(newItems); // Agrega los nuevos elementos
        notifyDataSetChanged(); // Notifica al RecyclerView que los datos han cambiado
    }
    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ListItem.TYPE_ROUTE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
            return new RouteViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem item = items.get(position);
        if (holder instanceof RouteViewHolder) {
            RouteViewHolder routeHolder = (RouteViewHolder) holder;
            routeHolder.postName.setText(item.getName());
            routeHolder.postDescription.setText(item.getDescription());

            // Cargar la imagen de la publicación usando Glide
            if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
                Glide.with(routeHolder.itemView.getContext())
                        .load(item.getImageUri())
                        .placeholder(R.drawable.ruta1) // Imagen de placeholder
                        .error(R.drawable.ruta2) // Imagen de error
                        .into(routeHolder.postImageView);
            } else {
                Glide.with(routeHolder.itemView.getContext())
                        .load(R.drawable.ruta1) // Imagen predeterminada
                        .into(routeHolder.postImageView);
            }
        } else {
            UserViewHolder userHolder = (UserViewHolder) holder;
            userHolder.userNameTextView.setText(item.getName());

            // Cargar la imagen de perfil usando Glide
            if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
                Glide.with(userHolder.itemView.getContext())
                        .load(item.getImageUri())
                        .placeholder(R.drawable.perfil) // Imagen de placeholder
                        .error(R.drawable.perfil) // Imagen de error
                        .circleCrop() // Recorte circular
                        .into(userHolder.profileImageView);
            } else {
                Glide.with(userHolder.itemView.getContext())
                        .load(R.drawable.perfil) // Imagen predeterminada
                        .circleCrop()
                        .into(userHolder.profileImageView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {
        TextView postName, postDescription;
        ImageView postImageView;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            postName = itemView.findViewById(R.id.postName);
            postDescription = itemView.findViewById(R.id.postDescription);
            postImageView = itemView.findViewById(R.id.postImageView);
        }
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        ImageView profileImageView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
        }
    }
}