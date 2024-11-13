package com.example.hikescape;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Retrasar la navegación a FeedActivity por 2 segundos (2000 ms)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Crear un Intent para iniciar FeedActivity
                Intent intent = new Intent(HomeActivity.this, FeedActivity.class);
                startActivity(intent);
                // Finalizar la actividad actual para que no vuelva al presionar "atrás"
                finish();
            }
        }, 2000); // 2000 milisegundos = 2 segundos
    }
}
