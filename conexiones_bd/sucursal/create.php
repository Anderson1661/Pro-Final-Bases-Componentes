<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['codigo_postal']) || !isset($data['direccion']) || !isset($data['telefono']) || !isset($data['nombre'])) {
    send_response(400, ['error' => 'Todos los campos son requeridos: codigo_postal, direccion, telefono, nombre.']);
    exit;
}

$codigo_postal = $data['codigo_postal'];
$direccion = $data['direccion'];
$telefono = $data['telefono'];
$nombre = $data['nombre'];

$query = "INSERT INTO sucursal (codigo_postal, direccion, telefono, nombre) VALUES ($1, $2, $3, $4)";
$params = [$codigo_postal, $direccion, $telefono, $nombre];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    send_response(201, ['success' => 'Sucursal creada exitosamente.']);
} else {
    send_response(500, ['error' => 'Error al crear la sucursal: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>