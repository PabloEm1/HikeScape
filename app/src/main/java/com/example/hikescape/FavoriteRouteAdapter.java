package com.example.hikescape;
import android.app.AlertDialog;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_route, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteRoute favoriteRoute = favoriteRoutes.get(position);

        holder.usernameTextView.setText(favoriteRoute.getUsername());
        holder.routeNameTextView.setText(favoriteRoute.getRouteName());

        // Cargar imagen desde la URL usando Glide con placeholder y manejo de errores
        Glide.with(holder.itemView.getContext())
                .load(favoriteRoute.getImageUrl())
                .circleCrop()                              // Para esquinas redondeadas
                .into(holder.profileImageView);

        // Configurar el click para abrir el diálogo con la imagen de la ruta
        holder.itemView.setOnClickListener(v -> showRouteImageDialog(v, favoriteRoute));
    }

    private void showRouteImageDialog(View view, FavoriteRoute favoriteRoute) {
        // Inflar la vista personalizada para el diálogo
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_route_image, null);

        ImageView routeImageView = dialogView.findViewById(R.id.routeImageView);

        // Realizar la consulta a la base de datos para obtener la URL de la imagen
        DatabaseHelper databaseHelper = new DatabaseHelper(view.getContext());
        String imageUrl = databaseHelper.getRouteImageUrl(favoriteRoute.getRouteId());

        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Cargar la imagen de la ruta en el diálogo
            Glide.with(view.getContext())
                    .load(imageUrl) // Cargar la URL obtenida de la base de datos
                    .into(routeImageView);
        }

        // Crear y mostrar el diálogo
        new AlertDialog.Builder(view.getContext())
                .setView(dialogView)
                .setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss())
                .show();
    }


    @Override
    public int getItemCount() {
        return favoriteRoutes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImageView;
        TextView usernameTextView;
        TextView routeNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            routeNameTextView = itemView.findViewById(R.id.routeNameTextView);
        }
    }
}

