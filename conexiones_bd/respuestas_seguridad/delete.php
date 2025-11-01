<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_pregunta']) || !isset($data['id_usuario'])) {
    send_response(400, ['error' => 'ID de pregunta y ID de usuario son requeridos para eliminar.']);
    exit;
}

$id_pregunta = (int)$data['id_pregunta'];
$id_usuario = (int)$data['id_usuario'];

$query = "DELETE FROM respuestas_seguridad WHERE id_pregunta = $1 AND id_usuario = $2";
$params = [$id_pregunta, $id_usuario];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Respuesta de seguridad eliminada.']);
    } else {
        send_response(404, ['message' => 'No se encontró el registro.']);
    }
} else {
    send_response(500, ['error' => 'Error al eliminar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>