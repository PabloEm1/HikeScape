package com.example.hikescape;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private DatabaseHelper databaseHelper;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);
        mAuth = FirebaseAuth.getInstance();

        // Configuración de Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // ID del cliente de Firebase
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Referencia a los campos de entrada
        EditText emailOrUsernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        ImageView googleSignInButton = findViewById(R.id.googleSignInButton);  // Asegúrate de agregar este botón en tu XML

        // Inicio de sesión manual
        loginButton.setOnClickListener(v -> {
            String emailOrUsername = emailOrUsernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            if (emailOrUsername.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(emailOrUsername).matches() && emailOrUsername.contains("@")) {
                Toast.makeText(LoginActivity.this, "Por favor ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show();
            } else {
                boolean isValid = databaseHelper.checkUser(this, emailOrUsername, password);
                if (isValid) {
                    Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                    String username = Patterns.EMAIL_ADDRESS.matcher(emailOrUsername).matches() ?
                            databaseHelper.getUsernameFromEmail(emailOrUsername) : emailOrUsername;

                    // Guardar el nombre de usuario en SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.apply();

                    // Navegar a la siguiente actividad
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciales no válidas", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Inicio de sesión con Google
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnSuccessListener(this::firebaseAuthWithGoogle)
                    .addOnFailureListener(e -> Toast.makeText(this, "Error en Google Sign-In: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    // Guardar el nombre del usuario en SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", user.getDisplayName());
                    editor.apply();

                    Toast.makeText(this, "Inicio de sesión exitoso con Google: " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    // Navegar a la siguiente actividad
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                Toast.makeText(this, "Error al autenticar con Google", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
