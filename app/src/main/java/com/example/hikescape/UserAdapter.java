package com.example.hikescape;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<FireStoreHelper.User> userList;
    private Context context;

    public UserAdapter(List<FireStoreHelper.User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        FireStoreHelper.User user = userList.get(position);
        holder.userNameTextView.setText(user.getUsername());

        // Cargar imagen de perfil con Glide y aplicar CircleCrop
        // Asumiendo que no hay URI de imagen en la clase User de FireStoreHelper
        Glide.with(context)
                .load(R.drawable.perfil) // Imagen por defecto
                .circleCrop() // Aplicar efecto circular
                .into(holder.profileImageView); // Colocar imagen en el ImageView
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView userNameTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
        }
    }
}
