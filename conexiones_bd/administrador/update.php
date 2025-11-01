<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_administrador'])) {
    send_response(400, ['error' => 'El ID del administrador es requerido.']);
    exit;
}

$id = (int)$data['id_administrador'];
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

if (empty($updates)) {
    send_response(400, ['error' => 'No hay campos para actualizar.']);
    exit;
}

$params[] = $id;
$query = "UPDATE administrador SET " . implode(', ', $updates) . " WHERE id_administrador = $" . $param_count;

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Administrador actualizado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el administrador o no se realizaron cambios.']);
    }
} else {
    send_response(500, ['error' => 'Error al actualizar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>