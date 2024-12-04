package com.example.hikescape;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SavedRoutesAdapter extends RecyclerView.Adapter<SavedRoutesAdapter.ViewHolder> {

    private List<SavedRoute> savedRoutes;

    public SavedRoutesAdapter(List<SavedRoute> savedRoutes) {
        this.savedRoutes = savedRoutes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_route, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedRoute route = savedRoutes.get(position);
        holder.routeName.setText(route.getRouteName());
        holder.routeActionIcon.setOnClickListener(v -> {
            // Implement action, e.g., navigate to route details or add a marker
        });
    }

    @Override
    public int getItemCount() {
        return savedRoutes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView routeName;
        ImageView routeActionIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            routeName = itemView.findViewById(R.id.routeName);
            routeActionIcon = itemView.findViewById(R.id.routeActionIcon);
        }
    }
}
