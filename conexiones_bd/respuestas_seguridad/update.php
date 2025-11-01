<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_pregunta']) || !isset($data['id_usuario']) || !isset($data['respuesta_pregunta'])) {
    send_response(400, ['error' => 'ID de pregunta, ID de usuario y la nueva respuesta son requeridos.']);
    exit;
}

$id_pregunta = (int)$data['id_pregunta'];
$id_usuario = (int)$data['id_usuario'];
$respuesta_pregunta = $data['respuesta_pregunta'];

$query = "UPDATE respuestas_seguridad SET respuesta_pregunta = $1 WHERE id_pregunta = $2 AND id_usuario = $3";
$params = [$respuesta_pregunta, $id_pregunta, $id_usuario];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Respuesta de seguridad actualizada.']);
    } else {
        send_response(404, ['message' => 'No se encontró la respuesta de seguridad o no se realizaron cambios.']);
    }
} else {
    send_response(500, ['error' => 'Error al actualizar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>