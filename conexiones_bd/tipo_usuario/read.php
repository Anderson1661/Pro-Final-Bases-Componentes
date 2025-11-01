<?php
require_once '../conexion.php';

$id = isset($_GET['id_tipo_usuario']) ? (int)$_GET['id_tipo_usuario'] : null;

if ($id) {
    // Leer un solo tipo de usuario
    $query = "SELECT * FROM tipo_usuario WHERE id_tipo_usuario = $1";
    $result = pg_query_params($conn, $query, [$id]);
} else {
    // Leer todos los tipos de usuario
    $query = "SELECT * FROM tipo_usuario ORDER BY id_tipo_usuario ASC";
    $result = pg_query($conn, $query);
}

if ($result) {
    $data = pg_fetch_all($result, PGSQL_ASSOC);
    send_response(200, $data ? $data : []);
} else {
    send_response(500, ['error' => 'Error en la consulta: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>