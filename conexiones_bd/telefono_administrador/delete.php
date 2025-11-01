<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_administrador']) || !isset($data['telefono'])) {
    send_response(400, ['error' => 'ID de administrador y teléfono son requeridos para eliminar.']);
    exit;
}

$id_administrador = (int)$data['id_administrador'];
$telefono = $data['telefono'];

$query = "DELETE FROM telefono_administrador WHERE id_administrador = $1 AND telefono = $2";
$params = [$id_administrador, $telefono];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Teléfono de administrador eliminado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el registro de teléfono.']);
    }
} else {
    send_response(500, ['error' => 'Error al eliminar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>