package com.example.hikescape;

public class Post {
    private String userName;
    private int imageResource;

    public Post(String userName, int imageResource) {
        this.userName = userName;
        this.imageResource = imageResource;
    }

    public String getUserName() {
        return userName;
    }

    public int getImageResource() {
        return imageResource;
    }
}

