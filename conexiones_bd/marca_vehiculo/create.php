<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['nombre_marca'])) {
    send_response(400, ['error' => 'El nombre de la marca es requerido.']);
    exit;
}

$nombre_marca = $data['nombre_marca'];

$query = "INSERT INTO marca_vehiculo (nombre_marca) VALUES ($1)";
$params = [$nombre_marca];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    send_response(201, ['success' => 'Marca de vehículo creada exitosamente.']);
} else {
    send_response(500, ['error' => 'Error al crear la marca de vehículo: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>