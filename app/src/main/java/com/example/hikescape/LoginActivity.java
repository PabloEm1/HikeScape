package com.example.hikescape;

import android.content.Intent;
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