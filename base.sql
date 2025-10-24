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




















/*
trigger cada que se inserta usuario se inserta en tabal usuarios
trigger para calcular precio total

vistas para reportes
-precio y cantidad agrupados por tipo de servicio
-cantidad personas y servicios por mes
-datos del cliente con servicios solicitados en x periodo ordenado segun cantidad total  servicios
-valor total de servicios con su medio de pago
*/