<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_usuario'])) {
    send_response(400, ['error' => 'El ID del usuario es requerido.']);
    exit;
}

$id = (int)$data['id_usuario'];
$updates = [];
$params = [];
$param_count = 1;

if (isset($data['id_tipo_usuario'])) {
    $updates[] = "id_tipo_usuario = $" . $param_count++;
    $params[] = (int)$data['id_tipo_usuario'];
}
if (isset($data['correo'])) {
    $updates[] = "correo = $" . $param_count++;
    $params[] = $data['correo'];
}
if (isset($data['contrasenia'])) {
    // En una aplicación real, la contraseña debería ser hasheada.
    $updates[] = "contrasenia = $" . $param_count++;
    $params[] = $data['contrasenia'];
}

if (empty($updates)) {
    send_response(400, ['error' => 'No hay campos para actualizar.']);
    exit;
}

$params[] = $id;
$query = "UPDATE usuario SET " . implode(', ', $updates) . " WHERE id_usuario = $" . $param_count;

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Usuario actualizado.']);
    } else {
        send_response(404, ['message' => 'No se encontró el usuario o no se realizaron cambios.']);
    }
} else {
    send_response(500, ['error' => 'Error al actualizar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>