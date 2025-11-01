<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_pais'])) {
    send_response(400, ['error' => 'El ID del país es requerido.']);
    exit;
}

$id = (int)$data['id_pais'];

$query = "DELETE FROM pais WHERE id_pais = $1";
$result = pg_query_params($conn, $query, [$id]);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'País eliminado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el país.']);
    }
} else {
    send_response(500, ['error' => 'Error al eliminar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>