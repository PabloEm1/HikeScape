package com.example.hikescape;

public class ListItem {
    public static final int TYPE_ROUTE = 0;
    public static final int TYPE_USER = 1;

    private int type;
    private String name;
    private String description;
    private String imageUri; // URI de la imagen de la publicación
    private String userName; // Nombre de usuario
    private String profileImageUri; // URI de la imagen de perfil

    public ListItem(int type, String name, String description, String imageUri, String userName, String profileImageUri) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.imageUri = imageUri;
        this.userName = userName;
        this.profileImageUri = profileImageUri;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getUserName() {
        return userName;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }
}