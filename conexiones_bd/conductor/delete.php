<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_conductor'])) {
    send_response(400, ['error' => 'El ID del conductor es requerido.']);
    exit;
}

$id = (int)$data['id_conductor'];

$query = "DELETE FROM conductor WHERE id_conductor = $1";
$result = pg_query_params($conn, $query, [$id]);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Conductor eliminado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el conductor.']);
    }
} else {
    send_response(500, ['error' => 'Error al eliminar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>