<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_tipo_servicio']) || !isset($data['descripcion'])) {
    send_response(400, ['error' => 'El ID y la descripción son requeridos.']);
    exit;
}

$id = (int)$data['id_tipo_servicio'];
$descripcion = $data['descripcion'];

$query = "UPDATE tipo_servicio SET descripcion = $1 WHERE id_tipo_servicio = $2";
$params = [$descripcion, $id];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Tipo de servicio actualizado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el tipo de servicio o no se realizaron cambios.']);
    }
} else {
    send_response(500, ['error' => 'Error al actualizar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>