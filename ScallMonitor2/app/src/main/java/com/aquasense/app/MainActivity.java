package com.aquasense.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvRegistro;
    RequestQueue queue;

    private static final String URL_LOGIN = "https://crane-commerce-foam.ngrok-free.dev/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegistro = findViewById(R.id.tvRegistro);
        queue = Volley.newRequestQueue(this);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            loginUsuario(email, password);
        });

        tvRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistroActivity.class);
            startActivity(intent);
        });
    }

    private void loginUsuario(String email, String password) {
        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("contrasena", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, URL_LOGIN, body,
                response -> {
                    try {
                        // Log para ver la respuesta completa
                        android.util.Log.d("LOGIN_RESPONSE", "Respuesta: " + response.toString());

                        boolean success = response.getBoolean("success");
                        android.util.Log.d("LOGIN_RESPONSE", "Success: " + success);

                        if (success) {
                            int idSesion = response.getInt("id_sesion");
                            android.util.Log.d("LOGIN_RESPONSE", "ID Sesion: " + idSesion);

                            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                            prefs.edit().putInt("id_sesion", idSesion).apply();

                            Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, MainActivitypantalla2.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String mensaje = response.getString("mensaje");
                            android.util.Log.d("LOGIN_RESPONSE", "Mensaje error: " + mensaje);
                            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        android.util.Log.e("LOGIN_ERROR", "JSON error: " + e.toString());
                        e.printStackTrace();
                    }
                },
                error -> {
                    android.util.Log.e("LOGIN_ERROR", "Volley error: " + error.toString());
                    Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
        );


        queue.add(request);
    }
}