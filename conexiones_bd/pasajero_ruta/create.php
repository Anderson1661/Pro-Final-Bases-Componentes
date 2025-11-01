<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_ruta']) || !isset($data['nombre_pasajero'])) {
    send_response(400, ['error' => 'ID de ruta y nombre del pasajero son requeridos.']);
    exit;
}

$id_ruta = (int)$data['id_ruta'];
$nombre_pasajero = $data['nombre_pasajero'];

$query = "INSERT INTO pasajero_ruta (id_ruta, nombre_pasajero) VALUES ($1, $2)";
$params = [$id_ruta, $nombre_pasajero];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    send_response(201, ['success' => 'Pasajero agregado a la ruta exitosamente.']);
} else {
    send_response(500, ['error' => 'Error al agregar el pasajero: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>