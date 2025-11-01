<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_cliente']) || !isset($data['telefono'])) {
    send_response(400, ['error' => 'ID de cliente y teléfono son requeridos.']);
    exit;
}

$id_cliente = (int)$data['id_cliente'];
$telefono = $data['telefono'];

$query = "INSERT INTO telefono_cliente (id_cliente, telefono) VALUES ($1, $2)";
$params = [$id_cliente, $telefono];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    send_response(201, ['success' => 'Teléfono de cliente agregado exitosamente.']);
} else {
    send_response(500, ['error' => 'Error al agregar el teléfono: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>