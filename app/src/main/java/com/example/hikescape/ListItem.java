package com.example.hikescape;

public class ListItem {
    public static final int TYPE_ROUTE = 0;
    public static final int TYPE_USER = 1;

    private int type;
    private String name;
    private String description; // Solo para rutas
    private int imageResource; // Opcional: Imagen de perfil o ruta

    public ListItem(int type, String name, String description, int imageResource) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.imageResource = imageResource;
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

    public int getImageResource() {
        return imageResource;
    }
}
