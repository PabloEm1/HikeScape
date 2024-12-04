package com.example.hikescape;

public class FavoriteRoute {
    private String username;
    private String routeName;

    public FavoriteRoute(String username, String routeName) {
        this.username = username;
        this.routeName = routeName;
    }

    public String getUsername() {
        return username;
    }

    public String getRouteName() {
        return routeName;
    }
}
