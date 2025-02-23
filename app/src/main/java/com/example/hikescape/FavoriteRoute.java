package com.example.hikescape;

public class FavoriteRoute {
    private String routeName;
    private String username;
    private String imageUrl;

    public FavoriteRoute(String routeName, String username,  String imageUrl) {

        this.username = username;
        this.routeName = routeName;
        this.imageUrl = imageUrl;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
