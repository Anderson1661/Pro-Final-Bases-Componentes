-- =================== PAISES ===================
INSERT INTO pais (nombre) VALUES ('Colombia');
INSERT INTO pais (nombre) VALUES ('Ecuador');
INSERT INTO pais (nombre) VALUES ('Perú');
INSERT INTO pais (nombre) VALUES ('Chile');
INSERT INTO pais (nombre) VALUES ('México');
INSERT INTO pais (nombre) VALUES ('Argentina');
INSERT INTO pais (nombre) VALUES ('Brasil');
INSERT INTO pais (nombre) VALUES ('Panamá');
INSERT INTO pais (nombre) VALUES ('Estados Unidos');
INSERT INTO pais (nombre) VALUES ('España');

-- =================== CODIGOS POSTALES ===================
INSERT INTO codigo_postal (id_codigo_postal, id_pais, ciudad, departamento) VALUES ('541641', 1, 'Armenia', 'Quindío');
INSERT INTO codigo_postal (id_codigo_postal, id_pais, ciudad, departamento) VALUES ('818855', 1, 'Ibagué', 'Tolima');
INSERT INTO codigo_postal (id_codigo_postal, id_pais, ciudad, departamento) VALUES ('252585', 1, 'Neiva', 'Huila');
INSERT INTO codigo_postal (id_codigo_postal, id_pais, ciudad, departamento) VALUES ('250619', 1, 'Sincelejo', 'Sucre');
INSERT INTO codigo_postal (id_codigo_postal, id_pais, ciudad, departamento) VALUES ('150571', 1, 'Cali', 'Valle del Cauca');
INSERT INTO codigo_postal (id_codigo_postal, id_pais, ciudad, departamento) VALUES ('172204', 1, 'Cartagena', 'Bolívar');
INSERT INTO codigo_postal (id_codigo_postal, id_pais, ciudad, departamento) VALUES ('954704', 1, 'Bucaramanga', 'Santander');
INSERT INTO codigo_postal (id_codigo_postal, id_pais, ciudad, departamento) VALUES ('953989', 1, 'Pasto', 'Nariño');
INSERT INTO codigo_postal (id_codigo_postal, id_pais, ciudad, departamento) VALUES ('056868', 1, 'Santa Marta', 'Magdalena');
INSERT INTO codigo_postal (id_codigo_postal, id_pais, ciudad, departamento) VALUES ('865802', 1, 'Manizales', 'Caldas');

-- =================== SUCURSALES ===================
INSERT INTO sucursal (nombre, direccion, id_codigo_postal, telefono) VALUES ('Sucursal Sincelejo', 'Calle 142 # 7-28 Este', 4, 7501354);
INSERT INTO sucursal (nombre, direccion, id_codigo_postal, telefono) VALUES ('Sucursal Bucaramanga', 'Carrera 15 # 2-6', 7, 6153478);
INSERT INTO sucursal (nombre, direccion, id_codigo_postal, telefono) VALUES ('Sucursal Cali', 'Avenida Magdalena # 18-5 Sur', 5, 3578201);

-- =================== CLIENTES ===================
INSERT INTO cliente (nombre, id_genero, id_tipo_identificacion, identificacion, direccion, correo)
VALUES ('Juan Felipe Garzon', 2, 1, '1026718628', 'Calle 139 # 10-28 Este', 'jfgarzon@gmail.com');
INSERT INTO cliente (nombre, id_genero, id_tipo_identificacion, identificacion, id_sucursal, direccion, correo)
VALUES ('María Vera', 2, 1, '1049294953', 'Diagonal 7 A # 35-28', 'mariav@gmail.com');
INSERT INTO cliente (nombre, id_genero, id_tipo_identificacion, identificacion, id_sucursal, direccion, correo)
VALUES ('Luisa Mejía', 2, 1, '1049294942', 'Carrera 84 # 151-28', 'luisamej@gmail.com');
INSERT INTO cliente (nombre, id_genero, id_tipo_identificacion, identificacion, id_sucursal, direccion, correo)
VALUES ('María Garzón', 2, 1, '1049294990', 'Carrera 29 # 92-28', 'mariag@gmail.com');
INSERT INTO cliente (nombre, id_genero, id_tipo_identificacion, identificacion, id_sucursal, direccion, correo)
VALUES ('Maritza López', 2, 1, '1049294985', 'Calle 74 # 48-28', 'maritzal@gmail.com');
INSERT INTO cliente (nombre, id_genero, id_tipo_identificacion, identificacion, id_sucursal, direccion, correo)
VALUES ('Yesid Rosero', 1, 1, '1049294916', 'Carrera 62 # 21-28', 'yesidros@gmail.com');
INSERT INTO cliente (nombre, id_genero, id_tipo_identificacion, identificacion, id_sucursal, direccion, correo)
VALUES ('Patricia Vélez', 2, 1, '1049294973', 'Transversal 80 B # 5-28', 'matvelez@gmail.com');

-- =================== MARCAS VEHICULO ===================
INSERT INTO marca_vehiculo (nombre) VALUES ('Renault');

--*******************************************************


