<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_estado_vehiculo']) || !isset($data['descripcion'])) {
    send_response(400, ['error' => 'El ID y la descripción son requeridos.']);
    exit;
}

$id = (int)$data['id_estado_vehiculo'];
$descripcion = $data['descripcion'];

$query = "UPDATE estado_vehiculo SET descripcion = $1 WHERE id_estado_vehiculo = $2";
$params = [$descripcion, $id];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Estado de vehículo actualizado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el estado de vehículo o no se realizaron cambios.']);
    }
} else {
    send_response(500, ['error' => 'Error al actualizar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>