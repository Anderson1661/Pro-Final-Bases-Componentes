<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['placa']) || !isset($data['modelo']) || !isset($data['id_marca']) || !isset($data['id_tipo_servicio']) || !isset($data['id_estado_vehiculo']) || !isset($data['id_sucursal'])) {
    send_response(400, ['error' => 'Todos los campos son requeridos.']);
    exit;
}

$placa = $data['placa'];
$modelo = (int)$data['modelo'];
$id_marca = (int)$data['id_marca'];
$id_tipo_servicio = (int)$data['id_tipo_servicio'];
$id_estado_vehiculo = (int)$data['id_estado_vehiculo'];
$id_sucursal = (int)$data['id_sucursal'];

$query = "INSERT INTO vehiculo (placa, modelo, id_marca, id_tipo_servicio, id_estado_vehiculo, id_sucursal) VALUES ($1, $2, $3, $4, $5, $6)";
$params = [$placa, $modelo, $id_marca, $id_tipo_servicio, $id_estado_vehiculo, $id_sucursal];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    send_response(201, ['success' => 'Vehículo creado exitosamente.']);
} else {
    send_response(500, ['error' => 'Error al crear el vehículo: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>