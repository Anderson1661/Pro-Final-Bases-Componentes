<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_marca']) || !isset($data['nombre_marca'])) {
    send_response(400, ['error' => 'El ID y el nombre de la marca son requeridos.']);
    exit;
}

$id = (int)$data['id_marca'];
$nombre_marca = $data['nombre_marca'];

$query = "UPDATE marca_vehiculo SET nombre_marca = $1 WHERE id_marca = $2";
$params = [$nombre_marca, $id];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Marca de vehículo actualizada.']);
    } else {
        send_response(404, ['message' => 'No se encontró la marca de vehículo o no se realizaron cambios.']);
    }
} else {
    send_response(500, ['error' => 'Error al actualizar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>