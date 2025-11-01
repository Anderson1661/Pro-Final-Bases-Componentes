<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_cliente']) || !isset($data['telefono'])) {
    send_response(400, ['error' => 'ID de cliente y teléfono son requeridos para eliminar.']);
    exit;
}

$id_cliente = (int)$data['id_cliente'];
$telefono = $data['telefono'];

$query = "DELETE FROM telefono_cliente WHERE id_cliente = $1 AND telefono = $2";
$params = [$id_cliente, $telefono];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Teléfono de cliente eliminado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el registro de teléfono.']);
    }
} else {
    send_response(500, ['error' => 'Error al eliminar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>