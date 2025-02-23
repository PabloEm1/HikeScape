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

public class FavoriteRouteAdapter extends RecyclerView.Adapter<FavoriteRouteAdapter.ViewHolder> {

    private final List<FavoriteRoute> favoriteRoutes;

    public FavoriteRouteAdapter(List<FavoriteRoute> favoriteRoutes) {
        this.favoriteRoutes = favoriteRoutes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteRoute favoriteRoute = favoriteRoutes.get(position);

        holder.userNameTextView.setText(favoriteRoute.getUsername());
        holder.postName.setText(favoriteRoute.getRouteName());  // Usar el ID correcto aquí

        // Cargar imagen desde la URL usando Glide (si tienes una URL de imagen)
        Glide.with(holder.itemView.getContext())
                .load(favoriteRoute.getImageUrl())
                .circleCrop()
                .into(holder.profileImageView);
    }


    @Override
    public int getItemCount() {
        return favoriteRoutes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImageView;
        TextView userNameTextView;
        TextView postName;  // Cambia 'routeNameTextView' a 'postName'

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            postName = itemView.findViewById(R.id.postName);  // Usa el ID correcto
        }
    }

}
