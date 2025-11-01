<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_codigo_postal'])) {
    send_response(400, ['error' => 'El ID del código postal es requerido.']);
    exit;
}

$id = $data['id_codigo_postal'];

$query = "DELETE FROM codigo_postal WHERE id_codigo_postal = $1";
$result = pg_query_params($conn, $query, [$id]);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Código postal eliminado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el código postal.']);
    }
} else {
    send_response(500, ['error' => 'Error al eliminar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>