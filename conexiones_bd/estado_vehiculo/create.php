<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['descripcion'])) {
    send_response(400, ['error' => 'La descripción es requerida.']);
    exit;
}

$descripcion = $data['descripcion'];

$query = "INSERT INTO estado_vehiculo (descripcion) VALUES ($1)";
$params = [$descripcion];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    send_response(201, ['success' => 'Estado de vehículo creado exitosamente.']);
} else {
    send_response(500, ['error' => 'Error al crear el estado de vehículo: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>