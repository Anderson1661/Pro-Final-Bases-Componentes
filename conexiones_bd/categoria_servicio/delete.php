<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_categoria_servicio'])) {
    send_response(400, ['error' => 'El ID de la categoría es requerido.']);
    exit;
}

$id = (int)$data['id_categoria_servicio'];

$query = "DELETE FROM categoria_servicio WHERE id_categoria_servicio = $1";
$result = pg_query_params($conn, $query, [$id]);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Categoría de servicio eliminada.']);
    } else {
        send_response(404, ['message' => 'No se encontró la categoría de servicio.']);
    }
} else {
    send_response(500, ['error' => 'Error al eliminar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>