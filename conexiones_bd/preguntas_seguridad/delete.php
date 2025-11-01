<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_pregunta'])) {
    send_response(400, ['error' => 'El ID de la pregunta es requerido.']);
    exit;
}

$id = (int)$data['id_pregunta'];

$query = "DELETE FROM preguntas_seguridad WHERE id_pregunta = $1";
$result = pg_query_params($conn, $query, [$id]);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Pregunta de seguridad eliminada.']);
    } else {
        send_response(404, ['message' => 'No se encontró la pregunta de seguridad.']);
    }
} else {
    send_response(500, ['error' => 'Error al eliminar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>