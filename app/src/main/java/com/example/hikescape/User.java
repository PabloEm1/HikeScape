package com.example.hikescape;

public class User {
    private int id;
    private String username;
    private String profileImageUri;

    public User(int id, String username, String profileImageUri) {
        this.id = id;
        this.username = username;
        this.profileImageUri = profileImageUri;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }
}
