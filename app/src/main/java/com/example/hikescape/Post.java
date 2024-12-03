package com.example.hikescape;

public class Post {
    private String userName;
    private int imageResource;
    private boolean liked;  // Nuevo atributo para controlar si el post ha sido marcado como "me gusta"

    public Post(String userName, int imageResource) {
        this.userName = userName;
        this.imageResource = imageResource;
        this.liked = false;  // Inicializamos en falso (sin "me gusta")
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
}
