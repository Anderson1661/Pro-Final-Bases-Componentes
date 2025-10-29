
CREATE DATABASE transportadora;



CREATE TABLE usuario (
	id_usuario 			SERIAL primary key,
	id_tipo_usuario 	int,
	correo 				varchar(100),
	contrasenia 		varchar(150)
);

CREATE TABLE tipo_usuario (
	id_tipo_usuario 	SERIAL primary key,
	descripcion 		varchar (30)
);

insert into tipo_usuario (descripcion) values 
('Administrador'),
('Conductor'),
('Cliente');

CREATE TABLE tipo_identificacion (
	id_tipo_identificacion SERIAL primary key,
	descripcion 		varchar(30)
);

INSERT INTO tipo_identificacion (descripcion) VALUES 
('Cédula de Ciudadanía'),
('Cédula de Extranjería'),
('NIT'),
('Pasaporte');

CREATE TABLE genero (
	id_genero			SERIAL primary key,
	descripcion			varchar(30)
);

insert into genero (descripcion) values
('Masculino'),
('Femenino'),
('Otro');

CREATE TABLE pais (
    id_pais SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

CREATE TABLE codigo_postal (
	codigo 			  varchar(10) primary key,
	id_pais           VARCHAR(50) NOT NULL,
	departamento      VARCHAR(50) NOT NULL,
	ciudad            VARCHAR(50) NOT NULL
);

CREATE TABLE sucursal (
	id_sucursal serial primary key,
	id_codigo_postal varchar(10),
	direccion varchar(50),
	telefono bigint,
	nombre varchar(100)
);

CREATE TABLE cliente (
	id_cliente 			int primary key,
	identificacion 		varchar(20),
	id_tipo_identificacion int,
	nombre 				varchar(100),
	direccion 			varchar(100),
	correo 				unique varchar(100),
	id_genero 			int,
	codigo_postal 		varchar(10)
);

CREATE TABLE telefono_cliente (
	id_cliente 			int,
	telefono 			bigint,
	primary key (id_cliente, telefono)
);

CREATE TABLE administrador (
	id_administrador 	int primary key,
	identificacion 		varchar(20),
	id_tipo_identificacion int,
	nombre 				varchar(100),
	direccion 			varchar(100),
	correo 				unique varchar(100),
	id_genero 			int,
	codigo_postal 		varchar(10)
);

CREATE TABLE telefono_administrador (
	id_administrador 	int,
	telefono 			bigint,
	primary key (id_administrador, telefono)
);

CREATE TABLE conductor (
	id_conductor		int primary key,
	identificacion 		varchar(20),
	id_tipo_identificacion int,
	nombre 				varchar(100),
	direccion 			varchar(100),
	correo 				unique varchar(100),
	id_genero 			int,
	codigo_postal 		varchar(10),
	url_foto			varchar(255),
	id_sucursal			int
);

CREATE TABLE telefono_conductor (
	id_conductor 		int,
	telefono 			bigint,
	primary key (id_conductor, telefono)
);

CREATE TABLE estado_vehiculo (
  id_estado				SERIAL PRIMARY KEY,
  descripcion       	VARCHAR(100) NOT NULL
);

INSERT INTO estado_vehiculo (descripcion) VALUES 
('Activo'),
('Mantenimiento'),
('Asignado');

CREATE TABLE marca_vehiculo (
  id_marca          	SERIAL PRIMARY KEY,
  nombre_marca      	VARCHAR(50) NOT NULL
);

CREATE TABLE tipo_servicio (
	id_tipo_servicio SERIAL PRIMARY KEY,
	descripcion VARCHAR(30)
);

INSERT INTO tipo_servicio (descripcion) VALUES 
('Pasajeros'),
('Alimentos'),
('Pasajeros y alimentos');

CREATE TABLE vehiculo (
	placa              VARCHAR(20) PRIMARY KEY,
	modelo             int,
	id_marca           INT,
	id_tipo_servicio   INT,
	id_estado_vehiculo INT,
	id_sucursal		   int
);

alter table vehiculo add constraint check_modelo check (modelo>=2010);

CREATE TABLE metodo_pago (
	id_metodo_pago serial primary key,
	descripcion VARCHAR(30)
);

INSERT INTO metodo_pago (descripcion) VALUES 
('Efectivo'),
('Tarjeta Debito'),
('Tarjeta Credito'),
('Transferencia');

CREATE TABLE categoria_servicio (
	id_categoria_servicio serial primary key,
	descripcion VARCHAR(30),
	valor_km NUMERIC(12, 2)
);

INSERT INTO categoria_servicio (descripcion, valor_km) VALUES 
('Normal',1000),
('Especial',1500),
('Urgente',2000);

CREATE TABLE estado_servicio (
	id_estado_servicio serial primary key,
	descripcion VARCHAR(30)
);

insert into estado_servicio (descripcion) VALUES
('Pendiente'),
('En proceso'),
('Finalizado');


CREATE TABLE ruta (
	id_ruta serial primary key,
	direccion_origen varchar(50),
	direccion_destino varchar(50),
	id_codigo_postal_origen varchar (10),
	id_codigo_postal_destino varchar (10),
	distancia_km NUMERIC(8, 2),
	fecha_hora_origen timestamp,
	fecha_hora_destino timestamp,
	id_conductor int,
	id_tipo_servicio int,
	id_cliente int,
	id_estado_servicio int,
	placa_vehiculo varchar(20),
	id_categoria_servicio int,
	id_metodo_pago int,
	total NUMERIC(12, 2)
);

alter table ruta add constraint check_distancia check (distancia_km>0);

create table pasajero_ruta(
	id_ruta int,
	nombre_pasajero varchar(100),
	primary key (id_ruta, nombre_pasajero)
);

CREATE TABLE preguntas_seguridad (
	id_pregunta serial primary key,
	descripcion varchar(100)
);

INSERT INTO preguntas_seguridad (descripcion) VALUES
('¿Cuál es el nombre de tu primera mascota?'),
('¿En qué ciudad naciste?'),
('¿Cuál es el segundo apellido de tu madre?'),
('¿Cuál fue el nombre de tu primer colegio?'),
('¿Cuál es el nombre de tu mejor amigo de la infancia?');

CREATE TABLE respuestas_seguridad (
	id_pregunta int,
	id_usuario int,
	respuesta_pregunta varchar(255),
	primary key(id_pregunta, id_usuario)
);








-- ---------- TRIGGER: insertar usuario automático ----------
CREATE OR REPLACE FUNCTION insertar_usuario()
RETURNS TRIGGER AS $$
DECLARE
    tipo INT;
BEGIN
    -- No insertar si no hay correo
    IF NEW.correo IS NULL THEN
        RETURN NEW;
    END IF;

    -- Obtener id_tipo_usuario según la tabla origen
    IF TG_TABLE_NAME = 'cliente' THEN
        SELECT id_tipo_usuario INTO tipo FROM tipo_usuario WHERE descripcion = 'Cliente' LIMIT 1;
    ELSIF TG_TABLE_NAME = 'conductor' THEN
        SELECT id_tipo_usuario INTO tipo FROM tipo_usuario WHERE descripcion = 'Conductor' LIMIT 1;
    ELSIF TG_TABLE_NAME = 'administrador' THEN
        SELECT id_tipo_usuario INTO tipo FROM tipo_usuario WHERE descripcion = 'Administrador' LIMIT 1;
    END IF;

    -- Si no encontramos el tipo, abortamos (error de configuración)
    IF tipo IS NULL THEN
        RAISE EXCEPTION 'Tipo de usuario no definido para la tabla %', TG_TABLE_NAME;
    END IF;

    -- Insertar solo si no existe usuario con ese correo
    IF NOT EXISTS (SELECT 1 FROM usuario u WHERE u.correo = NEW.correo) THEN
        INSERT INTO usuario (id_tipo_usuario, correo, contrasenia)
        VALUES (tipo, NEW.correo, 'temporal123');
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers asociados
DROP TRIGGER IF EXISTS trg_insert_usuario_cliente ON cliente;
CREATE TRIGGER trg_insert_usuario_cliente
AFTER INSERT ON cliente
FOR EACH ROW
EXECUTE FUNCTION insertar_usuario();

DROP TRIGGER IF EXISTS trg_insert_usuario_conductor ON conductor;
CREATE TRIGGER trg_insert_usuario_conductor
AFTER INSERT ON conductor
FOR EACH ROW
EXECUTE FUNCTION insertar_usuario();

DROP TRIGGER IF EXISTS trg_insert_usuario_admin ON administrador;
CREATE TRIGGER trg_insert_usuario_admin
AFTER INSERT ON administrador
FOR EACH ROW
EXECUTE FUNCTION insertar_usuario();


-- ---------- TRIGGER: calcular total de ruta ----------
CREATE OR REPLACE FUNCTION calcular_total_ruta()
RETURNS TRIGGER AS $$
DECLARE
    precio_km NUMERIC(12,2);
BEGIN
    -- Obtener valor por km desde la categoría
    SELECT valor_km INTO precio_km
    FROM categoria_servicio
    WHERE id_categoria_servicio = NEW.id_categoria_servicio
    LIMIT 1;

    IF precio_km IS NULL THEN
        RAISE EXCEPTION 'Categoría de servicio inválida (id_categoria_servicio = %)', NEW.id_categoria_servicio;
    END IF;

    -- Asegurar distancia válida
    IF NEW.distancia_km IS NULL OR NEW.distancia_km <= 0 THEN
        RAISE EXCEPTION 'Distancia inválida: %', NEW.distancia_km;
    END IF;

    -- Calcular total
    NEW.total := NEW.distancia_km * precio_km;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_calcular_total_ruta ON ruta;
CREATE TRIGGER trg_calcular_total_ruta
BEFORE INSERT OR UPDATE ON ruta
FOR EACH ROW
EXECUTE FUNCTION calcular_total_ruta();


-- ---------- VISTAS PARA REPORTES ----------

-- 1) Precio total y cantidad agrupados por tipo de servicio
CREATE OR REPLACE VIEW vw_total_cantidad_por_tipo_servicio AS
SELECT 
    ts.descripcion AS tipo_servicio,
    COUNT(r.id_ruta) AS cantidad_servicios,
    COALESCE(SUM(r.total), 0) AS valor_total
