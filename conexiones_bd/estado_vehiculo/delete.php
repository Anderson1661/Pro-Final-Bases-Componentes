<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_estado_vehiculo'])) {
    send_response(400, ['error' => 'El ID del estado de vehículo es requerido.']);
    exit;
}

$id = (int)$data['id_estado_vehiculo'];

$query = "DELETE FROM estado_vehiculo WHERE id_estado_vehiculo = $1";
$result = pg_query_params($conn, $query, [$id]);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Estado de vehículo eliminado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el estado de vehículo.']);
    }
} else {
    send_response(500, ['error' => 'Error al eliminar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>