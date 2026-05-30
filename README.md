# 🌧️ ScallMonitor - Sistema de Monitoreo de Agua Pluvial

## 📋 Descripción
Sistema IoT para monitorear la captación de agua de lluvia. El proyecto incluye:
- **Hardware:** ESP32 con sensor de temperatura/humedad (DHT11) y sensor de flujo de agua (YF-S201)
- **Backend:** Servidor Node.js con Express y MySQL
- **Frontend:** Aplicación Android nativa (Java)

## 🎯 Funcionalidades
- ✅ Lectura de sensores en tiempo real
- ✅ Envío de datos cada 30 segundos vía WiFi
- ✅ Almacenamiento en base de datos MySQL
- ✅ App Android con dashboard interactivo
- ✅ Gráficas históricas
- ✅ Sistema de alertas (valores fuera de rango)
- ✅ Registro y autenticación de usuarios
- ✅ Registro de sesiones (inicio/cierre)
- ✅ CRUD completo: INSERT, SELECT, UPDATE, DELETE

## 🛠️ Tecnologías utilizadas

| Componente | Tecnología |
|------------|------------|
| Microcontrolador | ESP32-WROOM-32 |
| Sensores | DHT11 (temp/humedad), YF-S201 (flujo) |
| Backend | Node.js, Express |
| Base de datos | MySQL |
| App Android | Java, Volley, MPAndroidChart |
| Infraestructura | Ngrok (túnel HTTPS) |

## 📁 Estructura del proyecto
ScallMonitor/
├── esp32/ # Código del ESP32
│ └── scall_monitor.ino
├── backend/ # Servidor Node.js
│ ├── index.js
│ └── package.json
├── android/ # App Android
│ └── ScallMonitor2/
├── database/ # Script SQL
│ └── scall_monitor_iotec.sql
└── iniciar.bat # Script de inicio


## 🔧 Instalación y configuración

