-- ==========================================================
-- SCALL Monitor-IoTec
-- Script SQL corregido y normalizado hasta 3FN
-- Base de datos: MySQL
-- ==========================================================

CREATE DATABASE scall_monitor_iotec
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE scall_monitor_iotec;

CREATE TABLE usuario (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY,
  telefono VARCHAR(20) NOT NULL,
  fecha_nacimiento DATE NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  contrasena VARCHAR(300) NOT NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1
) ENGINE = InnoDB;

CREATE TABLE sesion (
  id_sesion INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NULL,
  hora_de_inicio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  hora_salida DATETIME NULL,
  usuario_id INT NOT NULL,
  CONSTRAINT fk_sesion_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES usuario(id_usuario)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE = InnoDB;

CREATE TABLE captador_de_agua (
  id_captador INT AUTO_INCREMENT PRIMARY KEY,
  modelo VARCHAR(45) NOT NULL,
  descripcion VARCHAR(300) NULL
) ENGINE = InnoDB;

CREATE TABLE tipo_sensor (
  id_tipo_sensor INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(45) NOT NULL,
  unidad VARCHAR(20) NOT NULL,
  valor_minimo FLOAT NOT NULL,
  valor_maximo FLOAT NOT NULL
) ENGINE = InnoDB;

CREATE TABLE sensor (
  id_sensor INT AUTO_INCREMENT PRIMARY KEY,
  modelo VARCHAR(45) NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1,
  captador_id INT NOT NULL,
  tipo_sensor_id INT NOT NULL,
  CONSTRAINT fk_sensor_captador
    FOREIGN KEY (captador_id)
    REFERENCES captador_de_agua(id_captador)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT fk_sensor_tipo_sensor
    FOREIGN KEY (tipo_sensor_id)
    REFERENCES tipo_sensor(id_tipo_sensor)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE = InnoDB;

CREATE TABLE medicion (
  id_medicion INT AUTO_INCREMENT PRIMARY KEY,
  valor FLOAT NOT NULL,
  fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  sensor_id INT NOT NULL,
  CONSTRAINT fk_medicion_sensor
    FOREIGN KEY (sensor_id)
    REFERENCES sensor(id_sensor)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE = InnoDB;

CREATE TABLE tipo_alerta (
  id_tipo_alerta INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(45) NOT NULL
) ENGINE = InnoDB;

CREATE TABLE alerta (
  id_alerta INT AUTO_INCREMENT PRIMARY KEY,
  mensaje VARCHAR(100) NULL,
  atendido TINYINT(1) NOT NULL DEFAULT 0,
  fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  medicion_id INT NOT NULL,
  tipo_alerta_id INT NOT NULL,
  usuario_id INT NULL,
  CONSTRAINT fk_alerta_medicion
    FOREIGN KEY (medicion_id)
    REFERENCES medicion(id_medicion)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT fk_alerta_tipo_alerta
    FOREIGN KEY (tipo_alerta_id)
    REFERENCES tipo_alerta(id_tipo_alerta)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT fk_alerta_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES usuario(id_usuario)
    ON DELETE SET NULL
    ON UPDATE CASCADE
) ENGINE = InnoDB;

INSERT INTO tipo_sensor (nombre, unidad, valor_minimo, valor_maximo) VALUES
('pH', 'pH', 6.5, 8.5),
('Turbidez', 'NTU', 0, 5),
('Temperatura', 'C', 0, 40),
('Flujo de agua', 'L/min', 0, 30);

INSERT INTO tipo_alerta (nombre) VALUES
('Normal'),
('Advertencia'),
('Critica');

INSERT INTO captador_de_agua (modelo, descripcion) VALUES
('SCALL-IoTec V1', 'Sistema portatil de captacion de agua de lluvia con monitoreo por sensores');

INSERT INTO usuario (telefono, fecha_nacimiento, email, contrasena, activo) VALUES
('9991234567', '2000-01-01', 'admin@scall.com', 'admin123', 1);

INSERT INTO sensor (modelo, activo, captador_id, tipo_sensor_id) VALUES
('PH-4502C', 1, 1, 1),
('SEN0189', 1, 1, 2),
('DS18B20', 1, 1, 3),
('YF-S201', 1, 1, 4);

INSERT INTO medicion (valor, sensor_id) VALUES
(7.20, 1),
(3.20, 2),
(24.60, 3),
(12.40, 4);

INSERT INTO alerta (mensaje, atendido, medicion_id, tipo_alerta_id, usuario_id) VALUES
('Medicion registrada correctamente', 0, 1, 1, 1);

SELECT 
  m.id_medicion,
  ts.nombre AS tipo_sensor,
  m.valor,
  ts.unidad,
  m.fecha_hora
FROM medicion m
INNER JOIN sensor s ON m.sensor_id = s.id_sensor
INNER JOIN tipo_sensor ts ON s.tipo_sensor_id = ts.id_tipo_sensor
ORDER BY m.fecha_hora DESC;

SELECT 
  a.id_alerta,
  ta.nombre AS tipo_alerta,
  a.mensaje,
  a.atendido,
  a.fecha_hora,
  u.email AS usuario_responsable
FROM alerta a
INNER JOIN tipo_alerta ta ON a.tipo_alerta_id = ta.id_tipo_alerta
LEFT JOIN usuario u ON a.usuario_id = u.id_usuario;
