package com.example.agricultius;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Consulta extends AppCompatActivity {

    private ListView usuariosListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        usuariosListView = findViewById(R.id.usuarios_list_view);

        fetchUsuarios();
    }

    private void fetchUsuarios() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream stream = null;
                final String[] result = {null};
                Handler handler = new Handler(Looper.getMainLooper());

                try {
                    String query = "http://10.0.2.2:9000/Application/getUsuaris";
                    URL url = new URL(query);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();

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

                        handler.post(new Runnable() {
                            public void run() {
                                try {
                                    JSONArray jsonArray = new JSONArray(result[0]);
                                    List<String> usuarios = new ArrayList<>();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        String usuario = jsonArray.getString(i);
                                        usuarios.add(usuario);
                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Consulta.this,
                                            android.R.layout.simple_list_item_1, usuarios);
                                    usuariosListView.setAdapter(adapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(Consulta.this, "Error en el formato de la respuesta del servidor", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(Consulta.this, "Error en la solicitud: " + responseCode, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    conn.disconnect();

                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(Consulta.this, "Error de red o de análisis de datos", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}