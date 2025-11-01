<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['nombre'])) {
    send_response(400, ['error' => 'El nombre del país es requerido.']);
    exit;
}

$nombre = $data['nombre'];

$query = "INSERT INTO pais (nombre) VALUES ($1)";
$params = [$nombre];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    send_response(201, ['success' => 'País creado exitosamente.']);
} else {
    send_response(500, ['error' => 'Error al crear el país: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>