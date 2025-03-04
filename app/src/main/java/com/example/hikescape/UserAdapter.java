package com.example.hikescape;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<FireStoreHelper.User> userList;
    private Context context;
    private FireStoreHelper fireStoreHelper;  // Instanciamos FireStoreHelper

    public UserAdapter(List<FireStoreHelper.User> userList, Context context) {
        this.userList = userList;
        this.context = context;
        this.fireStoreHelper = new FireStoreHelper(); // Inicializamos FireStoreHelper
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

        // Cargar la imagen de perfil
        fireStoreHelper.getProfileImageUrl(user.getUsername(), imageUrl -> {
            Glide.with(context)
                    .load(imageUrl != null ? imageUrl : R.drawable.perfil)
                    .circleCrop()
                    .into(holder.profileImageView);
        });

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Obtener el ID del usuario actual
        String targetUsername = user.getUsername(); // Obtenemos el nombre de usuario
        fireStoreHelper.isFollowing(currentUserId, targetUsername, isFollowing -> {
            holder.seguirImageView.setImageResource(isFollowing ? R.drawable.noseguir : R.drawable.seguir);
            holder.seguirImageView.setTag(isFollowing);
        });


        // Manejar clic en el botón de seguir/no seguir
        holder.seguirImageView.setOnClickListener(v -> {
            boolean isFollowing = (boolean) holder.seguirImageView.getTag(); // Obtener el estado actual

            if (isFollowing) {
                // Dejar de seguir
                holder.seguirImageView.setImageResource(R.drawable.seguir); // Cambia la imagen instantáneamente
                holder.seguirImageView.setTag(false); // Actualizar estado en la etiqueta

                fireStoreHelper.unfollowUser(currentUserId, targetUsername, new FireStoreHelper.FirestoreCallback2() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context, "Dejaste de seguir a " + user.getUsername(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(context, "Error al dejar de seguir: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Seguir
                holder.seguirImageView.setImageResource(R.drawable.noseguir); // Cambia la imagen instantáneamente
                holder.seguirImageView.setTag(true); // Actualizar estado en la etiqueta

                fireStoreHelper.followUser(currentUserId, targetUsername, new FireStoreHelper.FirestoreCallback2() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context, "Sigues a " + user.getUsername(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(context, "Error al seguir: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView userNameTextView;
        ImageView seguirImageView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            seguirImageView = itemView.findViewById(R.id.SeguirImageView);


        }
    }
}