-- =================== ADMINISTRADORES ===================
INSERT INTO administrador (nombre, apellido, id_genero, id_tipo_identificacion, identificacion, id_tipo_usuario, id_sucursal)
VALUES ('Smith', 'Méndez', 1, 2, '31322818', 1, 1);
INSERT INTO administrador (nombre, apellido, id_genero, id_tipo_identificacion, identificacion, id_tipo_usuario, id_sucursal)
VALUES ('Andrés', 'Puentes', 2, 1, '20637542', 1, 3);
INSERT INTO administrador (nombre, apellido, id_genero, id_tipo_identificacion, identificacion, id_tipo_usuario, id_sucursal)
VALUES ('Abelardo', 'Marín', 2, 1, '61914109', 1, 1);

-- =================== CONDUCTORES ===================
INSERT INTO conductor (nombre, apellido, id_genero, id_tipo_identificacion, identificacion, id_tipo_usuario, id_sucursal)
VALUES ('María', 'Ruiz', 1, 2, '63419318', 2, 1);
INSERT INTO conductor (nombre, apellido, id_genero, id_tipo_identificacion, identificacion, id_tipo_usuario, id_sucursal)
VALUES ('María', 'Londoño', 1, 1, '55575747', 2, 2);
INSERT INTO conductor (nombre, apellido, id_genero, id_tipo_identificacion, identificacion, id_tipo_usuario, id_sucursal)
VALUES ('Doris', 'Henao', 2, 1, '21992727', 2, 1);
INSERT INTO conductor (nombre, apellido, id_genero, id_tipo_identificacion, identificacion, id_tipo_usuario, id_sucursal)
VALUES ('Brayan', 'Guerrero', 2, 1, '96330466', 2, 2);
INSERT INTO conductor (nombre, apellido, id_genero, id_tipo_identificacion, identificacion, id_tipo_usuario, id_sucursal)
VALUES ('Gabriel', 'Sánchez', 1, 2, '89759560', 2, 2);

-- =================== TELEFONOS CLIENTE ===================
INSERT INTO telefono_cliente (id_cliente, telefono) VALUES (1, '57 301 086 84 24');
INSERT INTO telefono_cliente (id_cliente, telefono) VALUES (2, '+57 315 285 81 29');
INSERT INTO telefono_cliente (id_cliente, telefono) VALUES (3, '+57 316 027 05 77');
INSERT INTO telefono_cliente (id_cliente, telefono) VALUES (4, '573160285888');
INSERT INTO telefono_cliente (id_cliente, telefono) VALUES (5, '+57 606 258 18 53');
INSERT INTO telefono_cliente (id_cliente, telefono) VALUES (6, '(+57) 308 335 46 08');
INSERT INTO telefono_cliente (id_cliente, telefono) VALUES (7, '438 30 34');

-- =================== TELEFONOS CONDUCTOR ===================
INSERT INTO telefono_conductor (id_conductor, telefono) VALUES (1, '395 15 88');
INSERT INTO telefono_conductor (id_conductor, telefono) VALUES (2, '018002776049');
INSERT INTO telefono_conductor (id_conductor, telefono) VALUES (3, '57 325 638 29 09');
INSERT INTO telefono_conductor (id_conductor, telefono) VALUES (4, '57 325 910 57 56');
INSERT INTO telefono_conductor (id_conductor, telefono) VALUES (5, '+573153753260');

-- =================== TELEFONOS ADMINISTRADOR ===================
INSERT INTO telefono_administrador (id_administrador, telefono) VALUES (1, '3268428651');
INSERT INTO telefono_administrador (id_administrador, telefono) VALUES (2, '3114895060');
INSERT INTO telefono_administrador (id_administrador, telefono) VALUES (3, '+57 314 179 53 74');

-- =================== VEHICULOS (MISMA MARCA Y MODELO) ===================
INSERT INTO vehiculo (placa, modelo, id_marca, id_estado_vehiculo)
VALUES ('SSL78H', 2022, 1, 2); -- Pasajeros
INSERT INTO vehiculo (placa, modelo, id_marca, id_estado_vehiculo)
VALUES ('IKB210', 2022, 1, 1); -- Paquetería

-- =================== RUTAS ===================
INSERT INTO ruta (origen, destino, distancia, duracion, id_categoria_servicio)
VALUES ('Popayán', 'Cartagena', 92.39, '2 horas', 1);
INSERT INTO ruta (origen, destino, distancia, duracion, id_categoria_servicio)
VALUES ('Villavicencio', 'Popayán', 371.62, '3 horas', 2);
INSERT INTO ruta (origen, destino, distancia, duracion, id_categoria_servicio)
VALUES ('Pereira', 'Pasto', 128.41, '4 horas', 1);
INSERT INTO ruta (origen, destino, distancia, duracion, id_categoria_servicio)
VALUES ('Neiva', 'Popayán', 218.17, '3 horas', 1);
INSERT INTO ruta (origen, destino, distancia, duracion, id_categoria_servicio)
VALUES ('Barranquilla', 'Sincelejo', 182.46, '9 horas', 2);
INSERT INTO ruta (origen, destino, distancia, duracion, id_categoria_servicio)
VALUES ('Medellín', 'Cali', 289.26, '1 horas', 1);
INSERT INTO ruta (origen, destino, distancia, duracion, id_categoria_servicio)
VALUES ('Cúcuta', 'Cartagena', 219.88, '3 horas', 2);
