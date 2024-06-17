package com.example.agricultius;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
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
import java.util.Arrays;
import java.util.List;

public class Register extends AppCompatActivity {

    private EditText nom;
    private EditText cognom;
    private EditText edat;
    private EditText usuari;
    private EditText contrasenya;
    //private EditText contrasenya2;
    private Spinner comarcaSpinner;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        nom = findViewById(R.id.nom);
        cognom = findViewById(R.id.cognom);
        edat = findViewById(R.id.edat);
        usuari = findViewById(R.id.usuari);
        contrasenya = findViewById(R.id.contrasenya);
        //contrasenya2 = findViewById(R.id.confirm_password);
        comarcaSpinner = findViewById(R.id.comarca);
        registerButton = findViewById(R.id.register_button);

        List<String> comarques = Arrays.asList("Alt Camp", "Alt Empordà", "Alt Penedès", "Alt Urgell", "Alta Ribagorça", "Aran", "Bages", "Baix Camp", "Baix Ebre", "Baix Empordà", "Baix Llobregat", "Baix Penedès", "Barcelonès", "Berguedà", "Cerdanya", "La Conca de Barberà", "Garraf", "Garrigues", "Garrotxa", "Gironès", "Lluçanès", "Maresme", "Moianès", "Montsià", "Noguera", "Osona", "Pallars Jussà", "Pallars Sobirà", "Pla d'Urgell", "Pla de l'Estany", "Priorat", "Ribera d'Ebre", "Ripollès", "Segarra", "Segrià", "Selva", "Solsonès", "Tarragonès", "Terra Alta", "Urgell", "Vallès Occidental", "Vallès Oriental");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, comarques);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comarcaSpinner.setAdapter(adapter);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nom.getText().toString();
                String surname = cognom.getText().toString();
                String age = edat.getText().toString();
                String username = usuari.getText().toString();
                String password = contrasenya.getText().toString();
                //String confirmPassword = contrasenya2.getText().toString();
                String comarca = comarcaSpinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(surname) ||TextUtils.isEmpty(username) || TextUtils.isEmpty(password) /*|| TextUtils.isEmpty(confirmPassword)*/ ||
                        TextUtils.isEmpty(age) || TextUtils.isEmpty(comarca)) {
                    Toast.makeText(Register.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                //} else if (!password.equals(confirmPassword)) {
                //    Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(name, surname, age, username, password, comarca);
                }
            }
        });
    }

    private void registerUser(final String nom, final String cognom, final String edat, final String usuari, final String contrasenya, final String comarca) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream stream = null;
                final String[] result = {null};
                Handler handler = new Handler(Looper.getMainLooper());

                try {
                    String query = "http://10.0.2.2:9000/Application/RegistrarAgricultor";
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

                    String params = "nom=" + URLEncoder.encode(nom, "UTF-8") +
                            "&cognom=" + URLEncoder.encode(cognom, "UTF-8") +
                            "&edat=" + URLEncoder.encode(edat, "UTF-8") +
                            "&usuari=" + URLEncoder.encode(usuari, "UTF-8") +
                            "&contrasenya=" + URLEncoder.encode(contrasenya, "UTF-8") +
                            "&comarca=" + URLEncoder.encode(comarca, "UTF-8");
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
                        if (result[0].startsWith("{")) {
                            JSONObject response = new JSONObject(result[0]);
                            boolean success = response.getString("status").equals("success");
                            handler.post(new Runnable() {
                                public void run() {
                                    try {
                                        if (success) {
                                            Intent intent = new Intent(Register.this, Login.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            String message = response.has("message") ? response.getString("message") : "Error desconocido";
                                            Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(Register.this, "Error en el formato de la respuesta del servidor", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                public void run() {
                                    Toast.makeText(Register.this, "Respuesta inesperada del servidor", Toast.LENGTH_SHORT).show();
                                    Log.e("serverTest", "Respuesta inesperada: " + result[0]);
                                }
                            });
                        }

                    } else {
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(Register.this, "Error en la solicitud: " + responseCode, Toast.LENGTH_SHORT).show();
                                Log.e("serverTest", "Código de respuesta: " + responseCode);
                            }
                        });
                    }
                    conn.disconnect();

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(Register.this, "Error de red o de análisis de datos", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}

