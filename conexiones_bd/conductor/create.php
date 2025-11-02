<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_conductor']) || !isset($data['placa_vehiculo']) || !isset($data['identificacion']) || !isset($data['id_tipo_identificacion']) || !isset($data['nombre']) || !isset($data['direccion']) || !isset($data['correo']) || !isset($data['id_genero']) || !isset($data['codigo_postal']) || !isset($data['id_pais_nacionalidad']) || !isset($data['url_foto']) || !isset($data['id_sucursal'])) {
    send_response(400, ['error' => 'Todos los campos son requeridos.']);
    exit;
}

$id_conductor = (int)$data['id_conductor'];
$placa_vehiculo = (int)$data['placa_vehiculo'];
$identificacion = $data['identificacion'];
$id_tipo_identificacion = (int)$data['id_tipo_identificacion'];
$nombre = $data['nombre'];
$direccion = $data['direccion'];
$correo = $data['correo'];
$id_genero = (int)$data['id_genero'];
$codigo_postal = $data['codigo_postal'];
$id_pais_nacionalidad = (int)$data['id_pais_nacionalidad'];
$url_foto = $data['url_foto'];
$id_sucursal = (int)$data['id_sucursal'];

$query = "INSERT INTO conductor (id_conductor, placa_vehiculo, identificacion, id_tipo_identificacion, nombre, direccion, correo, id_genero, codigo_postal, id_pais_nacionalidad, url_foto, id_sucursal) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11)";
$params = [$id_conductor, $identificacion, $id_tipo_identificacion, $nombre, $direccion, $correo, $id_genero, $codigo_postal, $url_foto, $id_sucursal];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    send_response(201, ['success' => 'Conductor creado exitosamente.']);
} else {
    send_response(500, ['error' => 'Error al crear el conductor: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>