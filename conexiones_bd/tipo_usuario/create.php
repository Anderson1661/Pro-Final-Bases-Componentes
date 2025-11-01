<?php
require_once '../conexion.php';

// Leer los datos JSON del cuerpo de la solicitud
$data = json_decode(file_get_contents('php://input'), true);

// Validar que los datos necesarios están presentes
if (!isset($data['descripcion'])) {
    send_response(400, ['error' => 'La descripción es requerida.']);
    exit;
}

$descripcion = $data['descripcion'];

// Preparar la consulta SQL para evitar inyección SQL
$query = "INSERT INTO tipo_usuario (descripcion) VALUES ($1)";
$params = [$descripcion];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    send_response(201, ['success' => 'Tipo de usuario creado exitosamente.']);
} else {
    send_response(500, ['error' => 'Error al crear el tipo de usuario: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>