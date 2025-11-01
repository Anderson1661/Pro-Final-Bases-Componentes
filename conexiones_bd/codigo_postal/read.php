<?php
require_once '../conexion.php';

$id = isset($_GET['id_codigo_postal']) ? $_GET['id_codigo_postal'] : null;

if ($id) {
    $query = "SELECT * FROM codigo_postal WHERE id_codigo_postal = $1";
    $result = pg_query_params($conn, $query, [$id]);
} else {
    $query = "SELECT * FROM codigo_postal ORDER BY id_codigo_postal ASC";
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