<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_tipo_usuario']) || !isset($data['correo']) || !isset($data['contrasenia'])) {
    send_response(400, ['error' => 'ID de tipo de usuario, correo y contraseña son requeridos.']);
    exit;
}

$id_tipo_usuario = (int)$data['id_tipo_usuario'];
$correo = $data['correo'];
// En una aplicación real, la contraseña debería ser hasheada antes de guardarla.
// Por ejemplo: $contrasenia = password_hash($data['contrasenia'], PASSWORD_BCRYPT);
$contrasenia = $data['contrasenia'];

$query = "INSERT INTO usuario (id_tipo_usuario, correo, contrasenia) VALUES ($1, $2, $3)";
$params = [$id_tipo_usuario, $correo, $contrasenia];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    send_response(201, ['success' => 'Usuario creado exitosamente.']);
} else {
    send_response(500, ['error' => 'Error al crear el usuario: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>