<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['descripcion']) || !isset($data['valor_km'])) {
    send_response(400, ['error' => 'La descripción y el valor por km son requeridos.']);
    exit;
}

$descripcion = $data['descripcion'];
$valor_km = $data['valor_km'];

$query = "INSERT INTO categoria_servicio (descripcion, valor_km) VALUES ($1, $2)";
$params = [$descripcion, $valor_km];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    send_response(201, ['success' => 'Categoría de servicio creada exitosamente.']);
} else {
    send_response(500, ['error' => 'Error al crear la categoría de servicio: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>