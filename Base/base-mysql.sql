-- ======== TABLAS ========

-- Tabla: tipo_usuario
-- Descripción: Catálogo de los tipos de usuarios permitidos en el sistema.
-- Propósito: Diferenciar entre Administradores, Conductores y Clientes para gestionar permisos y accesos.
CREATE TABLE IF NOT EXISTS tipo_usuario (
    id_tipo_usuario INT AUTO_INCREMENT PRIMARY KEY, -- Identificador único del tipo de usuario
    descripcion VARCHAR(30) NOT NULL UNIQUE -- Nombre del rol (ej. 'Administrador', 'Cliente')
);

-- Tabla: usuario
-- Descripción: Tabla central de autenticación.
-- Propósito: Almacenar las credenciales de acceso (correo y contraseña) para todos los actores del sistema.
-- Relación: Se vincula con 'tipo_usuario' para saber el rol del usuario.
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY, -- Identificador único del usuario en el sistema
    id_tipo_usuario INT NOT NULL, -- Clave foránea al tipo de usuario
    correo VARCHAR(100) UNIQUE NOT NULL, -- Correo electrónico (usado como login)
    contrasenia VARCHAR(200) NOT NULL, -- Contraseña encriptada o texto plano (según implementación)
    FOREIGN KEY (id_tipo_usuario) REFERENCES tipo_usuario (id_tipo_usuario)
);

-- Tabla: pais
-- Descripción: Catálogo de países.
-- Propósito: Normalizar la nacionalidad y ubicación de los usuarios.
CREATE TABLE IF NOT EXISTS pais (
    id_pais INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla: codigo_postal
-- Descripción: Catálogo geográfico detallado.
-- Propósito: Almacenar la relación entre códigos postales, departamentos y ciudades.
CREATE TABLE IF NOT EXISTS codigo_postal (
    id_codigo_postal VARCHAR(10) PRIMARY KEY, -- Código postal (clave primaria manual)
    id_pais INT NOT NULL, -- País al que pertenece
    departamento VARCHAR(50) NOT NULL, -- Nombre del departamento o estado
    ciudad VARCHAR(50) NOT NULL, -- Nombre de la ciudad
    FOREIGN KEY (id_pais) REFERENCES pais (id_pais)
);

-- Tabla: tipo_identificacion
-- Descripción: Catálogo de tipos de documentos de identidad.
-- Propósito: Soportar diferentes documentos (Cédula, Pasaporte, etc.).
CREATE TABLE IF NOT EXISTS tipo_identificacion (
    id_tipo_identificacion INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(30) UNIQUE NOT NULL
);

-- Tabla: genero
-- Descripción: Catálogo de géneros.
CREATE TABLE IF NOT EXISTS genero (
    id_genero INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(30) NOT NULL UNIQUE
);

-- Tabla: cliente
-- Descripción: Información detallada de los clientes.
-- Propósito: Almacenar datos personales y de contacto de quienes solicitan servicios.
CREATE TABLE IF NOT EXISTS cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    identificacion VARCHAR(20) NOT NULL UNIQUE, -- Número de documento de identidad
    id_tipo_identificacion INT NOT NULL, -- Tipo de documento
    nombre VARCHAR(100) NOT NULL, -- Nombre completo
    direccion VARCHAR(100) NOT NULL, -- Dirección de residencia
    correo VARCHAR(100) NOT NULL UNIQUE, -- Correo electrónico (debe coincidir con la tabla usuario)
    id_genero INT NOT NULL, -- Género
    id_pais_nacionalidad INT NOT NULL, -- Nacionalidad
    codigo_postal VARCHAR(10) NOT NULL, -- Ubicación geográfica
    FOREIGN KEY (id_tipo_identificacion) REFERENCES tipo_identificacion (id_tipo_identificacion),
    FOREIGN KEY (id_genero) REFERENCES genero (id_genero),
    FOREIGN KEY (codigo_postal) REFERENCES codigo_postal (id_codigo_postal),
    FOREIGN KEY (id_pais_nacionalidad) REFERENCES pais (id_pais)
);

-- Tabla: telefono_cliente
-- Descripción: Teléfonos de contacto de los clientes.
-- Propósito: Permitir que un cliente tenga múltiples números de teléfono (Relación 1:N).
CREATE TABLE IF NOT EXISTS telefono_cliente (
    id_cliente INT,
    telefono BIGINT,
    PRIMARY KEY (id_cliente, telefono), -- Clave compuesta para evitar duplicados
    FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente)
);

-- Tabla: administrador
-- Descripción: Información de los administradores del sistema.
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

-- Tabla: telefono_administrador
-- Descripción: Teléfonos de contacto de los administradores.
CREATE TABLE IF NOT EXISTS telefono_administrador (
    id_administrador INT,
    telefono BIGINT,
    PRIMARY KEY (id_administrador, telefono),
    FOREIGN KEY (id_administrador) REFERENCES administrador (id_administrador)
);

-- Tabla: estado_vehiculo
-- Descripción: Catálogo de estados operativos de los vehículos.
CREATE TABLE IF NOT EXISTS estado_vehiculo (
    id_estado_vehiculo INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(100) NOT NULL UNIQUE -- Ej: 'Activo', 'En Mantenimiento'
);

