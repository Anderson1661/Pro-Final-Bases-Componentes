<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_codigo_postal'])) {
    send_response(400, ['error' => 'El ID del código postal es requerido.']);
    exit;
}

$id = $data['id_codigo_postal'];
$updates = [];
$params = [];
$param_count = 1;

if (isset($data['id_pais'])) {
    $updates[] = "id_pais = $" . $param_count++;
    $params[] = (int)$data['id_pais'];
}
if (isset($data['departamento'])) {
    $updates[] = "departamento = $" . $param_count++;
    $params[] = $data['departamento'];
}
if (isset($data['ciudad'])) {
    $updates[] = "ciudad = $" . $param_count++;
    $params[] = $data['ciudad'];
}

if (empty($updates)) {
    send_response(400, ['error' => 'No hay campos para actualizar.']);
    exit;
}

$params[] = $id;
$query = "UPDATE codigo_postal SET " . implode(', ', $updates) . " WHERE id_codigo_postal = $" . $param_count;

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Código postal actualizado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el código postal o no se realizaron cambios.']);
    }
} else {
    send_response(500, ['error' => 'Error al actualizar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>