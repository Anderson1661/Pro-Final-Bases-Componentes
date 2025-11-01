<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['placa'])) {
    send_response(400, ['error' => 'La placa del vehículo es requerida.']);
    exit;
}

$placa = $data['placa'];
$updates = [];
$params = [];
$param_count = 1;

if (isset($data['modelo'])) { $updates[] = "modelo = $" . $param_count++; $params[] = (int)$data['modelo']; }
if (isset($data['id_marca'])) { $updates[] = "id_marca = $" . $param_count++; $params[] = (int)$data['id_marca']; }
if (isset($data['id_tipo_servicio'])) { $updates[] = "id_tipo_servicio = $" . $param_count++; $params[] = (int)$data['id_tipo_servicio']; }
if (isset($data['id_estado_vehiculo'])) { $updates[] = "id_estado_vehiculo = $" . $param_count++; $params[] = (int)$data['id_estado_vehiculo']; }
if (isset($data['id_sucursal'])) { $updates[] = "id_sucursal = $" . $param_count++; $params[] = (int)$data['id_sucursal']; }

if (empty($updates)) {
    send_response(400, ['error' => 'No hay campos para actualizar.']);
    exit;
}

$params[] = $placa;
$query = "UPDATE vehiculo SET " . implode(', ', $updates) . " WHERE placa = $" . $param_count;

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Vehículo actualizado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el vehículo o no se realizaron cambios.']);
    }
} else {
    send_response(500, ['error' => 'Error al actualizar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>