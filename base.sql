/*
tablas

usuarios
tipos de ususrio (admin, cliente, condu)

cliente
adminsitrador
conductor 
	tipo documento
	telefonos
	generos
	nacionalidades
vehiculo
tipo de servicio (pasajero, alimento, ambos)
servicios
ruta (origen y destino) (distancia)
categoria servicio (normal, especial o urgente) (valor por km)
estado servicio (pendiente, en proceso, finalizado)
metodos de pago

preguntas seguridad
respuestas segurirad (id usuario, id pregunta, respuesta hash)
*/

CREATE DATABASE transportadora;



CREATE TABLE usuarios ();

CREATE TABLE tipo_usuario (
	id_tipo_usuario int primary key,
	descripcion varchar (30)
);

insert into tipo_usuario values (1,Administrador);
insert into tipo_usuario values (2,Conductor);
insert into tipo_usuario values (3,Cliente);

CREATE TABLE cliente (
	id_cliente int primary key,
	identificacion varchar(20),
	id_tipo_identificacion int,
	nombre varchar(100),
	direccion varchar(100),
	id_genero int,
	codigo_postal varchar(10)
);

CREATE TABLE administrador (

);

CREATE TABLE conductor (

);

CREATE TABLE tipo_documento (
	id_tipo_identificacion int primary key,
	descripcion varchar(30)
);

CREATE TABLE telefono_cliente (
	id_cliente int,
	telefono bigint,
	primary key (id_cliente, telefono)
);

CREATE TABLE generos (
	id_genero int primary key,
	descripcion varchar(30)
);
CREATE TABLE codigo_postal (
	codigo varchar(10) primary key,
	ciudad VARCHAR(100) NOT NULL,
    provincia VARCHAR(100),
    region VARCHAR(100),
    id_pais INT NOT NULL
);

CREATE TABLE pais (
    id_pais INT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

CREATE TABLE vehiculo ();
CREATE TABLE tipo_servicio ();
CREATE TABLE servicios ();
CREATE TABLE ruta ();
CREATE TABLE categoria_servicio ();
CREATE TABLE estado_servicio ();
CREATE TABLE metodos_pago ();

CREATE TABLE preguntas_seguridad ();
CREATE TABLE respuestas_seguridad ();

















/*
trigger cada que se inserta usuario se inserta en tabal usuarios
trigger para calcular precio total

vistas para reportes
-precio y cantidad agrupados por tipo de servicio
-cantidad personas y servicios por mes
-datos del cliente con servicios solicitados en x periodo ordenado segun cantidad total  servicios
-valor total de servicios con su medio de pago
*/