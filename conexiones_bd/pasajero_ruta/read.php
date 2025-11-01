<?php
require_once '../conexion.php';

$id_ruta = isset($_GET['id_ruta']) ? (int)$_GET['id_ruta'] : null;

if ($id_ruta) {
    $query = "SELECT * FROM pasajero_ruta WHERE id_ruta = $1";
    $result = pg_query_params($conn, $query, [$id_ruta]);
} else {
    $query = "SELECT * FROM pasajero_ruta";
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