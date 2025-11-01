<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_administrador']) || !isset($data['telefono'])) {
    send_response(400, ['error' => 'ID de administrador y teléfono son requeridos.']);
    exit;
}

$id_administrador = (int)$data['id_administrador'];
$telefono = $data['telefono'];

$query = "INSERT INTO telefono_administrador (id_administrador, telefono) VALUES ($1, $2)";
$params = [$id_administrador, $telefono];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    send_response(201, ['success' => 'Teléfono de administrador agregado exitosamente.']);
} else {
    send_response(500, ['error' => 'Error al agregar el teléfono: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>