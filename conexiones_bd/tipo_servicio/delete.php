<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_tipo_servicio'])) {
    send_response(400, ['error' => 'El ID del tipo de servicio es requerido.']);
    exit;
}

$id = (int)$data['id_tipo_servicio'];

$query = "DELETE FROM tipo_servicio WHERE id_tipo_servicio = $1";
$result = pg_query_params($conn, $query, [$id]);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Tipo de servicio eliminado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el tipo de servicio.']);
    }
} else {
    send_response(500, ['error' => 'Error al eliminar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>