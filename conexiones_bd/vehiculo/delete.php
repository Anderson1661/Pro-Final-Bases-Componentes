<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['placa'])) {
    send_response(400, ['error' => 'La placa del vehículo es requerida.']);
    exit;
}

$placa = $data['placa'];

$query = "DELETE FROM vehiculo WHERE placa = $1";
$result = pg_query_params($conn, $query, [$placa]);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Vehículo eliminado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el vehículo.']);
    }
} else {
    send_response(500, ['error' => 'Error al eliminar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>