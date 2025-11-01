<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['descripcion'])) {
    send_response(400, ['error' => 'La descripción es requerida.']);
    exit;
}

$descripcion = $data['descripcion'];

$query = "INSERT INTO preguntas_seguridad (descripcion) VALUES ($1)";
$params = [$descripcion];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    send_response(201, ['success' => 'Pregunta de seguridad creada exitosamente.']);
} else {
    send_response(500, ['error' => 'Error al crear la pregunta de seguridad: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>