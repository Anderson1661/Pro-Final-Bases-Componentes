-- ======== TABLAS PRINCIPALES ========

CREATE TABLE IF NOT EXISTS tipo_usuario (
  id_tipo_usuario INT AUTO_INCREMENT PRIMARY KEY,
  descripcion VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS usuario (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY,
  id_tipo_usuario INT NOT NULL,
  correo VARCHAR(100) UNIQUE NOT NULL,
  contrasenia VARCHAR(200) NOT NULL,
  FOREIGN KEY (id_tipo_usuario) REFERENCES tipo_usuario (id_tipo_usuario)
);

CREATE TABLE IF NOT EXISTS pais (
  id_pais INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS codigo_postal (
  id_codigo_postal VARCHAR(10) PRIMARY KEY,
  id_pais INT NOT NULL,
  departamento VARCHAR(50) NOT NULL,
  ciudad VARCHAR(50) NOT NULL,
  FOREIGN KEY (id_pais) REFERENCES pais (id_pais)
);

CREATE TABLE IF NOT EXISTS tipo_identificacion (
  id_tipo_identificacion INT AUTO_INCREMENT PRIMARY KEY,
  descripcion VARCHAR(30) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS genero (
  id_genero INT AUTO_INCREMENT PRIMARY KEY,
  descripcion VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS cliente (
  id_cliente INT AUTO_INCREMENT PRIMARY KEY,
  identificacion VARCHAR(20) NOT NULL UNIQUE,
  id_tipo_identificacion INT NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  direccion VARCHAR(100) NOT NULL,
  correo VARCHAR(100) NOT NULL UNIQUE,
  id_genero INT NOT NULL,
  id_pais_nacionalidad INT NOT NULL,
  codigo_postal VARCHAR(10) NOT NULL,
  FOREIGN KEY (id_tipo_identificacion) REFERENCES tipo_identificacion (id_tipo_identificacion),
  FOREIGN KEY (id_genero) REFERENCES genero (id_genero),
  FOREIGN KEY (codigo_postal) REFERENCES codigo_postal (id_codigo_postal),
  FOREIGN KEY (id_pais_nacionalidad) REFERENCES pais (id_pais)
);

CREATE TABLE IF NOT EXISTS telefono_cliente (
  id_cliente INT,
  telefono BIGINT,
  PRIMARY KEY (id_cliente, telefono),
  FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente)
);

CREATE TABLE IF NOT EXISTS administrador (
  id_administrador INT AUTO_INCREMENT PRIMARY KEY,
  identificacion VARCHAR(20) NOT NULL UNIQUE,
  id_tipo_identificacion INT NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  direccion VARCHAR(100) NOT NULL,
  correo VARCHAR(100) NOT NULL UNIQUE,
  id_genero INT NOT NULL,
  codigo_postal VARCHAR(10) NOT NULL,
  FOREIGN KEY (id_tipo_identificacion) REFERENCES tipo_identificacion (id_tipo_identificacion),
  FOREIGN KEY (id_genero) REFERENCES genero (id_genero),
  FOREIGN KEY (codigo_postal) REFERENCES codigo_postal (id_codigo_postal)
);

CREATE TABLE IF NOT EXISTS telefono_administrador (
  id_administrador INT,
  telefono BIGINT,
  PRIMARY KEY (id_administrador, telefono),
  FOREIGN KEY (id_administrador) REFERENCES administrador (id_administrador)
);

CREATE TABLE IF NOT EXISTS estado_vehiculo (
  id_estado_vehiculo INT AUTO_INCREMENT PRIMARY KEY,
  descripcion VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS marca_vehiculo (
  id_marca INT AUTO_INCREMENT PRIMARY KEY,
  nombre_marca VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS tipo_servicio (
  id_tipo_servicio INT AUTO_INCREMENT PRIMARY KEY,
  descripcion VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS color_vehiculo (
  id_color INT AUTO_INCREMENT PRIMARY KEY,
  descripcion VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS linea_vehiculo (
  id_linea VARCHAR(50),
  id_marca INT,
  PRIMARY KEY (id_linea, id_marca),
  FOREIGN KEY (id_marca) REFERENCES marca_vehiculo (id_marca)
);

CREATE TABLE IF NOT EXISTS vehiculo (
  placa VARCHAR(20) PRIMARY KEY,
  linea_vehiculo VARCHAR(50) NOT NULL,
  modelo INT NOT NULL,
  id_color INT NOT NULL,
  id_marca INT NOT NULL,
  id_tipo_servicio INT NOT NULL,
  id_estado_vehiculo INT NOT NULL,
  FOREIGN KEY (id_color) REFERENCES color_vehiculo (id_color),
  FOREIGN KEY (linea_vehiculo, id_marca) REFERENCES linea_vehiculo (id_linea, id_marca),
  FOREIGN KEY (id_tipo_servicio) REFERENCES tipo_servicio (id_tipo_servicio),
  FOREIGN KEY (id_estado_vehiculo) REFERENCES estado_vehiculo (id_estado_vehiculo),
  CONSTRAINT check_modelo CHECK (modelo >= 2010)
);

CREATE TABLE IF NOT EXISTS estado_conductor (
  id_estado_conductor INT AUTO_INCREMENT PRIMARY KEY,
  descripcion VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS conductor (
  id_conductor INT AUTO_INCREMENT PRIMARY KEY,
  id_estado_conductor INT NOT NULL,
  placa_vehiculo VARCHAR(20) NOT NULL UNIQUE,
  identificacion VARCHAR(20) NOT NULL UNIQUE,
  id_tipo_identificacion INT NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  direccion VARCHAR(100) NOT NULL,
  correo VARCHAR(100) NOT NULL UNIQUE,
  id_genero INT NOT NULL,
  codigo_postal VARCHAR(10) NOT NULL,
  id_pais_nacionalidad INT NOT NULL,
  url_foto VARCHAR(255) NOT NULL UNIQUE,
  FOREIGN KEY (id_estado_conductor) REFERENCES estado_conductor (id_estado_conductor),
  FOREIGN KEY (placa_vehiculo) REFERENCES vehiculo (placa),
  FOREIGN KEY (id_tipo_identificacion) REFERENCES tipo_identificacion (id_tipo_identificacion),
  FOREIGN KEY (id_genero) REFERENCES genero (id_genero),
  FOREIGN KEY (codigo_postal) REFERENCES codigo_postal (id_codigo_postal),
  FOREIGN KEY (id_pais_nacionalidad) REFERENCES pais (id_pais)
);

CREATE TABLE IF NOT EXISTS telefono_conductor (
  id_conductor INT,
  telefono BIGINT,
  PRIMARY KEY (id_conductor, telefono),
  FOREIGN KEY (id_conductor) REFERENCES conductor (id_conductor)
);

CREATE TABLE IF NOT EXISTS metodo_pago (
  id_metodo_pago INT AUTO_INCREMENT PRIMARY KEY,
  descripcion VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categoria_servicio (
  id_categoria_servicio INT AUTO_INCREMENT PRIMARY KEY,
  descripcion VARCHAR(30) NOT NULL UNIQUE,
  valor_km DECIMAL(12,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS estado_servicio (
  id_estado_servicio INT AUTO_INCREMENT PRIMARY KEY,
  descripcion VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS ruta (
  id_ruta INT AUTO_INCREMENT PRIMARY KEY,
  direccion_origen VARCHAR(50) NOT NULL,
  direccion_destino VARCHAR(50) NOT NULL,
  id_codigo_postal_origen VARCHAR(10) NOT NULL,
  id_codigo_postal_destino VARCHAR(10) NOT NULL,
  distancia_km DECIMAL(8,2) NOT NULL,
  fecha_hora_reserva DATETIME NOT NULL,
  fecha_hora_origen DATETIME,
  fecha_hora_destino DATETIME,
  id_conductor INT,
  id_tipo_servicio INT NOT NULL,
  id_cliente INT NOT NULL,
  id_estado_servicio INT NOT NULL,
  id_categoria_servicio INT NOT NULL,
  id_metodo_pago INT NOT NULL,
  total DECIMAL(12,2),
  pago_conductor DECIMAL(12,2),
  FOREIGN KEY (id_codigo_postal_origen) REFERENCES codigo_postal (id_codigo_postal),
  FOREIGN KEY (id_codigo_postal_destino) REFERENCES codigo_postal (id_codigo_postal),
  FOREIGN KEY (id_conductor) REFERENCES conductor (id_conductor),
  FOREIGN KEY (id_tipo_servicio) REFERENCES tipo_servicio (id_tipo_servicio),
  FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente),
  FOREIGN KEY (id_estado_servicio) REFERENCES estado_servicio (id_estado_servicio),
  FOREIGN KEY (id_categoria_servicio) REFERENCES categoria_servicio (id_categoria_servicio),
  FOREIGN KEY (id_metodo_pago) REFERENCES metodo_pago (id_metodo_pago),
  CONSTRAINT check_distancia CHECK (distancia_km > 0)
);

CREATE TABLE IF NOT EXISTS pasajero_ruta (
  id_ruta INT,
  nombre_pasajero VARCHAR(100),
  PRIMARY KEY (id_ruta, nombre_pasajero),
  FOREIGN KEY (id_ruta) REFERENCES ruta (id_ruta)
);

CREATE TABLE IF NOT EXISTS preguntas_seguridad (
  id_pregunta INT AUTO_INCREMENT PRIMARY KEY,
  descripcion VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS respuestas_seguridad (
  id_pregunta INT,
  id_usuario INT,
  respuesta_pregunta VARCHAR(255) NOT NULL,
  PRIMARY KEY (id_pregunta, id_usuario),
  FOREIGN KEY (id_pregunta) REFERENCES preguntas_seguridad (id_pregunta),
  FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
);


-- ======== TRIGGERS ========

DELIMITER //

-- Trigger insertar usuario automático para cliente
CREATE TRIGGER trg_insert_usuario_cliente
AFTER INSERT ON cliente
FOR EACH ROW
BEGIN
  IF NOT EXISTS (SELECT 1 FROM usuario WHERE correo = NEW.correo) THEN
    INSERT INTO usuario (id_tipo_usuario, correo, contrasenia)
    SELECT id_tipo_usuario, NEW.correo, NEW.correo
    FROM tipo_usuario
    WHERE descripcion = 'Cliente'
    LIMIT 1;
  END IF;
END//

-- Trigger insertar usuario automático para conductor
CREATE TRIGGER trg_insert_usuario_conductor
AFTER INSERT ON conductor
FOR EACH ROW
BEGIN
  IF NOT EXISTS (SELECT 1 FROM usuario WHERE correo = NEW.correo) THEN
    INSERT INTO usuario (id_tipo_usuario, correo, contrasenia)
    SELECT id_tipo_usuario, NEW.correo, NEW.correo
    FROM tipo_usuario
    WHERE descripcion = 'Conductor'
    LIMIT 1;
  END IF;
END//

-- Trigger insertar usuario automático para administrador
CREATE TRIGGER trg_insert_usuario_admin
AFTER INSERT ON administrador
FOR EACH ROW
BEGIN
  IF NOT EXISTS (SELECT 1 FROM usuario WHERE correo = NEW.correo) THEN
    INSERT INTO usuario (id_tipo_usuario, correo, contrasenia)
    SELECT id_tipo_usuario, NEW.correo, NEW.correo
    FROM tipo_usuario
    WHERE descripcion = 'Administrador'
    LIMIT 1;
  END IF;
END//

-- Trigger calcular total de ruta
CREATE TRIGGER trg_calcular_total_ruta
BEFORE INSERT ON ruta
FOR EACH ROW
BEGIN
  DECLARE precio_km DECIMAL(12,2);
  SELECT valor_km INTO precio_km FROM categoria_servicio WHERE id_categoria_servicio = NEW.id_categoria_servicio LIMIT 1;
  SET NEW.total = NEW.distancia_km * precio_km;
END//

-- Trigger calcular pago del conductor (30%)
CREATE TRIGGER trg_calcular_pago_conductor
BEFORE INSERT ON ruta
FOR EACH ROW
BEGIN
  IF NEW.total IS NOT NULL THEN
    SET NEW.pago_conductor = ROUND(NEW.total * 0.30, 2);
  END IF;
END//

DELIMITER ;


-- ======== DATOS DE PRUEBA ========

-- Tablas maestras básicas
INSERT INTO tipo_usuario (descripcion) VALUES ('Administrador'), ('Conductor'), ('Cliente');
insert into usuario (id_tipo_usuario, correo, contrasenia) values 
(1, "admin@empresa.com", "admin@empresa.com"),
(2, "conductor@empresa.com", "conductor@empresa.com"),
(3, "cliente@empresa.com", "cliente@empresa.com");

INSERT INTO tipo_identificacion (descripcion) VALUES ('Cédula de Ciudadanía'), ('Cédula de Extranjería'), ('Pasaporte');
INSERT INTO genero (descripcion) VALUES ('Masculino'), ('Femenino'), ('Otro');
INSERT INTO pais (nombre) VALUES ('Colombia'), ('Venezuela'), ('Ecuador');
INSERT INTO codigo_postal (id_codigo_postal, id_pais, departamento, ciudad) VALUES 
('110111', 1, 'Cundinamarca', 'Bogotá'),
('050001', 1, 'Antioquia', 'Medellín'),
('760001', 1, 'Valle del Cauca', 'Cali');

-- Tablas de vehículos y servicios
INSERT INTO estado_vehiculo (descripcion) VALUES ('Activo'), ('En Mantenimiento'), ('Inactivo');
INSERT INTO marca_vehiculo (nombre_marca) VALUES ('Chevrolet'), ('Renault'), ('Nissan'), ('Mazda');
INSERT INTO linea_vehiculo (id_linea, id_marca) VALUES 
('N300', 1),
('Spark', 1),
('Logan', 2),
('Versa', 3);
INSERT INTO color_vehiculo (descripcion) VALUES ('Blanco'), ('Negro'), ('Gris'), ('Rojo'), ('Azul');
INSERT INTO tipo_servicio (descripcion) VALUES ('Pasajeros'),('Alimentos'),('Pasajeros y alimentos');
INSERT INTO categoria_servicio (descripcion, valor_km) VALUES 
('Económico', 2500.00),
('Estándar', 3500.00),
('Premium', 5000.00);
insert into estado_servicio (descripcion) VALUES ('Pendiente'),('En proceso'),('Finalizado');
INSERT INTO metodo_pago (descripcion) VALUES ('Efectivo'),('Tarjeta Debito'),('Tarjeta Credito'),('Transferencia');

-- Vehículos (deben crearse antes de los conductores)
INSERT INTO vehiculo (placa, linea_vehiculo, modelo, id_color, id_marca, id_tipo_servicio, id_estado_vehiculo) VALUES 
('ABC123', 'N300', 2020, 1, 1, 1, 1),
('DEF456', 'Spark', 2021, 2, 1, 1, 1),
('SEX69H', 'Logan', 2026, 5, 2, 3, 1),
('GHI789', 'Logan', 2019, 3, 2, 1, 1);

-- Administradores (los triggers crearán los usuarios automáticamente)
INSERT INTO administrador (identificacion, id_tipo_identificacion, nombre, direccion, correo, id_genero, codigo_postal) VALUES 
('1012345678', 1, 'Carlos Andrés Rodríguez García', 'Calle 72 # 11-20', 'admin@empresa.com', 1, '110111'),
('1018765432', 1, 'María Fernanda López Martínez', 'Carrera 50 # 30-45', 'maria.lopez@empresa.com', 2, '050001');

-- Teléfonos de administradores
INSERT INTO telefono_administrador (id_administrador, telefono) VALUES 
(1, 3101234567),
(1, 3209876543),
(2, 3157654321);

-- Clientes (los triggers crearán los usuarios automáticamente)
INSERT INTO cliente (identificacion, id_tipo_identificacion, nombre, direccion, correo, id_genero, id_pais_nacionalidad, codigo_postal) VALUES 
('1023456789', 1, 'Ana Sofía Martínez Gómez', 'Calle 100 # 15-30', 'ana.martinez@email.com', 2, 1, '110111'),
('1034567890', 1, 'Juan Pablo Herrera Sánchez', 'Carrera 7 # 45-12', 'juan.herrera@email.com', 1, 1, '110111'),
('1029141647', 1, 'William dog', 'Calle 100 # 15-30', '1@1.com', 1, 1, '760001'),
('1045678901', 1, 'Laura Camila Torres Ramírez', 'Avenida 68 # 25-40', 'laura.torres@email.com', 2, 1, '110111');

-- Teléfonos de clientes
INSERT INTO telefono_cliente (id_cliente, telefono) VALUES 
(1, 3012345678),
(1, 3123456789),
(2, 3187654321),
(3, 3156789012);

-- Estados de conductor
INSERT INTO estado_conductor (descripcion) VALUES ('Conectado'), ('Desconectado');

-- Conductores (los triggers crearán los usuarios automáticamente)
-- Nota: Necesitan URL de foto válida y placa de vehículo existente
INSERT INTO conductor (id_estado_conductor, placa_vehiculo, identificacion, id_tipo_identificacion, nombre, direccion, correo, id_genero, codigo_postal, id_pais_nacionalidad, url_foto) VALUES 
(1, 'ABC123', '1056789012', 1, 'Roberto Carlos Méndez Vargas', 'Calle 80 # 20-15', 'conductor1@empresa.com', 1, '110111', 1, 'https://example.com/fotos/conductor1.jpg'),
(2, 'DEF456', '1067890123', 1, 'Sandra Milena Ramírez Ortiz', 'Carrera 30 # 50-25', 'conductor2@empresa.com', 2, '050001', 1, 'https://example.com/fotos/conductor2.jpg'),
(2, 'SEX69H', '1000000000', 1, 'Mostro', 'Carrera 30 # 50-25', '2@2.com', 3, '050001', 1, 'https://example.com/fotos/mostro.jpg'),
(2, 'GHI789', '1078901234', 1, 'Diego Armando Suárez Pérez', 'Avenida 6N # 35-10', 'conductor3@empresa.com', 1, '760001', 1, 'https://example.com/fotos/conductor3.jpg');

-- Teléfonos de conductores
INSERT INTO telefono_conductor (id_conductor, telefono) VALUES 
(1, 3123456789),
(1, 3234567890),
(2, 3145678901),
(3, 3167890123);

-- Rutas (deben tener todos los campos requeridos y datos coherentes)
INSERT INTO ruta (direccion_origen, direccion_destino, id_codigo_postal_origen, id_codigo_postal_destino, distancia_km, fecha_hora_reserva, fecha_hora_origen, fecha_hora_destino, id_conductor, id_tipo_servicio, id_cliente, id_estado_servicio, id_categoria_servicio, id_metodo_pago) VALUES 
('Aeropuerto El Dorado', 'Centro Comercial Santa Fe', '110111', '110111', 25.50, '2024-01-15 08:00:00', '2024-01-15 09:00:00', '2024-01-15 09:45:00', 1, 1, 1, 3, 1, 1),
('Terminal de Transporte', 'Universidad Nacional', '110111', '110111', 18.30, '2024-01-16 14:00:00', '2024-01-16 14:30:00', '2024-01-16 15:15:00', 2, 1, 2, 3, 2, 3),
('Hospital San Ignacio', 'Centro de la ciudad', '110111', '110111', 12.75, '2024-01-17 10:00:00', '2024-01-17 10:30:00', '2024-01-17 11:00:00', 3, 1, 3, 2, 1, 4);

-- Pasajeros adicionales en rutas
INSERT INTO pasajero_ruta (id_ruta, nombre_pasajero) VALUES 
(1, 'María González'),
(1, 'Pedro Sánchez'),
(2, 'Carlos Ramírez');

-- Preguntas de seguridad
INSERT INTO preguntas_seguridad (descripcion) VALUES 
('¿Cuál es el nombre de tu primera mascota?'),
('¿En qué ciudad naciste?'),
('¿Cuál es el nombre de tu colegio favorito?'),
('¿Cuál es el nombre de tu mejor amigo de la infancia?'),
('¿Cuál es tu comida favorita?');

-- Respuestas de seguridad (asociadas a usuarios creados por triggers)
-- Nota: Los IDs de usuario se generarán automáticamente por los triggers
-- Estos INSERTs deben ejecutarse después de que los triggers hayan creado los usuarios
INSERT INTO respuestas_seguridad (id_pregunta, id_usuario, respuesta_pregunta)
SELECT 1, id_usuario, 'Pelusa' FROM usuario WHERE correo = 'ana.martinez@email.com'
UNION ALL
SELECT 2, id_usuario, 'Bogotá' FROM usuario WHERE correo = 'juan.herrera@email.com'
UNION ALL
SELECT 1, id_usuario, 'Max' FROM usuario WHERE correo = 'conductor1@empresa.com'
UNION ALL
SELECT 3, id_usuario, 'San José' FROM usuario WHERE correo = 'admin@empresa.com';

-- ======== VISTAS ========

CREATE OR REPLACE VIEW vw_total_por_tipo_y_categoria AS
SELECT 
  ts.descripcion AS tipo_servicio,
  cs.descripcion AS categoria_servicio,
  COUNT(r.id_ruta) AS cantidad_servicios,
  COALESCE(SUM(r.total), 0) AS valor_total
FROM ruta r
JOIN tipo_servicio ts ON r.id_tipo_servicio = ts.id_tipo_servicio
JOIN categoria_servicio cs ON r.id_categoria_servicio = cs.id_categoria_servicio
GROUP BY ts.descripcion, cs.descripcion
ORDER BY ts.descripcion, cs.descripcion;

CREATE OR REPLACE VIEW vw_cantidad_servicios_por_mes_y_tipo AS
SELECT 
  DATE_FORMAT(r.fecha_hora_origen, '%Y-%m') AS periodo,
  ts.descripcion AS tipo_servicio,
  COUNT(r.id_ruta) AS cantidad_servicios
FROM ruta r
JOIN tipo_servicio ts ON r.id_tipo_servicio = ts.id_tipo_servicio
GROUP BY DATE_FORMAT(r.fecha_hora_origen, '%Y-%m'), ts.descripcion
ORDER BY periodo, tipo_servicio;

CREATE OR REPLACE VIEW vw_clientes_con_servicios AS
SELECT 
  c.id_cliente,
  c.nombre,
  c.correo,
  COUNT(r.id_ruta) AS cantidad_servicios,
  COALESCE(SUM(r.total), 0) AS valor_total
FROM cliente c
JOIN ruta r ON c.id_cliente = r.id_cliente
GROUP BY c.id_cliente, c.nombre, c.correo
ORDER BY cantidad_servicios DESC;

CREATE OR REPLACE VIEW vw_total_por_metodo_pago AS
SELECT 
  mp.descripcion AS metodo_pago,
  COUNT(r.id_ruta) AS cantidad_servicios,
  COALESCE(SUM(r.total), 0) AS valor_total
FROM ruta r
JOIN metodo_pago mp ON r.id_metodo_pago = mp.id_metodo_pago
GROUP BY mp.descripcion
ORDER BY valor_total DESC;
