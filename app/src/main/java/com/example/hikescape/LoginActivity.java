package com.example.hikescape;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        // Referencia a los campos de entrada
        EditText emailOrUsernameEditText = findViewById(R.id.usernameEditText); // Campo para correo o usuario
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String emailOrUsername = emailOrUsernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            // Validaciones
            if (emailOrUsername.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(emailOrUsername).matches() && emailOrUsername.contains("@")) {
                Toast.makeText(LoginActivity.this, "Por favor ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show();
            } else {
                // Determinar si es un correo o nombre de usuario y verificar credenciales
                boolean isValid = databaseHelper.checkUser(this,emailOrUsername, password);
                if (isValid) {
                    Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                    // Obtener el nombre de usuario desde la base de datos, dependiendo de si es correo o nombre de usuario
                    String username;
                    if (Patterns.EMAIL_ADDRESS.matcher(emailOrUsername).matches()) {
                        // Si es un correo, obtener el nombre de usuario de la base de datos
                        username = databaseHelper.getUsernameFromEmail(emailOrUsername);  // Método para obtener nombre de usuario desde correo
                    } else {
                        // Si es un nombre de usuario, usarlo directamente
                        username = emailOrUsername;
                    }

                    // Guardar nombre del usuario en SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);  // Guardamos el nombre de usuario
                    editor.apply();  // Aplicamos los cambios


                    // Navegar a la siguiente actividad (por ejemplo, MenuActivity)
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciales no válidas", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}