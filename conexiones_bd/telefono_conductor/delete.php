<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_conductor']) || !isset($data['telefono'])) {
    send_response(400, ['error' => 'ID de conductor y teléfono son requeridos para eliminar.']);
    exit;
}

$id_conductor = (int)$data['id_conductor'];
$telefono = $data['telefono'];

$query = "DELETE FROM telefono_conductor WHERE id_conductor = $1 AND telefono = $2";
$params = [$id_conductor, $telefono];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Teléfono de conductor eliminado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el registro de teléfono.']);
    }
} else {
    send_response(500, ['error' => 'Error al eliminar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>