<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_metodo_pago']) || !isset($data['descripcion'])) {
    send_response(400, ['error' => 'El ID y la descripción son requeridos.']);
    exit;
}

$id = (int)$data['id_metodo_pago'];
$descripcion = $data['descripcion'];

$query = "UPDATE metodo_pago SET descripcion = $1 WHERE id_metodo_pago = $2";
$params = [$descripcion, $id];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Método de pago actualizado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el método de pago o no se realizaron cambios.']);
    }
} else {
    send_response(500, ['error' => 'Error al actualizar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>