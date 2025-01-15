package com.example.hikescape;

public class FavoriteRoute {
    private int routeId; // Identificador único de la ruta
    private String username;
    private String routeName;
    private String imageUrl; // (Opcional: si usas URL directamente en la clase)

    public FavoriteRoute(int routeId, String username, String routeName, String imageUrl) {
        this.routeId = routeId;
        this.username = username;
        this.routeName = routeName;
        this.imageUrl = imageUrl;
    }

    // Getters y setters
    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

