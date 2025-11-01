<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_pais']) || !isset($data['nombre'])) {
    send_response(400, ['error' => 'El ID y el nombre del país son requeridos.']);
    exit;
}

$id = (int)$data['id_pais'];
$nombre = $data['nombre'];

$query = "UPDATE pais SET nombre = $1 WHERE id_pais = $2";
$params = [$nombre, $id];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'País actualizado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el país o no se realizaron cambios.']);
    }
} else {
    send_response(500, ['error' => 'Error al actualizar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>