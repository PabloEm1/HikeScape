package com.example.hikescape;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private int postId;  // Identificador único
    private String userName;  // Nombre del usuario
    private int imageResource;  // Recurso de imagen
    private boolean liked;  // Indicador de "me gusta"
    private int likeCount;  // Contador de "me gusta"
    private List<String> comments;  // Lista de comentarios

    // Constructor con todos los atributos
    public Post(int postId, String userName, int imageResource, int likeCount) {
        this.postId = postId;
        this.userName = userName;
        this.imageResource = imageResource;
        this.likeCount = likeCount;  // Puedes inicializarlo en 0 si no se pasa un valor
        this.liked = false;  // Por defecto, no tiene "me gusta"
        this.comments = new ArrayList<>();  // Inicializamos la lista de comentarios
    }

    // Getters y setters
    public int getPostId() {
        return postId;
    }

    public String getUserName() {
        return userName;
    }

    public int getImageResource() {
        return imageResource;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public List<String> getComments() {
        return comments;
    }

    public void addComment(String comment) {
        this.comments.add(comment);
    }

    public void removeComment(String comment) {
        this.comments.remove(comment);
    }
}
