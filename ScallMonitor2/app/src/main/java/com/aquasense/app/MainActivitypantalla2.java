package com.aquasense.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class MainActivitypantalla2 extends AppCompatActivity {

    private static final String URL_MEDICIONES = "https://crane-commerce-foam.ngrok-free.dev/mediciones";
    private static final String URL_HISTORIAL = "https://crane-commerce-foam.ngrok-free.dev/historial";

    TextView tvVolumen, tvTemp, tvFlujo, tvHumedad;
    Button btnLogout;
    RequestQueue queue;
    Handler handler = new Handler();
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activitypantalla2);
        getWindow().setDecorFitsSystemWindows(false);

        tvTemp = findViewById(R.id.tvTemp);
        tvFlujo = findViewById(R.id.tvFlujo);
        tvHumedad = findViewById(R.id.tvHumedad);
        tvVolumen = findViewById(R.id.tvVolumen);
        btnLogout = findViewById(R.id.btnLogout);

        queue = Volley.newRequestQueue(this);

        runnable = new Runnable() {
            @Override
            public void run() {
                cargarMediciones();
                cargarVolumen();
                cargarHistorial();
                revisarAlertas();
                verificarAlertas();
                handler.postDelayed(this, 45000);
            }
        };
        handler.post(runnable);

        btnLogout.setOnClickListener(v -> cerrarSesion());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private void cerrarSesion() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int idSesion = prefs.getInt("id_sesion", -1);

        if (idSesion != -1) {
            JSONObject body = new JSONObject();
            try {
                body.put("id_sesion", idSesion);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    "https://crane-commerce-foam.ngrok-free.dev/logout",
                    body,
                    response -> {
                        prefs.edit().remove("id_sesion").apply();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    },
                    error -> {
                        Toast.makeText(this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
                    }
            );
            queue.add(request);
        } else {
            // Si no hay sesión guardada, solo vuelve al login
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void cargarMediciones() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, URL_MEDICIONES, null,
                response -> {
                    try {
                        float lastTemp = 0, lastFlujo = 0, lastHumedad = 0;

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String tipo = obj.getString("tipo_sensor");
                            float valor = (float) obj.getDouble("valor");

                            switch (tipo) {
                                case "Temperatura": lastTemp = valor; break;
                                case "Flujo de agua": lastFlujo = valor; break;
                                case "Humedad": lastHumedad = valor; break;
                            }
                        }

                        tvTemp.setText(String.valueOf(lastTemp));
                        tvFlujo.setText(String.valueOf(lastFlujo));
                        tvHumedad.setText(String.valueOf(lastHumedad));

                    } catch (JSONException e) { e.printStackTrace(); }
                },
                error -> error.printStackTrace()
        );
        queue.add(request);
    }

    private void cargarHistorial() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, URL_HISTORIAL, null,
                response -> {
                    try {
                        ArrayList<Entry> tempEntries = new ArrayList<>();
                        ArrayList<Entry> flujoEntries = new ArrayList<>();
                        ArrayList<Entry> humedadEntries = new ArrayList<>();
                        int tempIdx = 0, flujoIdx = 0, humedadIdx = 0;

                        for (int i = response.length() - 1; i >= 0; i--) {
                            JSONObject obj = response.getJSONObject(i);
                            String tipo = obj.getString("tipo_sensor");
                            float valor = (float) obj.getDouble("valor");

                            switch (tipo) {
                                case "Temperatura":
                                    tempEntries.add(new Entry(tempIdx++, valor));
                                    break;
                                case "Flujo de agua":
                                    flujoEntries.add(new Entry(flujoIdx++, valor));
                                    break;
                                case "Humedad":
                                    humedadEntries.add(new Entry(humedadIdx++, valor));
                                    break;
                            }
                        }

                        setupChart(R.id.chartTemp, tempEntries, "#EF9F27", "#33EF9F27");
                        setupChart(R.id.chartFlujo, flujoEntries, "#378ADD", "#33378ADD");
                        setupChart(R.id.chartHumedad, humedadEntries, "#7DD3B8", "#337DD3B8");

                    } catch (JSONException e) { e.printStackTrace(); }
                },
                error -> error.printStackTrace()
        );
        queue.add(request);
    }

    private void cargarVolumen() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                "https://crane-commerce-foam.ngrok-free.dev/volumen",
                null,
                response -> {
                    try {
                        double volumen = response.getDouble("volumen_total");
                        tvVolumen.setText(volumen + " L");
                    } catch (JSONException e) {
                        tvVolumen.setText("0.0 L");
                    }
                },
                error -> tvVolumen.setText("0.0 L")
        );
        queue.add(request);
    }

    private void revisarAlertas() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                "https://crane-commerce-foam.ngrok-free.dev/verificar-alertas",
                null,
                response -> {},
                error -> error.printStackTrace()
        );
        queue.add(request);
    }

    private void verificarAlertas() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                "https://crane-commerce-foam.ngrok-free.dev/alertas",
                null,
                response -> {
                    try {
                        if (response.length() > 0) {
                            StringBuilder mensajes = new StringBuilder();
                            int ultimoIdAlerta = 0; // Variable para guardar el ID

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                mensajes.append("⚠️ ").append(obj.getString("mensaje")).append("\n");
                                ultimoIdAlerta = obj.getInt("id_alerta"); // Guarda el último ID
                            }
                            // Pasa el ID como segundo parámetro
                            mostrarAlerta(mensajes.toString(), ultimoIdAlerta);
                        }
                    } catch (JSONException e) { e.printStackTrace(); }
                },
                error -> error.printStackTrace()
        );
        queue.add(request);
    }
    private void mostrarAlerta(String mensaje, int idAlerta) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("⚠️ Alerta del sistema")
                .setMessage(mensaje)
                .setPositiveButton("Entendido", (dialog, which) -> {
                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.POST,
                            "https://crane-commerce-foam.ngrok-free.dev/atender-alertas",
                            null,
                            response -> {},
                            error -> error.printStackTrace()
                    );
                    queue.add(request);
                })
                .setNegativeButton("Eliminar", (dialog, which) -> eliminarAlerta(idAlerta))
                .show();
    }

    private void setupChart(int chartId, ArrayList<Entry> entries, String lineColor, String fillColor) {
        LineChart chart = findViewById(chartId);
        if (entries.isEmpty()) return;

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(Color.parseColor(lineColor));
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor(fillColor));
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        chart.setData(new LineData(dataSet));
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.invalidate();
    }



// ELIMINAR ALERTAS POR USUARIO
    private void eliminarAlerta(int idAlerta) {
        String url = "https://crane-commerce-foam.ngrok-free.dev/alerta/" + idAlerta;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE, url, null,
                response -> {
                    try {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show();
                        verificarAlertas(); // Recargar alertas
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error al eliminar alerta", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }

}


