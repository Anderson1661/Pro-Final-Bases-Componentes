<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['identificacion']) || !isset($data['id_tipo_identificacion']) || !isset($data['nombre']) || !isset($data['direccion']) || !isset($data['correo']) || !isset($data['id_genero']) || !isset($data['id_pais_nacionalidad']) || !isset($data['codigo_postal'])) {
    send_response(400, ['error' => 'Todos los campos son requeridos.']);
    exit;
}

$identificacion = $data['identificacion'];
$id_tipo_identificacion = (int)$data['id_tipo_identificacion'];
$nombre = $data['nombre'];
$direccion = $data['direccion'];
$correo = $data['correo'];
$id_genero = (int)$data['id_genero'];
$id_pais_nacionalidad = (int)$data['id_pais_nacionalidad'];
$codigo_postal = $data['codigo_postal'];

$query = "INSERT INTO cliente (identificacion, id_tipo_identificacion, nombre, direccion, correo, id_genero, id_pais_nacionalidad, codigo_postal) VALUES ($1, $2, $3, $4, $5, $6, $7, $8)";
$params = [$identificacion, $id_tipo_identificacion, $nombre, $direccion, $correo, $id_genero, $id_pais_nacionalidad, $codigo_postal];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    send_response(201, ['success' => 'Cliente creado exitosamente.']);
} else {
    send_response(500, ['error' => 'Error al crear el cliente: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>