FROM ruta r
JOIN tipo_servicio ts ON r.id_tipo_servicio = ts.id_tipo_servicio
GROUP BY ts.descripcion
ORDER BY ts.descripcion;


-- 2) Cantidad de servicios y personas por mes (usa fecha_hora_origen)
CREATE OR REPLACE VIEW vw_cantidad_servicios_por_mes AS
SELECT 
    TO_CHAR(r.fecha_hora_origen, 'YYYY-MM') AS periodo,
    COUNT(r.id_ruta) AS cantidad_servicios,
    COUNT(DISTINCT r.id_cliente) AS cantidad_clientes
FROM ruta r
GROUP BY TO_CHAR(r.fecha_hora_origen, 'YYYY-MM')
ORDER BY periodo;


-- 3) Clientes con servicios en un período (ordenados por cantidad)
-- Nota: la vista devuelve todo el histórico; filtra en la consulta con WHERE sobre fecha_hora_origen para periodos concretos.
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


-- 4) Valor total de servicios agrupado por medio de pago
CREATE OR REPLACE VIEW vw_total_por_metodo_pago AS
SELECT 
    mp.descripcion AS metodo_pago,
    COUNT(r.id_ruta) AS cantidad_servicios,
    COALESCE(SUM(r.total), 0) AS valor_total
FROM ruta r
JOIN metodo_pago mp ON r.id_metodo_pago = mp.id_metodo_pago
GROUP BY mp.descripcion
ORDER BY valor_total DESC;