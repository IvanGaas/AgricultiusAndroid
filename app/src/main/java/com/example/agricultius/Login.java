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
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Login extends AppCompatActivity {

    private EditText usuari;
    private EditText contrasenya;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuari = findViewById(R.id.usuari);
        contrasenya = findViewById(R.id.contrasenya);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usuari.getText().toString();
                String password = contrasenya.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                } else {
                    authenticateUser(username, password);
                }
            }
        });
    }

    private void authenticateUser(final String usuari, final String contrasenya) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream stream = null;
                final String[] result = {null};
                Handler handler = new Handler(Looper.getMainLooper());

                try {
                    String query = "http://10.0.2.2:9000/Application/Login";
                    URL url = new URL(query);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("X-Requested-With", "XMLHttpRequest"); // Header para identificar la solicitud
                    conn.connect();

                    String params = "usuari=" + URLEncoder.encode(usuari, "UTF-8") +
                            "&contrasenya=" + URLEncoder.encode(contrasenya, "UTF-8");
                    Log.i("serverTest", params);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(params);
                    writer.flush();
                    writer.close();
                    os.close();

                    Log.i("serverTest", "esperant resposta");

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        stream = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        result[0] = sb.toString();
                        Log.i("serverTest", result[0]);

                        try {
                            JSONObject response = new JSONObject(result[0]);
                            boolean success = response.getString("status").equals("success");
                            handler.post(new Runnable() {
                                public void run() {
                                    try {
                                        if (success) {
                                            Intent intent = new Intent(Login.this, Principal.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            String message = response.has("message") ? response.getString("message") : "Error desconocido";
                                            Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(Login.this, "Error en el formato de la respuesta del servidor", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            handler.post(new Runnable() {
                                public void run() {
                                    Toast.makeText(Login.this, "Respuesta inesperada del servidor", Toast.LENGTH_SHORT).show();
                                    Log.e("serverTest", "Respuesta inesperada: " + result[0]);
                                }
                            });
                        }
                    } else {
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(Login.this, "Error en la solicitud: " + responseCode, Toast.LENGTH_SHORT).show();
                                Log.e("serverTest", "Código de respuesta: " + responseCode);
                            }
                        });
                    }
                    conn.disconnect();

                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(Login.this, "Error de red o de análisis de datos", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}