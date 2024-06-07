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

public class Register extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText ageEditText;
    private EditText comarcaEditText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        ageEditText = findViewById(R.id.age);
        comarcaEditText = findViewById(R.id.comarca);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                String age = ageEditText.getText().toString();
                String comarca = comarcaEditText.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) ||
                        TextUtils.isEmpty(age) || TextUtils.isEmpty(comarca)) {
                    Toast.makeText(Register.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(username, password, age, comarca);
                }
            }
        });
    }

    private void registerUser(final String username, final String password, final String age, final String comarca) {
        // Crear un nuevo Thread para el registro
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Simular una operación de registro (por ejemplo, guardar en un servidor)
                boolean success = false;
                try {
                    // Simular un tiempo de espera para el registro
                    Thread.sleep(2000);
                    // Registro de usuario (esto es solo un ejemplo básico)
                    // Aquí podrías agregar la lógica para registrar el usuario, como guardar en una base de datos
                    success = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                final boolean finalSuccess = success;

                // Usar un Handler para actualizar la UI en el hilo principal
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (finalSuccess) {
                            Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Register.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}