### 1. Base de datos (MySQL)
```sql
-- Ejecutar en MySQL Workbench
CREATE DATABASE scall_monitor_iotec;
USE scall_monitor_iotec;
-- Ejecutar el script SQL proporcionado

cd backend
npm install
node index.js


3. ESP32 (Arduino IDE)
Librerías necesarias:

DHT sensor library (Adafruit)

Adafruit Unified Sensor

Conexiones:

Sensor	Pin ESP32
DHT11 VCC	3.3V
DHT11 DATA	GPIO13
DHT11 GND	GND
YF-S201 Rojo	5V
YF-S201 Amarillo	GPIO4
YF-S201 Negro	GND
Configurar en código:

cpp
const char* ssid = "TU_WIFI";
const char* password = "TU_CONTRASEÑA";
const char* serverURL = "https://TU_DOMINIO.ngrok-free.dev/datos";
4. App Android
Abrir en Android Studio

Sincronizar dependencias

Configurar la URL del servidor en las clases Java

Ejecutar en dispositivo físico

📡 Endpoints del servidor
Método	Endpoint	Función
POST	/datos	Recibir mediciones del ESP32
POST	/registro	Registrar nuevo usuario
POST	/login	Iniciar sesión
POST	/logout	Cerrar sesión
GET	/mediciones	Últimas mediciones
GET	/historial	Historial (últimas 30)
GET	/volumen	Volumen captado hoy
GET	/alertas	Ver alertas activas
POST	/atender-alertas	Marcar alertas atendidas
DELETE	/alerta/:id	Eliminar alerta
DELETE	/mediciones-antiguas	Limpiar historial
🖼️ Capturas de pantalla
(Agrega aquí imágenes de tu app funcionando)

👤 Autor
Tu nombre - tunfer007@gmail.com

📅 Fecha
Mayo 2026

📚 Créditos
Proyecto desarrollado para la materia de Base de Datos / IoT.

text

4. Guarda con **Ctrl + S**

---

## Paso 4: Crear la estructura de carpetas

En VS Code, crea estas carpetas:

1. Haz clic derecho en el panel izquierdo → **New Folder**
2. Crea: `esp32`
3. Crea: `backend`
4. Crea: `android`
5. Crea: `database`

---

## Paso 5: Mover archivos a sus carpetas

**Mueve estos archivos (Corta y pega):**

| Desde | Hacia |
|-------|-------|
| `scall_monitor.ino` | `esp32/` |
| `index.js` | `backend/` |
| `package.json` | `backend/` |
| `package-lock.json` | `backend/` |
| `iniciar.bat` | `backend/` (o déjalo en raíz) |
| Tu script SQL | `database/` |
| Tu proyecto Android (carpeta completa) | `android/` |

---

## Paso 6: Crear archivo `package.json` dentro de `backend/`

Si no tienes `package.json`, créalo en la carpeta `backend/`:

```json
{
  "name": "scallmonitor-backend",
  "version": "1.0.0",
  "description": "Servidor Node.js para monitoreo de agua pluvial",
  "main": "index.js",
  "scripts": {
    "start": "node index.js"
  },
  "dependencies": {
    "express": "^4.18.2",
    "mysql2": "^3.6.0"
  }
}
Paso 7: Crear archivo iniciar.bat en la raíz
batch
@echo off
echo Iniciando Servidor ScallMonitor...
cd backend
start cmd /k "node index.js"
timeout /t 2
start cmd /k "ngrok http --domain=crane-commerce-foam.ngrok-free.dev 3000"
echo Servidor iniciado
pause
Paso 8: Subir todo a GitHub
En la terminal de VS Code (Ctrl + `), escribe:

bash
# Inicializar git
git init

# Agregar todos los archivos
git add .

# Ver qué archivos se van a subir
git status

# Crear el commit
git commit -m "Primera version completa de ScallMonitor - Proyecto IoT"

# Conectar con GitHub (crea el repositorio antes en GitHub.com)
git remote add origin https://github.com/TU_USUARIO/ScallMonitor.git

# Subir a GitHub
git branch -M main
git push -u origin main
Paso 9: Verificar
Ve a https://github.com/TU_USUARIO/ScallMonitor

Deberías ver todos tus archivos

El README.md se ve bonito automáticamente

¿Ya tienes los archivos listos o necesitas ayuda con algún paso? 😄

Esta respuesta es generada por AI, solo como referencia.


const char* ssid = "TU_WIFI";
const char* password = "TU_CONTRASEÑA";
const char* serverURL = "https://TU_DOMINIO.ngrok-free.dev/datos";



4. App Android
Abrir en Android Studio

Sincronizar dependencias

Configurar la URL del servidor en las clases Java

Ejecutar en dispositivo físico

📡 Endpoints del servidor
Método	Endpoint	Función
POST	/datos	Recibir mediciones del ESP32
POST	/registro	Registrar nuevo usuario
POST	/login	Iniciar sesión
POST	/logout	Cerrar sesión
GET	/mediciones	Últimas mediciones
GET	/historial	Historial (últimas 30)
GET	/volumen	Volumen captado hoy
GET	/alertas	Ver alertas activas
POST	/atender-alertas	Marcar alertas atendidas
DELETE	/alerta/:id	Eliminar alerta
DELETE	/mediciones-antiguas	Limpiar historial


👤 Autor
Tu nombre - tunfer007@gmail.com

📅 Fecha
Mayo 2026

📚 Créditos
Proyecto desarrollado para la materia de Base de Datos / IoT.



4. Guarda con **Ctrl + S**

---

## Paso 4: Crear la estructura de carpetas

En VS Code, crea estas carpetas:

1. Haz clic derecho en el panel izquierdo → **New Folder**
2. Crea: `esp32`
3. Crea: `backend`
4. Crea: `android`
5. Crea: `database`

---

## Paso 5: Mover archivos a sus carpetas

**Mueve estos archivos (Corta y pega):**

| Desde | Hacia |
|-------|-------|
| `scall_monitor.ino` | `esp32/` |
| `index.js` | `backend/` |
| `package.json` | `backend/` |
| `package-lock.json` | `backend/` |
| `iniciar.bat` | `backend/` (o déjalo en raíz) |
| Tu script SQL | `database/` |
| Tu proyecto Android (carpeta completa) | `android/` |

---

## Paso 6: Crear archivo `package.json` dentro de `backend/`

Si no tienes `package.json`, créalo en la carpeta `backend/`:

```json
{
  "name": "scallmonitor-backend",
  "version": "1.0.0",
  "description": "Servidor Node.js para monitoreo de agua pluvial",
  "main": "index.js",
  "scripts": {
    "start": "node index.js"
  },
  "dependencies": {
    "express": "^4.18.2",
    "mysql2": "^3.6.0"
  }
}



Paso 7: Crear archivo iniciar.bat en la raíz
batch
@echo off
echo Iniciando Servidor ScallMonitor...
cd backend
start cmd /k "node index.js"
timeout /t 2
start cmd /k "ngrok http --domain=crane-commerce-foam.ngrok-free.dev 3000"
echo Servidor iniciado
pause
Paso 8: Subir todo a GitHub
En la terminal de VS Code (Ctrl + `), escribe:

bash
# Inicializar git
git init

# Agregar todos los archivos
git add .

# Ver qué archivos se van a subir
git status

# Crear el commit
git commit -m "Primera version completa de ScallMonitor - Proyecto IoT"

# Conectar con GitHub (crea el repositorio antes en GitHub.com)
git remote add origin https://github.com/TU_USUARIO/ScallMonitor.git

# Subir a GitHub
git branch -M main
git push -u origin main
Paso 9: Verificar
Ve a https://github.com/TU_USUARIO/ScallMonitor

Deberías ver todos tus archivos

El README.md se ve bonito automáticamente

¿Ya tienes los archivos listos o necesitas ayuda con algún paso? 😄

Esta respuesta es generada por AI, solo como referencia.



