<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_pregunta']) || !isset($data['id_usuario']) || !isset($data['respuesta_pregunta'])) {
    send_response(400, ['error' => 'ID de pregunta, ID de usuario y respuesta son requeridos.']);
    exit;
}

$id_pregunta = (int)$data['id_pregunta'];
$id_usuario = (int)$data['id_usuario'];
$respuesta_pregunta = $data['respuesta_pregunta'];

$query = "INSERT INTO respuestas_seguridad (id_pregunta, id_usuario, respuesta_pregunta) VALUES ($1, $2, $3)";
$params = [$id_pregunta, $id_usuario, $respuesta_pregunta];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    send_response(201, ['success' => 'Respuesta de seguridad creada exitosamente.']);
} else {
    send_response(500, ['error' => 'Error al crear la respuesta de seguridad: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>