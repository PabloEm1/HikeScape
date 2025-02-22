package com.example.hikescape;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private String user_id;
    private int postId;  // Identificador único
    private String id_routes;
    private int userId;  // ID del autor de la publicación
    private String postName; //Nombre de la ruta
    private String postDescription; //Descripcion de la ruta
    private String userName;  // Nombre del usuario
    private String imageUri;  // URI de la imagen
    private boolean liked;  // Indicador de "me gusta"
    private boolean save;  // Indicador de "guardado"
    private int likeCount;  // Contador de "me gusta"
    private List<String> comments;  // Lista de comentarios

    // Constructor con todos los atributos
    public Post(int postId, int userId, String userName, String imageUri,String postName, String postDescription ,int likeCount) {
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;
        this.imageUri = imageUri;  // Ahora es una URI en lugar de un recurso estático
        this.likeCount = likeCount;
        this.postName = postName;
        this.postDescription= postDescription;
        this.liked = false;  // Por defecto, no tiene "me gusta"
        this.save = false;
        this.comments = new ArrayList<>();  // Inicializamos la lista de comentarios
    }

    public Post(String id_routes, String userName, String imageUri,String postName, String postDescription ,int likeCount) {
        this.id_routes = id_routes;
        this.userName = userName;
        this.imageUri = imageUri;  // Ahora es una URI en lugar de un recurso estático
        this.likeCount = likeCount;
        this.postName = postName;
        this.postDescription= postDescription;
        this.liked = false;  // Por defecto, no tiene "me gusta"
        this.save = false;
        this.comments = new ArrayList<>();  // Inicializamos la lista de comentarios
    }

    // Getters y setters
    public int getPostId() {
        return postId;
    }
    public int getLikes(){return likeCount; }
    public String getId_routes(){return id_routes;}

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getPostName(){return postName;}

    public String getPostDescription(){return postDescription;}

    public int getUserId() {  // Nuevo método para obtener el userId
        return userId;
    }

    public void setUserId(int userId) {  // Nuevo método para establecer el userId
        this.userId = userId;
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

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
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
