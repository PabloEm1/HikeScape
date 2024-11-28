package com.example.hikescape;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Nombre de la base de datos y tabla
    private static final String DATABASE_NAME = "HikeScape.db";
    private static final String TABLE_USERS = "users";

    // Columnas de la tabla
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla de usuarios
        String createTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT, "
                + COLUMN_EMAIL + " TEXT, "
                + COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
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
        return result != -1; // Devuelve true si se inserta correctamente
    }

    // Método para verificar credenciales en el login
    public boolean checkUser(String identifier, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query;
        String[] args;

        // Registro de depuración para verificar qué se pasa al método
        Log.d("DatabaseHelper", "Identificador: " + identifier + " y Contraseña: " + password);

        // Usamos el nombre de usuario y la contraseña
        if (identifier.contains("@")) { // Si el identificador contiene '@', es un email
            query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        } else { // Si no, asumimos que es un nombre de usuario
            query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        }
        args = new String[]{identifier, password}; // Usamos el email y la contraseña

        // Ejecución de la consulta
        Cursor cursor = db.rawQuery(query, args);

        // Registro de depuración para verificar el resultado de la consulta
        Log.d("DatabaseHelper", "Resultado de la consulta: " + cursor.getCount());

        boolean result = cursor.getCount() > 0; // Si encuentra algún registro, devuelve true
        cursor.close();
        return result;
    }
}