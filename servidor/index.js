const express = require('express');
const mysql = require('mysql2');

const app = express();
app.use(express.json());

const db = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '!Angel2427f',
  database: 'scall_monitor_iotec'
});

db.connect((err) => {
  if (err) {
    console.log('Error conectando a MySQL:', err);
    return;
  }
  console.log('Conectado a MySQL ✅');
});

// Recibir datos del ESP32///

// Variables para guardar el último valor por sensor
let ultimoFlujo = null;

app.post('/datos', (req, res) => {
  const { temperatura, humedad, flujo } = req.body;

  // Guardar temperatura siempre
  db.query('INSERT INTO medicion (valor, sensor_id) VALUES (?, ?)', [temperatura, 3]);

  // Guardar humedad siempre
  db.query('INSERT INTO medicion (valor, sensor_id) VALUES (?, ?)', [humedad, 5]);

  // Guardar flujo SOLO si cambió
  if (ultimoFlujo !== flujo) {
    db.query('INSERT INTO medicion (valor, sensor_id) VALUES (?, ?)', [flujo, 4]);
    ultimoFlujo = flujo;
    console.log('✅ Flujo guardado (nuevo valor):', flujo);
  } else {
    console.log('⏭️ Flujo omitido (mismo valor):', flujo);
  }

  res.json({ mensaje: 'Datos guardados correctamente' });
});


// Últimas mediciones
app.get('/mediciones', (req, res) => {
  db.query(
    `SELECT ts.nombre AS tipo_sensor, m.valor, ts.unidad, m.fecha_hora
     FROM medicion m
     INNER JOIN sensor s ON m.sensor_id = s.id_sensor
     INNER JOIN tipo_sensor ts ON s.tipo_sensor_id = ts.id_tipo_sensor
     WHERE m.id_medicion IN (SELECT MAX(id_medicion) FROM medicion GROUP BY sensor_id)
     ORDER BY m.fecha_hora DESC`,
    (err, results) => {
      if (err) return res.status(500).json({ error: err });
      res.json(results);
    }
  );
});

// Historial para gráficas
app.get('/historial', (req, res) => {
  db.query(
    `SELECT ts.nombre AS tipo_sensor, m.valor, m.fecha_hora
     FROM medicion m
     INNER JOIN sensor s ON m.sensor_id = s.id_sensor
     INNER JOIN tipo_sensor ts ON s.tipo_sensor_id = ts.id_tipo_sensor
     ORDER BY m.fecha_hora DESC LIMIT 30`,
    (err, results) => {
      if (err) return res.status(500).json({ error: err });
      res.json(results);
    }
  );
});

// Volumen captado hoy
app.get('/volumen', (req, res) => {
  db.query(
    `SELECT 
      MAX(CASE WHEN orden = 'ultimo' THEN valor END) - 
      MAX(CASE WHEN orden = 'primero' THEN valor END) AS volumen_total
     FROM (
       (SELECT valor, 'primero' as orden 
        FROM medicion m
        INNER JOIN sensor s ON m.sensor_id = s.id_sensor
        WHERE s.tipo_sensor_id = 4 AND DATE(m.fecha_hora) = CURDATE()
        ORDER BY m.fecha_hora ASC LIMIT 1)
       UNION ALL
       (SELECT valor, 'ultimo' as orden 
        FROM medicion m
        INNER JOIN sensor s ON m.sensor_id = s.id_sensor
        WHERE s.tipo_sensor_id = 4 AND DATE(m.fecha_hora) = CURDATE()
        ORDER BY m.fecha_hora DESC LIMIT 1)
     ) AS datos`,
    (err, results) => {
      if (err) return res.status(500).json({ error: err });
      let volumen = results[0].volumen_total || 0;
      if (volumen < 0) volumen = 0;
      res.json({ volumen_total: parseFloat(volumen.toFixed(2)) });
    }
  );
});


// Verificar alertas
app.get('/verificar-alertas', (req, res) => {
  db.query(
    `SELECT m.id_medicion, m.valor, ts.valor_minimo, ts.valor_maximo, ts.nombre
     FROM medicion m
     INNER JOIN sensor s ON m.sensor_id = s.id_sensor
     INNER JOIN tipo_sensor ts ON s.tipo_sensor_id = ts.id_tipo_sensor
     WHERE m.id_medicion IN (SELECT MAX(id_medicion) FROM medicion GROUP BY sensor_id)`,
    (err, results) => {
      if (err) return res.status(500).json({ error: err });
      results.forEach(row => {
        if (row.valor < row.valor_minimo || row.valor > row.valor_maximo) {
          db.query('INSERT INTO alerta (mensaje, atendido, medicion_id, tipo_alerta_id, usuario_id) VALUES (?, 0, ?, 2, 1)',
            [`${row.nombre} fuera de rango: ${row.valor}`, row.id_medicion]);
        }
      });
      res.json({ mensaje: 'Alertas verificadas' });
    }
  );
});

