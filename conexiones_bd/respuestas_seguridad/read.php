<?php
require_once '../conexion.php';

$id_usuario = isset($_GET['id_usuario']) ? (int)$_GET['id_usuario'] : null;

if ($id_usuario) {
    $query = "SELECT * FROM respuestas_seguridad WHERE id_usuario = $1";
    $result = pg_query_params($conn, $query, [$id_usuario]);
} else {
    $query = "SELECT * FROM respuestas_seguridad";
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