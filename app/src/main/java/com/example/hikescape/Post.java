package com.example.hikescape;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private int postId;  // Identificador único
    private String userName;  // Nombre del usuario
    private String imageUri;  // URI de la imagen
    private boolean liked;  // Indicador de "me gusta"
    private int likeCount;  // Contador de "me gusta"
    private List<String> comments;  // Lista de comentarios

    // Constructor con todos los atributos
    public Post(int postId, String userName, String imageUri, int likeCount) {
        this.postId = postId;
        this.userName = userName;
        this.imageUri = imageUri;  // Ahora es una URI en lugar de un recurso estático
        this.likeCount = likeCount;
        this.liked = false;  // Por defecto, no tiene "me gusta"
        this.comments = new ArrayList<>();  // Inicializamos la lista de comentarios
    }



    // Getters y setters
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
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

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
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

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public void addComment(String comment) {
        this.comments.add(comment);
    }

    public void removeComment(String comment) {
        this.comments.remove(comment);
    }
}
