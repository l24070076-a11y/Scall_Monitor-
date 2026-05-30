package com.aquasense.app;

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

public class RegistroActivity extends AppCompatActivity {

    EditText etNombre, etEmail, etTelefono, etPassword;
    Button btnRegistrar;
    TextView tvVolverLogin;
    RequestQueue queue;

    private static final String URL_REGISTRO = "https://crane-commerce-foam.ngrok-free.dev/registro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etTelefono = findViewById(R.id.etTelefono);
        etPassword = findViewById(R.id.etPassword);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        tvVolverLogin = findViewById(R.id.tvVolverLogin);
        queue = Volley.newRequestQueue(this);

        btnRegistrar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            registrarUsuario(nombre, email, telefono, password);
        });

        tvVolverLogin.setOnClickListener(v -> finish());
    }

    private void registrarUsuario(String nombre, String email, String telefono, String password) {
        JSONObject body = new JSONObject();
        try {
            body.put("nombre", nombre);
            body.put("email", email);
            body.put("telefono", telefono);
            body.put("contrasena", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, URL_REGISTRO, body,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            Toast.makeText(this, "¡Registro exitoso! Ahora inicia sesión", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error de conexión: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );
        queue.add(request);
    }
}