// Ver alertas activas
app.get('/alertas', (req, res) => {
  db.query(
    `SELECT a.id_alerta, ta.nombre AS tipo_alerta, a.mensaje, a.fecha_hora
     FROM alerta a
     INNER JOIN tipo_alerta ta ON a.tipo_alerta_id = ta.id_tipo_alerta
     WHERE a.atendido = 0
     ORDER BY a.fecha_hora DESC`,
    (err, results) => {
      if (err) return res.status(500).json({ error: err });
      res.json(results);
    }
  );
});

// Atender alertas
app.post('/atender-alertas', (req, res) => {
  db.query('UPDATE alerta SET atendido = 1 WHERE atendido = 0', (err) => {
    if (err) return res.status(500).json({ error: err });
    res.json({ mensaje: 'Alertas atendidas' });
  });
});

app.listen(3000, () => {
  console.log('Servidor corriendo en puerto 3000 ✅');
});

// Registrar nuevo usuario
app.post('/registro', (req, res) => {
  const { nombre, email, telefono, contrasena } = req.body;
  
  db.query(
    'INSERT INTO usuario (nombre, email, telefono, contrasena, activo) VALUES (?, ?, ?, ?, 1)',
    [nombre, email, telefono, contrasena],
    (err, result) => {
      if (err) {
        console.error('Error en registro:', err);
        if (err.code === 'ER_DUP_ENTRY') {
          return res.json({ success: false, mensaje: 'El correo ya está registrado' });
        }
        return res.status(500).json({ success: false, mensaje: 'Error al registrar usuario' });
      }
      res.json({ success: true, mensaje: 'Usuario registrado correctamente' });
    }
  );
});


// iniciio de sesion 
app.post('/login', (req, res) => {
  const { email, contrasena } = req.body;
  
  // Cambia 'contraseña' por 'contrasena' (sin ñ)
  db.query(
    'SELECT * FROM usuario WHERE email = ? AND contrasena = ? AND activo = 1',
    [email, contrasena],
    (err, results) => {
      if (err) return res.status(500).json({ error: err });
      
      if (results.length > 0) {
    const usuario = results[0];
    db.query(
        'INSERT INTO sesion (usuario_id, hora_de_inicio, nombre) VALUES (?, NOW(), ?)',
        [usuario.id_usuario, usuario.nombre || 'Usuario'],
        (errSesion, resultSesion) => {
            if (errSesion) console.error('Error al guardar sesión:', errSesion);
            console.log('Login exitoso - ID Sesion:', resultSesion?.insertId); // ← Agrega esto
            res.json({ success: true, usuario: usuario, id_sesion: resultSesion?.insertId });
        }
    );
} else {
    console.log('Login fallido - Credenciales incorrectas'); // ← Agrega esto
    res.json({ success: false, mensaje: 'Credenciales incorrectas' });
}
    }
  );
});



//salida de sesion 
app.post('/logout', (req, res) => {
  const { id_sesion } = req.body;
  
  if (!id_sesion) {
    return res.status(400).json({ success: false, mensaje: 'ID de sesión requerido' });
  }
  
  db.query(
    'UPDATE sesion SET hora_salida = NOW() WHERE id_sesion = ? AND hora_salida IS NULL',
    [id_sesion],
    (err, result) => {
      if (err) {
        console.error('Error al cerrar sesión:', err);
        return res.status(500).json({ success: false, mensaje: 'Error al cerrar sesión' });
      }
      res.json({ success: true, mensaje: 'Sesión cerrada correctamente' });
    }
  );
});


// Eliminar una alerta por ID
app.delete('/alerta/:id', (req, res) => {
  const idAlerta = req.params.id;
  
  db.query(
    'DELETE FROM alerta WHERE id_alerta = ?',
    [idAlerta],
    (err, result) => {
      if (err) return res.status(500).json({ error: err });
      
      if (result.affectedRows === 0) {
        return res.status(404).json({ success: false, mensaje: 'Alerta no encontrada' });
      }
      
      res.json({ success: true, mensaje: 'Alerta eliminada correctamente' });
    }
  );
});

// Eliminar mediciones antiguas (más de 30 días)
app.delete('/mediciones-antiguas', (req, res) => {
  db.query(
    'DELETE FROM medicion WHERE fecha_hora < DATE_SUB(NOW(), INTERVAL 30 DAY)',
    (err, result) => {
      if (err) return res.status(500).json({ error: err });
      res.json({ 
        success: true, 
        mensaje: `Se eliminaron ${result.affectedRows} mediciones antiguas` 
      });
    }
  );
});