-- Tabla: marca_vehiculo
-- Descripción: Catálogo de marcas de vehículos.
CREATE TABLE IF NOT EXISTS marca_vehiculo (
    id_marca INT AUTO_INCREMENT PRIMARY KEY,
    nombre_marca VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla: tipo_servicio
-- Descripción: Catálogo de tipos de servicio que ofrece la empresa.
CREATE TABLE IF NOT EXISTS tipo_servicio (
    id_tipo_servicio INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(30) NOT NULL UNIQUE -- Ej: 'Pasajeros', 'Encomiendas'
);

-- Tabla: color_vehiculo
-- Descripción: Catálogo de colores para los vehículos.
CREATE TABLE IF NOT EXISTS color_vehiculo (
    id_color INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(30) NOT NULL UNIQUE
);

-- Tabla: linea_vehiculo
-- Descripción: Catálogo de líneas o modelos específicos de cada marca.
-- Relación: Depende de 'marca_vehiculo'.
CREATE TABLE IF NOT EXISTS linea_vehiculo (
    id_linea VARCHAR(50), -- Nombre de la línea (ej: 'Aveo', 'Picanto')
    id_marca INT, -- Marca a la que pertenece
    PRIMARY KEY (id_linea, id_marca),
    FOREIGN KEY (id_marca) REFERENCES marca_vehiculo (id_marca)
);

-- Tabla: vehiculo
-- Descripción: Inventario de vehículos registrados en el sistema.
-- Propósito: Gestionar los vehiculos de cada cliente.
CREATE TABLE IF NOT EXISTS vehiculo (
    placa VARCHAR(20) PRIMARY KEY, -- Placa única del vehículo
    linea_vehiculo VARCHAR(50) NOT NULL, -- Línea del vehículo
    modelo INT NOT NULL, -- Año del modelo
    id_color INT NOT NULL, -- Color
    id_marca INT NOT NULL, -- Marca
    id_tipo_servicio INT NOT NULL, -- Tipo de servicio habilitado para este vehículo
    id_estado_vehiculo INT NOT NULL, -- Estado actual (Activo, Inactivo, etc.)
    FOREIGN KEY (id_color) REFERENCES color_vehiculo (id_color),
    FOREIGN KEY (linea_vehiculo, id_marca) REFERENCES linea_vehiculo (id_linea, id_marca),
    FOREIGN KEY (id_tipo_servicio) REFERENCES tipo_servicio (id_tipo_servicio),
    FOREIGN KEY (id_estado_vehiculo) REFERENCES estado_vehiculo (id_estado_vehiculo),
    CONSTRAINT check_modelo CHECK (modelo >= 2010) -- Restricción: Solo vehículos del 2010 en adelante
);

-- Tabla: estado_conductor
-- Descripción: Catálogo de estados de disponibilidad del conductor.
CREATE TABLE IF NOT EXISTS estado_conductor (
    id_estado_conductor INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(30) NOT NULL UNIQUE -- Ej: 'Conectado', 'Desconectado'
);

-- Tabla: conductor
-- Descripción: Información detallada de los conductores.
-- Propósito: Gestionar al personal que presta los servicios.
CREATE TABLE IF NOT EXISTS conductor (
    id_conductor INT AUTO_INCREMENT PRIMARY KEY,
    id_estado_conductor INT NOT NULL, -- Estado actual de disponibilidad
    placa_vehiculo VARCHAR(20) NOT NULL UNIQUE, -- Vehículo asignado (Relación 1:1 actual)
    identificacion VARCHAR(20) NOT NULL UNIQUE,
    id_tipo_identificacion INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    id_genero INT NOT NULL,
    codigo_postal VARCHAR(10) NOT NULL,
    id_pais_nacionalidad INT NOT NULL,
    url_foto VARCHAR(255) NOT NULL, -- Link de la foto del conductor
    FOREIGN KEY (id_estado_conductor) REFERENCES estado_conductor (id_estado_conductor),
    FOREIGN KEY (placa_vehiculo) REFERENCES vehiculo (placa),
    FOREIGN KEY (id_tipo_identificacion) REFERENCES tipo_identificacion (id_tipo_identificacion),
    FOREIGN KEY (id_genero) REFERENCES genero (id_genero),
    FOREIGN KEY (codigo_postal) REFERENCES codigo_postal (id_codigo_postal),
    FOREIGN KEY (id_pais_nacionalidad) REFERENCES pais (id_pais)
);

-- Tabla: telefono_conductor
-- Descripción: Teléfonos de contacto de los conductores.
CREATE TABLE IF NOT EXISTS telefono_conductor (
    id_conductor INT,
    telefono BIGINT,
    PRIMARY KEY (id_conductor, telefono),
    FOREIGN KEY (id_conductor) REFERENCES conductor (id_conductor)
);

-- Tabla: metodo_pago
-- Descripción: Catálogo de formas de pago aceptadas.
CREATE TABLE IF NOT EXISTS metodo_pago (
    id_metodo_pago INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(30) NOT NULL UNIQUE -- Ej: 'Efectivo', 'Tarjeta'
);

-- Tabla: categoria_servicio
-- Descripción: Niveles de servicio con sus respectivas tarifas.
CREATE TABLE IF NOT EXISTS categoria_servicio (
    id_categoria_servicio INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(30) NOT NULL UNIQUE, -- Ej: 'Económico', 'Premium'
    valor_km DECIMAL(12, 2) NOT NULL -- Tarifa por kilómetro
);

-- Tabla: estado_servicio
-- Descripción: Catálogo de estados del ciclo de vida de un servicio.
CREATE TABLE IF NOT EXISTS estado_servicio (
    id_estado_servicio INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(30) NOT NULL UNIQUE -- Ej: 'Pendiente', 'En proceso', 'Finalizado'
);

-- Tabla: ruta
-- Descripción: Tabla transaccional principal. Registra cada servicio solicitado.
-- Propósito: Almacenar toda la información relacionada con un viaje o servicio.
CREATE TABLE IF NOT EXISTS ruta (
    id_ruta INT AUTO_INCREMENT PRIMARY KEY,
    direccion_origen VARCHAR(50) NOT NULL,
    direccion_destino VARCHAR(50) NOT NULL,
    id_codigo_postal_origen VARCHAR(10) NOT NULL,
    id_codigo_postal_destino VARCHAR(10) NOT NULL,
    distancia_km DECIMAL(8, 2) NOT NULL, -- Distancia calculada del viaje
    fecha_hora_reserva DATETIME NOT NULL, -- Cuándo se solicitó
    fecha_hora_origen DATETIME, -- Cuándo inició el viaje
    fecha_hora_destino DATETIME, -- Cuándo finalizó el viaje
    id_conductor INT, -- Conductor asignado, puede ser null si no a sido asignado aun
    id_tipo_servicio INT NOT NULL,
    id_cliente INT NOT NULL, -- Cliente que solicita
    id_estado_servicio INT NOT NULL, -- Estado actual del viaje
    id_categoria_servicio INT NOT NULL, -- Categoría (tarifa aplicada)
    id_metodo_pago INT NOT NULL,
    total DECIMAL(12, 2), -- Costo total (calculado por trigger)
    pago_conductor DECIMAL(12, 2), -- Ganancia del conductor (calculado por trigger)
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

-- Tabla: pasajero_ruta
-- Descripción: Lista de pasajeros adicionales en una ruta.
-- Propósito: Registro de ocupantes del vehículo además del cliente principal.
CREATE TABLE IF NOT EXISTS pasajero_ruta (
    id_ruta INT,
    nombre_pasajero VARCHAR(100),
    PRIMARY KEY (id_ruta, nombre_pasajero),
    FOREIGN KEY (id_ruta) REFERENCES ruta (id_ruta)
);

-- Tabla: preguntas_seguridad
-- Descripción: Catálogo de preguntas para recuperación de cuenta.
CREATE TABLE IF NOT EXISTS preguntas_seguridad (
    id_pregunta INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(100) NOT NULL UNIQUE
);

-- Tabla: respuestas_seguridad
-- Descripción: Almacena las respuestas de los usuarios a las preguntas de seguridad.
-- Propósito: Mecanismo de validación para recuperación de contraseñas.
CREATE TABLE IF NOT EXISTS respuestas_seguridad (
    id_pregunta INT,
    id_usuario INT,
    respuesta_pregunta VARCHAR(255) NOT NULL,
    PRIMARY KEY (id_pregunta, id_usuario),
    FOREIGN KEY (id_pregunta) REFERENCES preguntas_seguridad (id_pregunta),
    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
);

-- ===================================
-- TRIGGERS
-- ===================================

-- 1. Se cambia el delimitador para permitir bloques BEGIN...END
DELIMITER //

-- Trigger: trg_insert_usuario_cliente
CREATE TRIGGER trg_insert_usuario_cliente
AFTER INSERT ON cliente
FOR EACH ROW
BEGIN
  IF NOT EXISTS (SELECT 1 FROM usuario WHERE correo = NEW.correo) THEN
    INSERT INTO usuario (id_tipo_usuario, correo, contrasenia)
    SELECT id_tipo_usuario, NEW.correo, NEW.identificacion
    FROM tipo_usuario
    WHERE descripcion = 'Cliente'
    LIMIT 1;
  END IF;
END//

-- Trigger: trg_insert_usuario_conductor
CREATE TRIGGER trg_insert_usuario_conductor
AFTER INSERT ON conductor
FOR EACH ROW
BEGIN
  IF NOT EXISTS (SELECT 1 FROM usuario WHERE correo = NEW.correo) THEN
    INSERT INTO usuario (id_tipo_usuario, correo, contrasenia)
    SELECT id_tipo_usuario, NEW.correo, NEW.identificacion
    FROM tipo_usuario
    WHERE descripcion = 'Conductor'
    LIMIT 1;
  END IF;
END//

-- Trigger: trg_insert_usuario_admin
CREATE TRIGGER trg_insert_usuario_admin
AFTER INSERT ON administrador
FOR EACH ROW
BEGIN
  IF NOT EXISTS (SELECT 1 FROM usuario WHERE correo = NEW.correo) THEN
    INSERT INTO usuario (id_tipo_usuario, correo, contrasenia)
    SELECT id_tipo_usuario, NEW.correo, NEW.identificacion
    FROM tipo_usuario
    WHERE descripcion = 'Administrador'
    LIMIT 1;
  END IF;
END//

-- Trigger: trg_calcular_total_ruta
-- Propósito: Calcular automáticamente el costo total del servicio antes de insertarlo.
-- Lógica: Multiplica la distancia (km) por el valor del kilómetro de la categoría seleccionada.
CREATE TRIGGER trg_calcular_total_ruta
BEFORE INSERT ON ruta
FOR EACH ROW
BEGIN
  DECLARE precio_km DECIMAL(12,2);
  SELECT valor_km INTO precio_km 
  FROM categoria_servicio 
  WHERE id_categoria_servicio = NEW.id_categoria_servicio 
  LIMIT 1;
  SET NEW.total = NEW.distancia_km * precio_km;
END//

-- Trigger: trg_calcular_pago_conductor
-- Propósito: Calcular la comisión del conductor.
-- Lógica: Asigna al conductor el 30% del valor total del servicio.
CREATE TRIGGER trg_calcular_pago_conductor
BEFORE INSERT ON ruta
FOR EACH ROW
BEGIN
  IF NEW.total IS NOT NULL THEN 
    SET NEW.pago_conductor = ROUND(NEW.total * 0.30, 2);
  END IF;
END//

-- Trigger: trg_update_correo_cliente
-- Propósito: Mantener la consistencia del correo electrónico.
-- Lógica: Si se actualiza el correo en la tabla 'cliente', se actualiza también en la tabla 'usuario'.
CREATE TRIGGER trg_update_correo_cliente
BEFORE UPDATE ON cliente
FOR EACH ROW
BEGIN
  IF OLD.correo <> NEW.correo THEN
    UPDATE usuario
    SET correo = NEW.correo
    WHERE correo = OLD.correo;
  END IF;
END//

-- Los demás triggers de actualización de correo siguen la misma estructura
-- Trigger: trg_update_correo_conductor
CREATE TRIGGER trg_update_correo_conductor
BEFORE UPDATE ON conductor
FOR EACH ROW
BEGIN
  IF OLD.correo <> NEW.correo THEN
    UPDATE usuario
    SET correo = NEW.correo
    WHERE correo = OLD.correo;
  END IF;
END//

-- Trigger: trg_update_correo_admin
CREATE TRIGGER trg_update_correo_admin
BEFORE UPDATE ON administrador
FOR EACH ROW
BEGIN
  IF OLD.correo <> NEW.correo THEN
    UPDATE usuario
    SET correo = NEW.correo
    WHERE correo = OLD.correo;
  END IF;
END//

-- Se restablece el delimitador al punto y coma
DELIMITER ;

-- ======== DATOS DE PRUEBA ========

-- 1. Tablas maestras básicas
-- Se insertan los catálogos fundamentales: Tipos de usuario, identificación, género, países y la estructura geográfica completa.
INSERT INTO tipo_usuario (descripcion) VALUES ('Administrador'), ('Conductor'), ('Cliente');
INSERT INTO tipo_identificacion (descripcion) VALUES ('Cédula de Ciudadanía'), ('Cédula de Extranjería'), ('Pasaporte');
INSERT INTO genero (descripcion) VALUES ('Masculino'), ('Femenino'), ('Otro');
INSERT INTO pais (nombre) VALUES ('Colombia'), ('Venezuela'), ('Ecuador');
INSERT INTO codigo_postal (id_codigo_postal, id_pais, departamento, ciudad) VALUES
-- COLOMBIA
('110111', 1, 'Cundinamarca', 'Bogotá'),
('250001', 1, 'Cundinamarca', 'Soacha'),
('250101', 1, 'Cundinamarca', 'Chía'),
('250201', 1, 'Cundinamarca', 'Zipaquirá'),
('250301', 1, 'Cundinamarca', 'Facatativá'),
('250401', 1, 'Cundinamarca', 'Mosquera'),
('250501', 1, 'Cundinamarca', 'Funza'),
('250601', 1, 'Cundinamarca', 'Madrid'),
('250701', 1, 'Cundinamarca', 'Cajicá'),
('250801', 1, 'Cundinamarca', 'La Calera'),
('250901', 1, 'Cundinamarca', 'Girardot'),
('080001', 1, 'Atlántico', 'Barranquilla'),
('081001', 1, 'Atlántico', 'Soledad'),
('082001', 1, 'Atlántico', 'Malambo'),
('083001', 1, 'Atlántico', 'Sabanalarga'),
('084001', 1, 'Atlántico', 'Galapa'),
('680001', 1, 'Santander', 'Bucaramanga'),
('681001', 1, 'Santander', 'Floridablanca'),
('682001', 1, 'Santander', 'Girón'),
('683001', 1, 'Santander', 'Piedecuesta'),
('684001', 1, 'Santander', 'Barrancabermeja'),
('130001', 1, 'Bolívar', 'Cartagena'),
('131001', 1, 'Bolívar', 'Magangué'),
('132001', 1, 'Bolívar', 'Turbaco'),
('133001', 1, 'Bolívar', 'Arjona'),
('134001', 1, 'Bolívar', 'El Carmen de Bolívar'),
('730001', 1, 'Tolima', 'Ibagué'),
('731001', 1, 'Tolima', 'Espinal'),
('732001', 1, 'Tolima', 'Melgar'),
('733001', 1, 'Tolima', 'Mariquita'),
('734001', 1, 'Tolima', 'Líbano'),
('500001', 1, 'Meta', 'Villavicencio'),
('501001', 1, 'Meta', 'Acacías'),
('502001', 1, 'Meta', 'Granada'),
('503001', 1, 'Meta', 'Cumaral'),
('504001', 1, 'Meta', 'Restrepo'),
('540001', 1, 'Norte de Santander', 'Cúcuta'),
('541001', 1, 'Norte de Santander', 'Ocaña'),
('542001', 1, 'Norte de Santander', 'Pamplona'),
('543001', 1, 'Norte de Santander', 'Los Patios'),
('544001', 1, 'Norte de Santander', 'Villa del Rosario'),
('660001', 1, 'Risaralda', 'Pereira'),
('661001', 1, 'Risaralda', 'Dosquebradas'),
('662001', 1, 'Risaralda', 'Santa Rosa de Cabal'),
('663001', 1, 'Risaralda', 'La Virginia'),
('664001', 1, 'Risaralda', 'Belén de Umbría'),
('630001', 1, 'Quindío', 'Armenia'),
('631001', 1, 'Quindío', 'Calarcá'),
('632001', 1, 'Quindío', 'Montenegro'),
('633001', 1, 'Quindío', 'La Tebaida'),
('634001', 1, 'Quindío', 'Quimbaya'),
('190001', 1, 'Cauca', 'Popayán'),
('191001', 1, 'Cauca', 'Santander de Quilichao'),
('192001', 1, 'Cauca', 'Puerto Tejada'),
('193001', 1, 'Cauca', 'Patía'),
('194001', 1, 'Cauca', 'Piendamó'),
('200001', 1, 'Cesar', 'Valledupar'),
('201001', 1, 'Cesar', 'Aguachica'),
('202001', 1, 'Cesar', 'Bosconia'),
('203001', 1, 'Cesar', 'Curumaní'),
('204001', 1, 'Cesar', 'Chiriguaná'),
('760001', 1, 'Valle del Cauca', 'Cali'),
('761001', 1, 'Valle del Cauca', 'Palmira'),
('762001', 1, 'Valle del Cauca', 'Buga'),
('763001', 1, 'Valle del Cauca', 'Tuluá'),
('764001', 1, 'Valle del Cauca', 'Yumbo'),
('765001', 1, 'Valle del Cauca', 'Jamundí'),
('766001', 1, 'Valle del Cauca', 'Cartago'),
('767001', 1, 'Valle del Cauca', 'Sevilla'),
('768001', 1, 'Valle del Cauca', 'Caicedonia'),
('769001', 1, 'Valle del Cauca', 'Zarzal'),
('050001', 1, 'Antioquia', 'Medellín'),
('050002', 1, 'Antioquia', 'Bello'),
('050003', 1, 'Antioquia', 'Envigado'),
('050004', 1, 'Antioquia', 'Itagüí'),
('050005', 1, 'Antioquia', 'Rionegro'),
('050006', 1, 'Antioquia', 'Apartadó'),
('050007', 1, 'Antioquia', 'Turbo'),
('050008', 1, 'Antioquia', 'La Ceja'),
('050009', 1, 'Antioquia', 'Sabaneta'),
('050010', 1, 'Antioquia', 'Copacabana'),
('050011', 1, 'Antioquia', 'Girardota'),
('170001', 1, 'Caldas', 'Manizales'),
('171001', 1, 'Caldas', 'La Dorada'),
('172001', 1, 'Caldas', 'Villamaría'),
('173001', 1, 'Caldas', 'Chinchiná'),
('174001', 1, 'Caldas', 'Riosucio'),
('410001', 1, 'Huila', 'Neiva'),
('411001', 1, 'Huila', 'Pitalito'),
('412001', 1, 'Huila', 'Garzón'),
('413001', 1, 'Huila', 'La Plata'),
('414001', 1, 'Huila', 'Campoalegre'),
('520001', 1, 'Nariño', 'Pasto'),
('521001', 1, 'Nariño', 'Tumaco'),
('522001', 1, 'Nariño', 'Ipiales'),
('523001', 1, 'Nariño', 'Túquerres'),
('524001', 1, 'Nariño', 'Sandoná'),
('700001', 1, 'Sucre', 'Sincelejo'),
('701001', 1, 'Sucre', 'Corozal'),
('702001', 1, 'Sucre', 'Sampués'),
('703001', 1, 'Sucre', 'San Marcos'),
('704001', 1, 'Sucre', 'Tolú'),
('230001', 1, 'Córdoba', 'Montería'),
('231001', 1, 'Córdoba', 'Sahagún'),
('232001', 1, 'Córdoba', 'Lorica'),
('233001', 1, 'Córdoba', 'Planeta Rica'),
('234001', 1, 'Córdoba', 'Tierralta'),
('470001', 1, 'Magdalena', 'Santa Marta'),
('471001', 1, 'Magdalena', 'Ciénaga'),
('472001', 1, 'Magdalena', 'Fundación'),
('473001', 1, 'Magdalena', 'Aracataca'),
('474001', 1, 'Magdalena', 'El Banco'),
('150001', 1, 'Boyacá', 'Tunja'),
('151001', 1, 'Boyacá', 'Duitama'),
('152001', 1, 'Boyacá', 'Sogamoso'),
('153001', 1, 'Boyacá', 'Chiquinquirá'),
('154001', 1, 'Boyacá', 'Paipa'),
('810001', 1, 'Arauca', 'Arauca'),
('811001', 1, 'Arauca', 'Saravena'),
('812001', 1, 'Arauca', 'Tame'),
('813001', 1, 'Arauca', 'Arauquita'),
('814001', 1, 'Arauca', 'Fortul'),
('850001', 1, 'Casanare', 'Yopal'),
('851001', 1, 'Casanare', 'Aguazul'),
('852001', 1, 'Casanare', 'Villanueva'),
('853001', 1, 'Casanare', 'Tauramena'),
('854001', 1, 'Casanare', 'Monterrey'),
('860001', 1, 'Putumayo', 'Mocoa'),
('861001', 1, 'Putumayo', 'Puerto Asís'),
('862001', 1, 'Putumayo', 'Orito'),
('863001', 1, 'Putumayo', 'Sibundoy'),
('864001', 1, 'Putumayo', 'San Miguel'),
('950001', 1, 'Guaviare', 'San José del Guaviare'),
('951001', 1, 'Guaviare', 'El Retorno'),
('952001', 1, 'Guaviare', 'Calamar'),
('953001', 1, 'Guaviare', 'Miraflores'),
('440001', 1, 'La Guajira', 'Riohacha'),
('441001', 1, 'La Guajira', 'Maicao'),
('442001', 1, 'La Guajira', 'Uribia'),
('443001', 1, 'La Guajira', 'Fonseca'),
('444001', 1, 'La Guajira', 'San Juan del Cesar'),
('180001', 1, 'Caquetá', 'Florencia'),
('181001', 1, 'Caquetá', 'San Vicente del Caguán'),
('182001', 1, 'Caquetá', 'Puerto Rico'),
('183001', 1, 'Caquetá', 'Morelia'),
('184001', 1, 'Caquetá', 'Belén de los Andaquíes'),
('270001', 1, 'Chocó', 'Quibdó'),
('271001', 1, 'Chocó', 'Istmina'),
('272001', 1, 'Chocó', 'Tadó'),
('273001', 1, 'Chocó', 'Bahía Solano'),
('274001', 1, 'Chocó', 'Condoto'),
('505001', 1, 'Meta', 'Puerto López'),
('506001', 1, 'Meta', 'Puerto Gaitán'),
('910001', 1, 'Amazonas', 'Leticia'),
('911001', 1, 'Amazonas', 'Puerto Nariño'),
('990001', 1, 'Vichada', 'Puerto Carreño'),
('991001', 1, 'Vichada', 'La Primavera'),
-- VENEZUELA
('1010', 2, 'Distrito Capital', 'Caracas'),
('1011', 2, 'Distrito Capital', 'El Paraíso'),
('1012', 2, 'Distrito Capital', 'La Candelaria'),
('1013', 2, 'Distrito Capital', 'Chacao'),
('1014', 2, 'Distrito Capital', 'El Recreo'),
('1201', 2, 'Miranda', 'Los Teques'),
('1202', 2, 'Miranda', 'Guarenas'),
('1203', 2, 'Miranda', 'Guatire'),
('1204', 2, 'Miranda', 'Higuerote'),
('1205', 2, 'Miranda', 'Baruta'),
('2001', 2, 'Carabobo', 'Valencia'),
('2002', 2, 'Carabobo', 'Naguanagua'),
('2003', 2, 'Carabobo', 'San Diego'),
('2004', 2, 'Carabobo', 'Guacara'),
('2005', 2, 'Carabobo', 'Puerto Cabello'),
('3001', 2, 'Lara', 'Barquisimeto'),
('3002', 2, 'Lara', 'Carora'),
('3003', 2, 'Lara', 'El Tocuyo'),
('3004', 2, 'Lara', 'Quíbor'),
('3005', 2, 'Lara', 'Duaca'),
('4001', 2, 'Zulia', 'Maracaibo'),
('4002', 2, 'Zulia', 'Cabimas'),
('4003', 2, 'Zulia', 'Ciudad Ojeda'),
('4004', 2, 'Zulia', 'Santa Rita'),
('4005', 2, 'Zulia', 'Machiques'),
('5001', 2, 'Táchira', 'San Cristóbal'),
('5002', 2, 'Táchira', 'Ureña'),
('5003', 2, 'Táchira', 'Táriba'),
('5004', 2, 'Táchira', 'Rubio'),
('5005', 2, 'Táchira', 'La Fría'),
('2101', 2, 'Aragua', 'Maracay'),
('2102', 2, 'Aragua', 'Cagua'),
('2103', 2, 'Aragua', 'La Victoria'),
('2104', 2, 'Aragua', 'Turmero'),
('2105', 2, 'Aragua', 'El Limón'),
('5101', 2, 'Mérida', 'Mérida'),
('5102', 2, 'Mérida', 'Ejido'),
('5103', 2, 'Mérida', 'Tovar'),
('5104', 2, 'Mérida', 'El Vigía'),
('5105', 2, 'Mérida', 'Mucuchíes'),
('8001', 2, 'Bolívar', 'Ciudad Bolívar'),
('8002', 2, 'Bolívar', 'Ciudad Guayana'),
('8003', 2, 'Bolívar', 'Upata'),
('8004', 2, 'Bolívar', 'El Callao'),
('8005', 2, 'Bolívar', 'Tumeremo'),
('9001', 2, 'Falcón', 'Coro'),
('9002', 2, 'Falcón', 'Punto Fijo'),
('9003', 2, 'Falcón', 'Chichiriviche'),
('9004', 2, 'Falcón', 'Tucacas'),
('9005', 2, 'Falcón', 'Dabajuro'),
-- ECUADOR
('170150', 3, 'Pichincha', 'Quito'),
('170151', 3, 'Pichincha', 'Cumbayá'),
('170152', 3, 'Pichincha', 'Tumbaco'),
('170153', 3, 'Pichincha', 'Sangolquí'),
('170154', 3, 'Pichincha', 'Machachi'),
('090101', 3, 'Guayas', 'Guayaquil'),
('090102', 3, 'Guayas', 'Daule'),
('090103', 3, 'Guayas', 'Samborondón'),
('090104', 3, 'Guayas', 'Durán'),
('090105', 3, 'Guayas', 'Milagro'),
('010101', 3, 'Azuay', 'Cuenca'),
('010102', 3, 'Azuay', 'Gualaceo'),
('010103', 3, 'Azuay', 'Paute'),
('010104', 3, 'Azuay', 'Sigsig'),
('010105', 3, 'Azuay', 'Chordeleg'),
('130101', 3, 'Manabí', 'Portoviejo'),
('130102', 3, 'Manabí', 'Manta'),
('130103', 3, 'Manabí', 'Chone'),
('130104', 3, 'Manabí', 'Jipijapa'),
('130105', 3, 'Manabí', 'Bahía de Caráquez'),
('180101', 3, 'Tungurahua', 'Ambato'),
('180102', 3, 'Tungurahua', 'Baños'),
('180103', 3, 'Tungurahua', 'Pelileo'),
('180104', 3, 'Tungurahua', 'Píllaro'),
('180105', 3, 'Tungurahua', 'Mocha'),
('110101', 3, 'Loja', 'Loja'),
('110102', 3, 'Loja', 'Catamayo'),
('110103', 3, 'Loja', 'Macará'),
('110104', 3, 'Loja', 'Zapotillo'),
('110105', 3, 'Loja', 'Saraguro'),
('080101', 3, 'Esmeraldas', 'Esmeraldas'),
('080102', 3, 'Esmeraldas', 'Atacames'),
('080103', 3, 'Esmeraldas', 'Quinindé'),
('080104', 3, 'Esmeraldas', 'San Lorenzo'),
('080105', 3, 'Esmeraldas', 'Muisne'),
('100101', 3, 'Imbabura', 'Ibarra'),
('100102', 3, 'Imbabura', 'Otavalo'),
('100103', 3, 'Imbabura', 'Cotacachi'),
('100104', 3, 'Imbabura', 'Atuntaqui'),
('100105', 3, 'Imbabura', 'Pimampiro'),
('070101', 3, 'El Oro', 'Machala'),
('070102', 3, 'El Oro', 'Santa Rosa'),
('070103', 3, 'El Oro', 'Arenillas'),
('070104', 3, 'El Oro', 'Huaquillas'),
('070105', 3, 'El Oro', 'Pasaje'),
('230101', 3, 'Santo Domingo', 'Santo Domingo'),
('230102', 3, 'Santo Domingo', 'La Concordia'),
('230103', 3, 'Santo Domingo', 'Alluriquín'),
('230104', 3, 'Santo Domingo', 'Valle Hermoso'),
('230105', 3, 'Santo Domingo', 'Puerto Limón');

-- 2. Tablas de vehículos y servicios
-- Se definen los estados, marcas, líneas y colores de vehículos, así como los tipos y categorías de servicio.
INSERT INTO estado_vehiculo (descripcion) VALUES ('Activo'), ('En Mantenimiento'), ('Inactivo');
INSERT INTO marca_vehiculo (nombre_marca) VALUES 
('Chevrolet'), 
('Renault'), 
('Nissan'), 
('Mazda'),
('Toyota'),
('Ford'),
('Hyundai'),
('Kia'),
('Volkswagen'),
('Mercedes-Benz'),
('Mitsubishi'),
('Suzuki'),
('BMW'),
('Audi'),
('Dodge');

-- Insertar líneas para Chevrolet (id_marca = 1)
INSERT INTO linea_vehiculo (id_linea, id_marca) VALUES 
('Aveo', 1),
('Spark GT', 1),
('Cruze', 1),
('Trax', 1),
('Equinox', 1),
('Captiva', 1),
('Orlando', 1),
('Tornado', 1),
('D-Max', 1),
('Silverado', 1),
('Colorado', 1),
('Tahoe', 1),
('Suburban', 1),
('Blazer', 1),
('Malibu', 1);
-- Insertar líneas para Renault (id_marca = 2)
INSERT INTO linea_vehiculo (id_linea, id_marca) VALUES 
('Sandero', 2),
('Sandero Stepway', 2),
('Duster', 2),
('Koleos', 2),
('Captur', 2),
('Kwid', 2),
('Twingo', 2),
('Clio', 2),
('Megane', 2),
('Scenic', 2),
('Kangoo', 2),
('Master', 2),        
('Trafic', 2),
('Alaskan', 2),
('Symbol', 2);
-- Insertar líneas para Nissan (id_marca = 3)
INSERT INTO linea_vehiculo (id_linea, id_marca) VALUES 
('March', 3),
('Versa', 3),
('Sentra', 3),
('Altima', 3),
('Maxima', 3),
('X-Trail', 3),
('Qashqai', 3),
('Juke', 3),
('Murano', 3),
('Pathfinder', 3),
('Frontier', 3),
('Navara', 3),
('Urvan', 3),
('Tiida', 3),
('Kicks', 3);
-- Insertar líneas para Mazda (id_marca = 4)
INSERT INTO linea_vehiculo (id_linea, id_marca) VALUES 
('Mazda2', 4),
('Mazda3', 4),
('Mazda6', 4),
('CX-3', 4),
('CX-5', 4),
('CX-9', 4),
('CX-30', 4),
('BT-50', 4),
('MX-5', 4),
('RX-8', 4),
('CX-7', 4),
('CX-8', 4),
('Tribute', 4),
('Premacy', 4),
('Bongo', 4);
-- Insertar líneas para Toyota (id_marca = 5)
INSERT INTO linea_vehiculo (id_linea, id_marca) VALUES 
('Yaris', 5),
('Corolla', 5),
('Camry', 5),
('RAV4', 5),
('Hilux', 5),
('Fortuner', 5),
('Prado', 5),
('Land Cruiser', 5),
('4Runner', 5),
('Tacoma', 5),
('Tundra', 5),
('Sienna', 5),
('Highlander', 5),
('Avanza', 5),
('Innova', 5);
-- Insertar líneas para Ford (id_marca = 6)
INSERT INTO linea_vehiculo (id_linea, id_marca) VALUES 
('Fiesta', 6),
('Focus', 6),
('Fusion', 6),
('Escape', 6),
('Explorer', 6),
('Expedition', 6),
('Ranger', 6),
('F-150', 6),
('Transit', 6),
('Tourneo', 6),
('EcoSport', 6),
('Edge', 6),
('Bronco', 6),
('Mustang', 6),
('Territory', 6);
-- Insertar líneas para Hyundai (id_marca = 7)
INSERT INTO linea_vehiculo (id_linea, id_marca) VALUES 
('Accent', 7),
('Elantra', 7),
('Tucson', 7),
('Santa Fe', 7),
('Creta', 7),
('Kona', 7),
('Palisade', 7),
('Venue', 7),
('i10', 7),
('i20', 7),
('i30', 7),
('Grand i10', 7),
('H-1', 7),
('Staria', 7),
('Sonata', 7);
-- Insertar líneas para Kia (id_marca = 8)
INSERT INTO linea_vehiculo (id_linea, id_marca) VALUES 
('Rio', 8),
('Forte', 8),
('Optima', 8),
('Sportage', 8),
('Sorento', 8),
('Picanto', 8),
('Stonic', 8),
('Seltos', 8),
('Carnival', 8),
('Telluride', 8),
('Soul', 8),
('Niro', 8),
('Cerato', 8),
('K2500', 8),
('K2700', 8);
-- Insertar líneas para Volkswagen (id_marca = 9)
INSERT INTO linea_vehiculo (id_linea, id_marca) VALUES 
('Golf', 9),
('Jetta', 9),
('Passat', 9),
('Tiguan', 9),
('Amarok', 9),
('T-Cross', 9),
('Taos', 9),
('Virtus', 9),
('Polo', 9),
('Vento', 9),
('Transporter', 9),
('Crafter', 9),
('Caddy', 9),
('Teramont', 9),
('Arteon', 9);
-- Insertar líneas para Mercedes-Benz (id_marca = 10)
INSERT INTO linea_vehiculo (id_linea, id_marca) VALUES 
('Clase A', 10),
('Clase C', 10),
('Clase E', 10),
('Clase G', 10),
('Sprinter', 10),
('Vito', 10),
('Viano', 10),
('Clase B', 10),
('Clase CLA', 10),
('Clase GLA', 10),
('Clase GLB', 10),
('Clase GLC', 10),
('Clase GLE', 10),
('Clase GLS', 10),
('Clase V', 10);
-- Insertar líneas para Mitsubishi (id_marca = 11)
INSERT INTO linea_vehiculo (id_linea, id_marca) VALUES 
('L200', 11),
('Outlander', 11),
('Eclipse Cross', 11),
('ASX', 11),
('Montero', 11),
('Pajero', 11),
('Mirage', 11),
('Lancer', 11),
('XPANDER', 11),
('Delica', 11),
('Triton', 11),
('Strada', 11),
('Galant', 11),
('Space Star', 11),
('Attrage', 11);

INSERT INTO color_vehiculo (descripcion) VALUES 
('Blanco'), 
('Negro'), 
('Gris'), 
('Rojo'), 
('Azul'),
('Verde'),
('Amarillo'),
('Plateado'),
('Beige'),
('Marrón'),
('Vino Tinto'),
('Verde Militar'),
('Naranja');

INSERT INTO tipo_servicio (descripcion) VALUES ('Alimentos'),('Pasajeros'),('Pasajeros y alimentos');

INSERT INTO categoria_servicio (descripcion, valor_km) VALUES 
('Económico', 2500.00),
('Estándar', 3500.00),
('Premium', 5000.00);
INSERT INTO estado_servicio (descripcion) VALUES
('Cancelado'),
('En proceso'),
('Finalizado'),
('Pendiente');
INSERT INTO metodo_pago (descripcion) VALUES ('Efectivo'),('Tarjeta Debito'),('Tarjeta Credito'),('Transferencia');

-- 3. Vehículos
-- Registro de la flota inicial disponible.
INSERT INTO vehiculo (placa, linea_vehiculo, modelo, id_color, id_marca, id_tipo_servicio, id_estado_vehiculo) VALUES 
('ABC123', 'D-Max', 2020, 1, 1, 1, 1),
('DEF456', 'Trax', 2021, 2, 1, 1, 1),
('SEX69H', 'Duster', 2023, 5, 2, 3, 1),
('GHI789', 'Sandero', 2019, 3, 2, 1, 1);

-- Administradores
INSERT INTO administrador (identificacion, id_tipo_identificacion, nombre, direccion, correo, id_genero, codigo_postal) VALUES 
('1012345678', 1, 'Carlos Andrés Rodríguez García', 'Calle 72 # 11-20', 'admin@empresa.com', 1, '110111'),
('1018765432', 1, 'María Fernanda López Martínez', 'Carrera 50 # 30-45', 'maria.lopez@empresa.com', 2, '050001');

INSERT INTO administrador (identificacion, id_tipo_identificacion, nombre, direccion, correo, id_genero, codigo_postal) VALUES 
('1029', 1, 'Admin William Diaz', 'Carrera 50 # 30-45', 'w@empresa.com', 2, '050001');

-- Teléfonos de administradores
INSERT INTO telefono_administrador (id_administrador, telefono) VALUES 
(1, 3101234567),
(1, 3209876543),
(2, 3157654321);

-- 5. Clientes
-- Registro de clientes de prueba.
INSERT INTO cliente (identificacion, id_tipo_identificacion, nombre, direccion, correo, id_genero, id_pais_nacionalidad, codigo_postal) VALUES 
('1023456789', 1, 'Ana Sofía Martínez Gómez', 'Calle 100 # 15-30', 'ana.martinez@email.com', 2, 1, '110111'),
('1034567890', 1, 'Juan Pablo Herrera Sánchez', 'Carrera 7 # 45-12', 'juan.herrera@email.com', 1, 1, '110111'),
('1029', 1, 'William dog', 'Calle 100 # 15-30', '1@1.com', 1, 1, '760001'),
('1045678901', 1, 'Laura Camila Torres Ramírez', 'Avenida 68 # 25-40', 'laura.torres@email.com', 2, 1, '110111');

-- Teléfonos de clientes
INSERT INTO telefono_cliente (id_cliente, telefono) VALUES 
(1, 3012345678),
(1, 3123456789),
(2, 3187654321),
(3, 3156789012);

-- 6. Estados de conductor
-- Definición de estados de disponibilidad (Conectado/Desconectado).
INSERT INTO estado_conductor (descripcion) VALUES ('Conectado'), ('Desconectado');

-- 7. Conductores
-- Registro de conductores y asociación con sus vehículos.
INSERT INTO conductor (id_estado_conductor, placa_vehiculo, identificacion, id_tipo_identificacion, nombre, direccion, correo, id_genero, codigo_postal, id_pais_nacionalidad, url_foto) VALUES 
(1, 'ABC123', '1056789012', 1, 'Roberto Carlos Méndez Vargas', 'Calle 80 # 20-15', 'conductor1@empresa.com', 1, '110111', 1, 'https://example.com/fotos/conductor1.jpg'),
(2, 'DEF456', '1067890123', 1, 'Sandra Milena Ramírez Ortiz', 'Carrera 30 # 50-25', 'conductor2@empresa.com', 2, '050001', 1, 'https://example.com/fotos/conductor2.jpg'),
(2, 'SEX69H', '100', 1, 'Mostro', 'Carrera 30 # 50-25', '2@2.com', 3, '050001', 1, 'https://example.com/fotos/mostro.jpg'),
(2, 'GHI789', '1078901234', 1, 'Diego Armando Suárez Pérez', 'Avenida 6N # 35-10', 'conductor3@empresa.com', 1, '760001', 1, 'https://example.com/fotos/conductor3.jpg');

-- Teléfonos de conductores
INSERT INTO telefono_conductor (id_conductor, telefono) VALUES 
(1, 3123456789),
(1, 3234567890),
(2, 3145678901),
(3, 3167890123);

-- 8. Rutas (Servicios)
-- Historial de viajes realizados para pruebas de reportes.
INSERT INTO ruta (direccion_origen, direccion_destino, id_codigo_postal_origen, id_codigo_postal_destino, distancia_km, fecha_hora_reserva, fecha_hora_origen, fecha_hora_destino, id_conductor, id_tipo_servicio, id_cliente, id_estado_servicio, id_categoria_servicio, id_metodo_pago) VALUES 
('Aeropuerto El Dorado', 'Centro Comercial Santa Fe', '110111', '110111', 25.50, '2024-01-15 08:00:00', '2024-01-15 09:00:00', '2024-01-15 09:45:00', 1, 1, 1, 3, 1, 1),
('Terminal de Transporte', 'Universidad Nacional', '110111', '110111', 18.30, '2024-01-16 14:00:00', '2024-01-16 14:30:00', '2024-01-16 15:15:00', 2, 1, 2, 3, 2, 3),
('Hospital San Ignacio', 'Centro de la ciudad', '110111', '110111', 12.75, '2024-01-17 10:00:00', '2024-01-17 10:30:00', '2024-01-17 11:00:00', 3, 1, 3, 2, 1, 4);

-- Pasajeros en rutas
INSERT INTO pasajero_ruta (id_ruta, nombre_pasajero) VALUES 
(1, 'María González'),
(1, 'Pedro Sánchez'),
(2, 'Carlos Ramírez');

-- 9. Preguntas de seguridad
-- Catálogo de preguntas para recuperación de contraseña.
INSERT INTO preguntas_seguridad (descripcion) VALUES 
('¿Cuál es el nombre de tu primera mascota?'),
('¿En qué ciudad naciste?'),
('¿Cuál es el nombre de tu colegio favorito?'),
('¿Cuál es el nombre de tu mejor amigo de la infancia?'),
('¿Cuál es tu comida favorita?');

-- 10. Respuestas de seguridad
-- Asociación de respuestas a los usuarios creados.
-- Nota: Los IDs de usuario se generarán automáticamente por los triggers al insertar en las tablas específicas.
-- Estos INSERTs deben ejecutarse después de que los triggers hayan creado los usuarios.
INSERT INTO respuestas_seguridad (id_pregunta, id_usuario, respuesta_pregunta)
SELECT 1, id_usuario, 'Pelusa' FROM usuario WHERE correo = 'ana.martinez@email.com'
UNION ALL
SELECT 2, id_usuario, 'Bogotá' FROM usuario WHERE correo = 'juan.herrera@email.com'
UNION ALL
SELECT 3, id_usuario, 'San José' FROM usuario WHERE correo = 'admin@empresa.com';

insert into respuestas_seguridad (id_pregunta, id_usuario, respuesta_pregunta) VALUES
(1, 7,'mascota'),
(2, 7,'ciudad'),
(3, 7,'colegio');

insert into respuestas_seguridad (id_pregunta, id_usuario, respuesta_pregunta) VALUES
(1, 3,'mascota'),
(2, 3,'ciudad'),
(3, 3,'colegio');

-- ======== VISTAS CONVENCIONALES ========

-- Vista: vw_total_por_tipo_y_categoria
-- Descripción: Reporte de rendimiento por tipo de servicio y categoría.
-- Muestra: Cantidad de servicios y valor total recaudado.
CREATE OR REPLACE VIEW vw_total_por_tipo_y_categoria AS
SELECT
    ts.descripcion AS tipo_servicio,
    cs.descripcion AS categoria_servicio,
    COUNT(r.id_ruta) AS cantidad_servicios,
    COALESCE(SUM(r.total), 0) AS valor_total
FROM
    ruta r
    JOIN tipo_servicio ts ON r.id_tipo_servicio = ts.id_tipo_servicio
    JOIN categoria_servicio cs ON r.id_categoria_servicio = cs.id_categoria_servicio
GROUP BY
    ts.descripcion,
    cs.descripcion
ORDER BY ts.descripcion, cs.descripcion;

-- Vista: vw_cantidad_servicios_por_mes_y_tipo
-- Descripción: Análisis temporal de la demanda.
-- Muestra: Cantidad de servicios agrupados por mes y tipo.
CREATE OR REPLACE VIEW vw_cantidad_servicios_por_mes_y_tipo AS
SELECT
    DATE_FORMAT(r.fecha_hora_origen, '%Y-%m') AS periodo,
    ts.descripcion AS tipo_servicio,
    COUNT(r.id_ruta) AS cantidad_servicios
FROM ruta r
    JOIN tipo_servicio ts ON r.id_tipo_servicio = ts.id_tipo_servicio
GROUP BY
    DATE_FORMAT(r.fecha_hora_origen, '%Y-%m'),
    ts.descripcion
ORDER BY periodo, tipo_servicio;

-- Vista: vw_clientes_con_servicios
-- Descripción: Ranking de clientes.
-- Muestra: Clientes ordenados por cantidad de servicios solicitados y gasto total.
CREATE OR REPLACE VIEW vw_clientes_con_servicios AS
SELECT
    c.id_cliente,
    c.nombre,
    c.correo,
    COUNT(r.id_ruta) AS cantidad_servicios,
    COALESCE(SUM(r.total), 0) AS valor_total
FROM cliente c
    JOIN ruta r ON c.id_cliente = r.id_cliente
GROUP BY
    c.id_cliente,
    c.nombre,
    c.correo
ORDER BY cantidad_servicios DESC;

-- Vista: vw_total_por_metodo_pago
-- Descripción: Preferencias de pago.
-- Muestra: Uso de cada método de pago y volumen de dinero transaccionado.
CREATE OR REPLACE VIEW vw_total_por_metodo_pago AS
SELECT
    mp.descripcion AS metodo_pago,
    COUNT(r.id_ruta) AS cantidad_servicios,
    COALESCE(SUM(r.total), 0) AS valor_total
FROM ruta r
    JOIN metodo_pago mp ON r.id_metodo_pago = mp.id_metodo_pago
GROUP BY
    mp.descripcion
ORDER BY valor_total DESC;

-- Vista: vw_analisis_categoria_metodo_pago
-- Descripción: Análisis cruzado detallado.
-- Muestra: Métricas financieras y operativas desglosadas por categoría y método de pago.
-- Incluye: Ganancia de la empresa, pagos a conductores y conteos únicos.
CREATE OR REPLACE VIEW vw_analisis_categoria_metodo_pago AS
SELECT
    cs.descripcion AS categoria_servicio,
    mp.descripcion AS metodo_pago,
    COUNT(r.id_ruta) AS cantidad_servicios,
    COALESCE(SUM(r.total), 0) AS valor_total,
    COALESCE(AVG(r.total), 0) AS valor_promedio,
    COALESCE(SUM(r.pago_conductor), 0) AS pago_conductores,
    COALESCE(
        SUM(r.total) - COALESCE(SUM(r.pago_conductor), 0),
        0
    ) AS ganancia_empresa,
    COUNT(DISTINCT r.id_cliente) AS clientes_unicos,
    COUNT(DISTINCT r.id_conductor) AS conductores_unicos
FROM
    ruta r
    JOIN categoria_servicio cs ON r.id_categoria_servicio = cs.id_categoria_servicio
    JOIN metodo_pago mp ON r.id_metodo_pago = mp.id_metodo_pago
WHERE
    r.id_estado_servicio IN (2, 3) -- Solo servicios en proceso y finalizados
GROUP BY
    cs.descripcion,
    mp.descripcion
ORDER BY valor_total DESC;

-- ======== VISTAS MATERIALIZADAS ========

-- Esta vista materializada almacenaría los ingresos mensuales precalculados
-- Tabla Materializada: mv_resumen_mensual_ingresos
-- Descripción: Almacena estadísticas mensuales precalculadas para mejorar el rendimiento de consultas históricas.
-- Campos: Ingresos totales, conteo de servicios, promedios y desglose de ganancias.
CREATE TABLE mv_resumen_mensual_ingresos (
    anio_mes VARCHAR(7) PRIMARY KEY,
    total_ingresos DECIMAL(12, 2) NOT NULL,
    total_servicios INT NOT NULL,
    promedio_por_servicio DECIMAL(10, 2) NOT NULL,
    ingresos_conductor DECIMAL(12, 2) NOT NULL,
    ingresos_empresa DECIMAL(12, 2) NOT NULL,
    ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


-- Procedimiento: ActualizarResumenMensual
-- Propósito: Recalcular y actualizar la tabla materializada 'mv_resumen_mensual_ingresos'.
-- Lógica: Itera sobre los meses con actividad, calcula los totales y actualiza la tabla destino.
-- Procedimiento para actualizar la vista materializada

DROP PROCEDURE IF EXISTS ActualizarResumenMensual;

DELIMITER //

CREATE PROCEDURE ActualizarResumenMensual()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE current_year_month VARCHAR(7);
    DECLARE cur CURSOR FOR 
        SELECT DISTINCT DATE_FORMAT(fecha_hora_reserva, '%Y-%m') 
        FROM ruta 
        WHERE fecha_hora_reserva IS NOT NULL;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- Limpiar tabla existente para regenerar datos
    DELETE FROM mv_resumen_mensual_ingresos;
    
    OPEN cur;
    
    read_loop: LOOP
        FETCH cur INTO current_year_month;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        INSERT INTO mv_resumen_mensual_ingresos (
            anio_mes, 
            total_ingresos, 
            total_servicios, 
            promedio_por_servicio,
            ingresos_conductor,
            ingresos_empresa
        )
        SELECT 
            current_year_month,
            COALESCE(SUM(total), 0),
            COUNT(id_ruta),
            COALESCE(AVG(total), 0),
            COALESCE(SUM(pago_conductor), 0),
            COALESCE(SUM(total) - SUM(pago_conductor), 0)
        FROM ruta 
        WHERE DATE_FORMAT(fecha_hora_reserva, '%Y-%m') = current_year_month
        AND id_estado_servicio IN (2, 3); -- Solo servicios en proceso y finalizados
    END LOOP;
    
    CLOSE cur;
END //

DELIMITER ;

-- Evento para actualización automática diaria
-- Evento: event_actualizar_resumen_mensual
-- Propósito: Automatizar la ejecución del procedimiento de actualización.
-- Frecuencia: Se ejecuta diariamente.
CREATE EVENT IF NOT EXISTS event_actualizar_resumen_mensual
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP
DO
    CALL ActualizarResumenMensual();

-- Vista materializada para estadísticas de conductores por ciudad
-- Tabla Materializada: mv_estadisticas_conductores_ciudad
-- Descripción: Estadísticas operativas de conductores agrupadas por ciudad.
-- Propósito: Monitorear la oferta y demanda de conductores geográficamente.
CREATE TABLE mv_estadisticas_conductores_ciudad (
    id_ciudad VARCHAR(10) PRIMARY KEY,
    ciudad VARCHAR(50) NOT NULL,
    departamento VARCHAR(50) NOT NULL,
    total_conductores INT NOT NULL,
    conductores_activos INT NOT NULL,
    conductores_conectados INT NOT NULL,
    vehiculos_activos INT NOT NULL,
    promedio_servicios_por_conductor DECIMAL(10, 2) NOT NULL,
    ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Procedimiento: ActualizarEstadisticasConductores
-- Propósito: Recalcular las estadísticas de conductores por ciudad.
-- Lógica: Itera por códigos postales, cuenta conductores totales, conectados y vehículos activos.

-- Eliminar el procedimiento si existe
DROP PROCEDURE IF EXISTS ActualizarEstadisticasConductores;

-- Cambiar delimitador temporalmente
DELIMITER //

CREATE PROCEDURE ActualizarEstadisticasConductores()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE current_codigo_postal VARCHAR(10);
    DECLARE cur CURSOR FOR 
        SELECT DISTINCT cp.id_codigo_postal 
        FROM codigo_postal cp 
        JOIN conductor c ON cp.id_codigo_postal = c.codigo_postal;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- Limpiar tabla existente
    DELETE FROM mv_estadisticas_conductores_ciudad;
    
    OPEN cur;
    
    read_loop: LOOP
        FETCH cur INTO current_codigo_postal;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        INSERT INTO mv_estadisticas_conductores_ciudad (
            id_ciudad,
            ciudad,
            departamento,
            total_conductores,
            conductores_activos,
            conductores_conectados,
            vehiculos_activos,
            promedio_servicios_por_conductor
        )
        SELECT 
            cp.id_codigo_postal,
            cp.ciudad,
            cp.departamento,
            COUNT(DISTINCT c.id_conductor) as total_conductores,
            COUNT(DISTINCT CASE WHEN ec.descripcion = 'Conectado' THEN c.id_conductor END) as conductores_activos,
            COUNT(DISTINCT CASE WHEN c.id_estado_conductor = 1 THEN c.id_conductor END) as conductores_conectados,
            COUNT(DISTINCT CASE WHEN ev.descripcion = 'Activo' THEN v.placa END) as vehiculos_activos,
            COALESCE(
                (SELECT COUNT(r.id_ruta) / COUNT(DISTINCT r.id_conductor) 
                 FROM ruta r 
                 JOIN conductor c2 ON r.id_conductor = c2.id_conductor 
                 WHERE c2.codigo_postal = current_codigo_postal
                 AND r.id_estado_servicio IN (2, 3)), 0
            ) as promedio_servicios
        FROM codigo_postal cp
        LEFT JOIN conductor c ON cp.id_codigo_postal = c.codigo_postal
        LEFT JOIN estado_conductor ec ON c.id_estado_conductor = ec.id_estado_conductor
        LEFT JOIN vehiculo v ON c.placa_vehiculo = v.placa
        LEFT JOIN estado_vehiculo ev ON v.id_estado_vehiculo = ev.id_estado_vehiculo
        WHERE cp.id_codigo_postal = current_codigo_postal
        GROUP BY cp.id_codigo_postal, cp.ciudad, cp.departamento;
    END LOOP;
    
    CLOSE cur;
END //

-- Restaurar delimitador
DELIMITER ;

-- Evento para actualización automática cada 6 horas
-- Evento: event_actualizar_estadisticas_conductores
-- Propósito: Automatizar la actualización de estadísticas de conductores.
-- Frecuencia: Se ejecuta cada 6 horas.
CREATE EVENT IF NOT EXISTS event_actualizar_estadisticas_conductores
ON SCHEDULE EVERY 6 HOUR
STARTS CURRENT_TIMESTAMP
DO
    CALL ActualizarEstadisticasConductores();



-- Ejecutar estos comandos en MySQL para poblar las tablas materializadas
CALL ActualizarResumenMensual();
CALL ActualizarEstadisticasConductores();