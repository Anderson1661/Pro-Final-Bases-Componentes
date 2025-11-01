<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_conductor'])) {
    send_response(400, ['error' => 'El ID del conductor es requerido.']);
    exit;
}

$id = (int)$data['id_conductor'];
$updates = [];
$params = [];
$param_count = 1;

if (isset($data['identificacion'])) { $updates[] = "identificacion = $" . $param_count++; $params[] = $data['identificacion']; }
if (isset($data['id_tipo_identificacion'])) { $updates[] = "id_tipo_identificacion = $" . $param_count++; $params[] = (int)$data['id_tipo_identificacion']; }
if (isset($data['nombre'])) { $updates[] = "nombre = $" . $param_count++; $params[] = $data['nombre']; }
if (isset($data['direccion'])) { $updates[] = "direccion = $" . $param_count++; $params[] = $data['direccion']; }
if (isset($data['correo'])) { $updates[] = "correo = $" . $param_count++; $params[] = $data['correo']; }
if (isset($data['id_genero'])) { $updates[] = "id_genero = $" . $param_count++; $params[] = (int)$data['id_genero']; }
if (isset($data['codigo_postal'])) { $updates[] = "codigo_postal = $" . $param_count++; $params[] = $data['codigo_postal']; }
if (isset($data['url_foto'])) { $updates[] = "url_foto = $" . $param_count++; $params[] = $data['url_foto']; }
if (isset($data['id_sucursal'])) { $updates[] = "id_sucursal = $" . $param_count++; $params[] = (int)$data['id_sucursal']; }

if (empty($updates)) {
    send_response(400, ['error' => 'No hay campos para actualizar.']);
    exit;
}

$params[] = $id;
$query = "UPDATE conductor SET " . implode(', ', $updates) . " WHERE id_conductor = $" . $param_count;

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Conductor actualizado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el conductor o no se realizaron cambios.']);
    }
} else {
    send_response(500, ['error' => 'Error al actualizar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>