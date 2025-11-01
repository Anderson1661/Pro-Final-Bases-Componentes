<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_tipo_identificacion']) || !isset($data['descripcion'])) {
    send_response(400, ['error' => 'El ID y la descripción son requeridos.']);
    exit;
}

$id = (int)$data['id_tipo_identificacion'];
$descripcion = $data['descripcion'];

$query = "UPDATE tipo_identificacion SET descripcion = $1 WHERE id_tipo_identificacion = $2";
$params = [$descripcion, $id];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Tipo de identificación actualizado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el tipo de identificación o no se realizaron cambios.']);
    }
} else {
    send_response(500, ['error' => 'Error al actualizar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>