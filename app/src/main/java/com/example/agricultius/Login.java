package com.example.agricultius;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

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
            InputStream stream = null;
            String result = null;
            Handler handler = new Handler();
            @Override
            public void run() {
                // Simular una operación de autenticación (por ejemplo, verificar con un servidor)
                boolean success = false;
                try {
                    // Simular un tiempo de espera para la autenticación
                   // Thread.sleep(2000);
                    // Verificación de credenciales (esto es solo un ejemplo básico)
                   // if (username.equals("user") && password.equals("password")) {
                     //   success = true;
                 //   }
                    String query = String.format("http://localhost:9000/Application/Login");
                    URL url = new URL(query);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 );
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.connect();


                    // enviar paràmetres de la funció Login en el cos del missatge

                    String params = "username="+username + "&password="+ password;
                    Log.i("serverTest ", params);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(params.toString());

                    writer.flush();
                    writer.close();
                    os.close();

                    Log.i("serverTest ", "esperant resposta");

                    //obtenir resposta del servidor
                    stream = conn.getInputStream();
                    BufferedReader reader = null;
                    StringBuilder sb = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    result = sb.toString();
                    conn.disconnect();
                    // Mostrar resultat en el quadre de text.
                    // Codi incorrecte
                    // EditText n = (EditText) findViewById (R.id.edit_message);
                    //n.setText(result);

                    //Codi correcte
                    Log.i("serverTest", result);
                    handler.post(new Runnable() {
                        public void run() {
                            TextView n = (TextView) findViewById (R.id.main);
                            n.setText(result);
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
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

