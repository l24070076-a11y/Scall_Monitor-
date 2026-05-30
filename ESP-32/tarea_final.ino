#include <DHT.h>
#include <WiFi.h>
#include <HTTPClient.h>

#define DHTTYPE DHT11
#define DHTPIN 13

DHT HT(DHTPIN, DHTTYPE);

volatile double waterFlow;
float humidity;
float tempC;

// Tus datos de WiFi
const char* ssid = "ALUMNOS-ITSVA";
const char* password = "";

// URL de ngrok (dominio estático)
const char* serverURL = "https://crane-commerce-foam.ngrok-free.dev/datos";

void IRAM_ATTR pulse() {
  waterFlow += 1.0 / 450.0;
}

void setup() {
  Serial.begin(9600);
  waterFlow = 0;
  attachInterrupt(digitalPinToInterrupt(4), pulse, RISING);
  HT.begin();

  // Conectar WiFi
  WiFi.begin(ssid, password);
  Serial.print("Conectando al WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi conectado ✅");
  Serial.println(WiFi.localIP());
}

void loop() {
  humidity = HT.readHumidity();
  tempC = HT.readTemperature();

  Serial.print("Flujo: "); Serial.println(waterFlow);
  Serial.print("Humedad: "); Serial.println(humidity);
  Serial.print("Temperatura: "); Serial.println(tempC);

  // Enviar datos al servidor
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(serverURL);
    http.addHeader("Content-Type", "application/json");
    http.addHeader("ngrok-skip-browser-warning", "true");

    String jsonData = "{\"temperatura\":" + String(tempC) + 
                      ",\"humedad\":" + String(humidity) + 
                      ",\"flujo\":" + String(waterFlow) + "}";

    int httpCode = http.POST(jsonData);
    Serial.println("Respuesta servidor: " + String(httpCode));
    http.end();
  }

  delay(35000); // 34 segundos
}