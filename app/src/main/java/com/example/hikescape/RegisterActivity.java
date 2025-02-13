package com.example.hikescape;

import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private DatabaseHelper databaseHelper;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicialización de DatabaseHelper y FirebaseAuth
        databaseHelper = new DatabaseHelper(this);
        mAuth = FirebaseAuth.getInstance();

        // Configuración de Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // ID del cliente de Firebase desde strings.xml
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Referencias a las vistas
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        EditText confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        Button registerButton = findViewById(R.id.registerButton);
        ImageView googleSignInButton = findViewById(R.id.googleSignInButton);

        // Registro manual
        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            if (validateInputs(username, email, password, confirmPassword)) {
                boolean isInserted = databaseHelper.insertUser(username, email, password);
                if (isInserted) {
                    Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    clearInputs(usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText);
                    finish();  // Vuelve a la actividad anterior
                } else {
                    Toast.makeText(RegisterActivity.this, "El correo electrónico ya está registrado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Registro con Google
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());
    }

    // Método para iniciar el flujo de Google Sign-In
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

    // Método para autenticar al usuario en Firebase con Google
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(this, "Registro exitoso con Google: " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                }
                finish();  // Vuelve a la actividad anterior
            } else {
                Toast.makeText(this, "Error al autenticar con Google", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para validar las entradas del formulario
    private boolean validateInputs(String username, String email, String password, String confirmPassword) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Método para limpiar los campos después de un registro exitoso
    private void clearInputs(EditText usernameEditText, EditText emailEditText, EditText passwordEditText, EditText confirmPasswordEditText) {
        usernameEditText.setText("");
        emailEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
    }
}
