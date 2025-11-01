<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_conductor']) || !isset($data['telefono'])) {
    send_response(400, ['error' => 'ID de conductor y teléfono son requeridos.']);
    exit;
}

$id_conductor = (int)$data['id_conductor'];
$telefono = $data['telefono'];

$query = "INSERT INTO telefono_conductor (id_conductor, telefono) VALUES ($1, $2)";
$params = [$id_conductor, $telefono];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    send_response(201, ['success' => 'Teléfono de conductor agregado exitosamente.']);
} else {
    send_response(500, ['error' => 'Error al agregar el teléfono: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>