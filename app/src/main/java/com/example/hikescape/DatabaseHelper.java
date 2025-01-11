package com.example.hikescape;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Nombre de la base de datos y las tablas
    private static final String DATABASE_NAME = "HikeScape.db";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_RUTAS = "rutas";
    private static final String TABLE_COMENTARIOS = "comentarios";
    private static final String TABLE_LIKES = "likes";
    private static final String TABLE_FAVORITES = "favorites";

    // Columnas de la tabla `users`
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    // Columnas de la tabla `rutas`
    private static final String COLUMN_RUTA_ID = "id_ruta";
    private static final String COLUMN_RUTA_USER_ID = "fk_id_user";
    private static final String COLUMN_NOMBRE_RUTA = "nombre_ruta";
    private static final String COLUMN_DESCRIPCION = "descripcion";
    private static final String COLUMN_FOTO = "foto";
    private static final String COLUMN_DIFICULTAD = "dificultad";

    // Columnas de la tabla `comentarios`
    private static final String COLUMN_COMENTARIO_ID = "id_comentario";
    private static final String COLUMN_COMENTARIO_RUTA_ID = "fk_id_ruta";
    private static final String COLUMN_COMENTARIO_USER_ID = "fk_id_user";
    private static final String COLUMN_MENSAJE = "mensaje";
    private static final String COLUMN_FECHA = "fecha";

    // Columnas de la tabla `likes`
    private static final String COLUMN_LIKE_RUTA_ID = "fk_id_ruta";
    private static final String COLUMN_LIKE_USER_ID = "fk_id_user";
    private static final String COLUMN_LIKE_FECHA = "fecha";

    //Columnas de la tabla 'favorites'
    private static final String COLUMN_FAVORITES_ID = "id_favorito";
    private static final String COLUMN_FAVORITES_USER_ID = "fk_id_user";
    private static final String COLUMN_FAVORITES_RUTA_ID = "fk_id_ruta";
    private static final String COLUMN_FAVORITES_FECHA = "fecha_favorites";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla de usuarios
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT, "
                + COLUMN_EMAIL + " TEXT UNIQUE, "
                + COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createUsersTable);

        // Crear tabla de rutas
        String createRutasTable = "CREATE TABLE " + TABLE_RUTAS + " (" +
                COLUMN_RUTA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RUTA_USER_ID + " INTEGER, " +
                COLUMN_NOMBRE_RUTA + " TEXT, " +
                COLUMN_DESCRIPCION + " TEXT, " +
                COLUMN_FOTO + " TEXT, " +
                COLUMN_DIFICULTAD + " TEXT CHECK(" + COLUMN_DIFICULTAD + " IN ('Fácil', 'Medio', 'Difícil', 'Muy Difícil')), " +
                "FOREIGN KEY(" + COLUMN_RUTA_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE)";
        db.execSQL(createRutasTable);


        // Crear tabla de comentarios
        String createComentariosTable = "CREATE TABLE " + TABLE_COMENTARIOS + " ("
                + COLUMN_COMENTARIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_COMENTARIO_RUTA_ID + " INTEGER, "
                + COLUMN_COMENTARIO_USER_ID + " INTEGER, "
                + COLUMN_MENSAJE + " TEXT, "
                + COLUMN_FECHA + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(" + COLUMN_COMENTARIO_RUTA_ID + ") REFERENCES " + TABLE_RUTAS + "(" + COLUMN_RUTA_ID + ") ON DELETE CASCADE, "
                + "FOREIGN KEY(" + COLUMN_COMENTARIO_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE)";
        db.execSQL(createComentariosTable);

        // Crear tabla de likes
        String createLikesTable = "CREATE TABLE " + TABLE_LIKES + " ("
                + COLUMN_LIKE_RUTA_ID + " INTEGER, "
                + COLUMN_LIKE_USER_ID + " INTEGER, "
                + COLUMN_LIKE_FECHA + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "PRIMARY KEY(" + COLUMN_LIKE_RUTA_ID + ", " + COLUMN_LIKE_USER_ID + "), "
                + "FOREIGN KEY(" + COLUMN_LIKE_RUTA_ID + ") REFERENCES " + TABLE_RUTAS + "(" + COLUMN_RUTA_ID + ") ON DELETE CASCADE, "
                + "FOREIGN KEY(" + COLUMN_LIKE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE)";
        db.execSQL(createLikesTable);

        //Crear tabla de favoritos
        String createFavoritesTable = "CREATE TABLE " + TABLE_FAVORITES + " ("
                + COLUMN_FAVORITES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FAVORITES_USER_ID + " INTEGER, "
                + COLUMN_FAVORITES_RUTA_ID + " INTEGER, "
                + COLUMN_FAVORITES_FECHA + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(" + COLUMN_FAVORITES_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE, "
                + "FOREIGN KEY(" + COLUMN_FAVORITES_RUTA_ID + ") REFERENCES " + TABLE_RUTAS + "(" + COLUMN_RUTA_ID + ") ON DELETE CASCADE)";
        db.execSQL(createFavoritesTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUTAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMENTARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIKES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);
    }

    // Método para insertar un nuevo usuario
    public boolean insertUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, contentValues);
        return result != -1;
    }

    // Método para insertar una nueva ruta
    public boolean insertRuta(int userId, String nombreRuta, String descripcion, String foto, String dificultad) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_RUTA_USER_ID, userId);
        contentValues.put(COLUMN_NOMBRE_RUTA, nombreRuta);
        contentValues.put(COLUMN_DESCRIPCION, descripcion);
        contentValues.put(COLUMN_FOTO, foto);
        contentValues.put(COLUMN_DIFICULTAD, dificultad);
        long result = db.insert(TABLE_RUTAS, null, contentValues);
        return result != -1;
    }

    // Método para insertar un nuevo comentario
    public boolean insertComentario(int rutaId, int userId, String mensaje) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_COMENTARIO_RUTA_ID, rutaId);
        contentValues.put(COLUMN_COMENTARIO_USER_ID, userId);
        contentValues.put(COLUMN_MENSAJE, mensaje);
        long result = db.insert(TABLE_COMENTARIOS, null, contentValues);
        return result != -1;
    }

    // Método para insertar un nuevo like
    public boolean likeRuta(Context context, int rutaId) {
        int userId = getLoggedInUserId(context);

        if (userId == -1) {
            // Si no hay usuario autenticado, no permitimos dar un like
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LIKE_RUTA_ID, rutaId);
        contentValues.put(COLUMN_LIKE_USER_ID, userId);

        long result = db.insert(TABLE_LIKES, null, contentValues);
        return result != -1; // Retorna true si el like fue exitoso
    }

    public boolean unlikeRuta(Context context, int rutaId) {
        int userId = getLoggedInUserId(context);

        if (userId == -1) {
            // Si no hay usuario autenticado, no permitimos quitar un like
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_LIKES,
                COLUMN_LIKE_RUTA_ID + " = ? AND " + COLUMN_LIKE_USER_ID + " = ?",
                new String[]{String.valueOf(rutaId), String.valueOf(userId)});

        return rowsAffected > 0; // Retorna true si se eliminó el like
    }

    //Metodo para guardar publicacion
    public boolean saveFavorite(Context context, int rutaId) {
        int userId = getLoggedInUserId(context);

        if (userId == -1) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FAVORITES_USER_ID, userId);
        contentValues.put(COLUMN_FAVORITES_RUTA_ID, rutaId);

        long result = db.insert(TABLE_FAVORITES, null, contentValues);
        return result != 1; //retorna true si se guardó con éxito
    }

    //Metodo para eliminar de favoritos
    public boolean removeFavorite(Context context, int rutaId) {
        int userId = getLoggedInUserId(context);

        if (userId == -1) {
            // Si no hay usuario autenticado, no permitimos eliminar favoritos
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_FAVORITES,
                COLUMN_FAVORITES_USER_ID + " = ? AND " + COLUMN_FAVORITES_RUTA_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(rutaId)});

        return rowsAffected > 0;
    }

    ;

    // Método para verificar credenciales de usuario
    public boolean checkUser(Context context, String identifier, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query;
        String[] args;

        // Si el identificador contiene '@', lo tratamos como correo electrónico
        if (identifier.contains("@")) {
            query = "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
            args = new String[]{identifier, password};
        } else {
            // Si no, lo tratamos como nombre de usuario
            query = "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
            args = new String[]{identifier, password};
        }

        Cursor cursor = db.rawQuery(query, args);

        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(0); // Obtiene el ID del usuario
            cursor.close();
            saveLoggedInUserId(context, userId); // Guarda la sesión del usuario
            return true; // Credenciales válidas
        }

        if (cursor != null) {
            cursor.close();
        }

        return false; // Credenciales inválidas
    }

    public void saveLoggedInUserId(Context context, int userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId", userId);
        editor.apply(); // Guarda el cambio de manera asíncrona
    }

    public int getLoggedInUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("userId", -1); // -1 si no está autenticado
    }

    public void clearLoggedInUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Elimina todos los datos en "UserSession"
        editor.apply();
    }

    public List<Post> getAllRutas() {
        List<Post> rutasList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT r." + COLUMN_RUTA_ID + ", u." + COLUMN_USERNAME + ", r." + COLUMN_FOTO + ", " +
                "(SELECT COUNT(*) FROM " + TABLE_LIKES + " WHERE " + COLUMN_LIKE_RUTA_ID + " = r." + COLUMN_RUTA_ID + ") AS likes " +
                "FROM " + TABLE_RUTAS + " r " +
                "INNER JOIN " + TABLE_USERS + " u ON r." + COLUMN_RUTA_USER_ID + " = u." + COLUMN_USER_ID;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String username = cursor.getString(1);
                String rutaUri = cursor.getString(2); // La URI de la foto
                int likes = cursor.getInt(3);

                rutasList.add(new Post(id, username, rutaUri, likes));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return rutasList;
    }

    // Método para obtener publicaciones por ID de usuario
    public List<Post> getPostsByUserId(int userId) {
        List<Post> posts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta para obtener las rutas del usuario con el número de likes
        String query = "SELECT rutas.id_ruta, users.username, rutas.foto, " +
                "(SELECT COUNT(*) FROM likes WHERE likes.fk_id_ruta = rutas.id_ruta) AS likeCount " +
                "FROM rutas " +
                "INNER JOIN users ON rutas.fk_id_user = users.id " +
                "WHERE rutas.fk_id_user = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int postId = cursor.getInt(cursor.getColumnIndexOrThrow("id_ruta"));
                String userName = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("foto"));
                int likeCount = cursor.getInt(cursor.getColumnIndexOrThrow("likeCount"));

                // Crear una nueva instancia de Post usando el constructor correcto
                Post post = new Post(postId, userName, imageUri, likeCount);

                // Agregar el post a la lista
                posts.add(post);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return posts;
    }

    // Método para obtener las publicaciones favoritas de un usuario
    public List<Post> getFavoritePostsByUserId(int userId) {
        List<Post> favoritePosts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta para obtener las rutas favoritas del usuario junto con la información necesaria
        String query = "SELECT rutas." + COLUMN_RUTA_ID + ", users." + COLUMN_USERNAME + ", rutas." + COLUMN_FOTO + ", " +
                "(SELECT COUNT(*) FROM " + TABLE_LIKES + " WHERE " + COLUMN_LIKE_RUTA_ID + " = rutas." + COLUMN_RUTA_ID + ") AS likeCount " +
                "FROM " + TABLE_FAVORITES + " " +
                "INNER JOIN " + TABLE_RUTAS + " rutas ON " + TABLE_FAVORITES + "." + COLUMN_FAVORITES_RUTA_ID + " = rutas." + COLUMN_RUTA_ID + " " +
                "INNER JOIN " + TABLE_USERS + " users ON rutas." + COLUMN_RUTA_USER_ID + " = users." + COLUMN_USER_ID + " " +
                "WHERE " + TABLE_FAVORITES + "." + COLUMN_FAVORITES_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int postId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RUTA_ID));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
                String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOTO));
                int likeCount = cursor.getInt(cursor.getColumnIndexOrThrow("likeCount"));

                // Crear un objeto Post con los datos obtenidos
                Post post = new Post(postId, username, imageUri, likeCount);

                // Agregar la publicación a la lista de favoritos
                favoritePosts.add(post);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return favoritePosts;
    }


}