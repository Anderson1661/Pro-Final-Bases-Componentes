<?php
require_once '../conexion.php';

$id_conductor = isset($_GET['id_conductor']) ? (int)$_GET['id_conductor'] : null;

if ($id_conductor) {
    $query = "SELECT * FROM telefono_conductor WHERE id_conductor = $1";
    $result = pg_query_params($conn, $query, [$id_conductor]);
} else {
    $query = "SELECT * FROM telefono_conductor";
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