package com.example.hikescape;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // Local database helper
    DatabaseHelper databaseHelper;

    // Firebase Authentication
    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;

    // Activity result launcher for Google Sign-In
    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account);
                    } catch (ApiException e) {
                        Log.e(TAG, "Google sign-in failed", e);
                        Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize local database helper
        databaseHelper = new DatabaseHelper(this);

        // Google Sign-In configuration
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Replace with your client ID
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // References to UI components
        EditText emailOrUsernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        Button googleLoginButton = findViewById(R.id.googleLoginButton);

        // Login with email/username and password
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
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciales no válidas", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Login with Google
        googleLoginButton.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle: " + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Log.d(TAG, "signInWithCredential:success, user: " + (user != null ? user.getEmail() : "N/A"));
                        Toast.makeText(this, "Inicio de sesión exitoso con Google", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Fallo en la autenticación con Google", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
