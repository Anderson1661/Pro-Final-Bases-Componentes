<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_ruta']) || !isset($data['nombre_pasajero'])) {
    send_response(400, ['error' => 'ID de ruta y nombre del pasajero son requeridos para eliminar.']);
    exit;
}

$id_ruta = (int)$data['id_ruta'];
$nombre_pasajero = $data['nombre_pasajero'];

$query = "DELETE FROM pasajero_ruta WHERE id_ruta = $1 AND nombre_pasajero = $2";
$params = [$id_ruta, $nombre_pasajero];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Pasajero de la ruta eliminado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el registro del pasajero en la ruta.']);
    }
} else {
    send_response(500, ['error' => 'Error al eliminar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>