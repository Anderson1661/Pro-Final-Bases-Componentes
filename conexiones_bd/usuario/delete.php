<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_usuario'])) {
    send_response(400, ['error' => 'El ID del usuario es requerido.']);
    exit;
}

$id = (int)$data['id_usuario'];

$query = "DELETE FROM usuario WHERE id_usuario = $1";
$result = pg_query_params($conn, $query, [$id]);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Usuario eliminado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el usuario.']);
    }
} else {
    send_response(500, ['error' => 'Error al eliminar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>