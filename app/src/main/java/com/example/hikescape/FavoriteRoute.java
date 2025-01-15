package com.example.hikescape;

public class FavoriteRoute {
    private String username;
    private String routeName;
    private String imageUrl; // Imagen de la ruta

    public FavoriteRoute(String username, String routeName, String imageUrl) {
        this.username = username;
        this.routeName = routeName;
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
