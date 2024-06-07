package com.example.agricultius;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                } else {
                    authenticateUser(username, password);
                }
            }
        });
    }

    private void authenticateUser(final String username, final String password) {
        // Crear un nuevo Thread para la autenticación
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Simular una operación de autenticación (por ejemplo, verificar con un servidor)
                boolean success = false;
                try {
                    // Simular un tiempo de espera para la autenticación
                    Thread.sleep(2000);
                    // Verificación de credenciales (esto es solo un ejemplo básico)
                    if (username.equals("user") && password.equals("password")) {
                        success = true;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                final boolean finalSuccess = success;

                // Usar un Handler para actualizar la UI en el hilo principal
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (finalSuccess) {
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}

