<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_categoria_servicio'])) {
    send_response(400, ['error' => 'El ID de la categoría es requerido.']);
    exit;
}

$id = (int)$data['id_categoria_servicio'];
$updates = [];
$params = [];
$param_count = 1;

if (isset($data['descripcion'])) { $updates[] = "descripcion = $" . $param_count++; $params[] = $data['descripcion']; }
if (isset($data['valor_km'])) { $updates[] = "valor_km = $" . $param_count++; $params[] = $data['valor_km']; }

if (empty($updates)) {
    send_response(400, ['error' => 'No hay campos para actualizar.']);
    exit;
}

$params[] = $id;
$query = "UPDATE categoria_servicio SET " . implode(', ', $updates) . " WHERE id_categoria_servicio = $" . $param_count;

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Categoría de servicio actualizada.']);
    } else {
        send_response(404, ['message' => 'No se encontró la categoría o no se realizaron cambios.']);
    }
} else {
    send_response(500, ['error' => 'Error al actualizar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>