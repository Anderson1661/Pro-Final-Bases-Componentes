<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_sucursal'])) {
    send_response(400, ['error' => 'El ID de la sucursal es requerido.']);
    exit;
}

$id = (int)$data['id_sucursal'];
$updates = [];
$params = [];
$param_count = 1;

if (isset($data['codigo_postal'])) {
    $updates[] = "codigo_postal = $" . $param_count++;
    $params[] = $data['codigo_postal'];
}
if (isset($data['direccion'])) {
    $updates[] = "direccion = $" . $param_count++;
    $params[] = $data['direccion'];
}
if (isset($data['telefono'])) {
    $updates[] = "telefono = $" . $param_count++;
    $params[] = $data['telefono'];
}
if (isset($data['nombre'])) {
    $updates[] = "nombre = $" . $param_count++;
    $params[] = $data['nombre'];
}

if (empty($updates)) {
    send_response(400, ['error' => 'No hay campos para actualizar.']);
    exit;
}

$params[] = $id;
$query = "UPDATE sucursal SET " . implode(', ', $updates) . " WHERE id_sucursal = $" . $param_count;

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Sucursal actualizada.']);
    } else {
        send_response(404, ['message' => 'No se encontró la sucursal o no se realizaron cambios.']);
    }
} else {
    send_response(500, ['error' => 'Error al actualizar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>