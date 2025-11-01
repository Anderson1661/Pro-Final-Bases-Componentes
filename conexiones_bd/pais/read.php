<?php
require_once '../conexion.php';

$id = isset($_GET['id_pais']) ? (int)$_GET['id_pais'] : null;

if ($id) {
    $query = "SELECT * FROM pais WHERE id_pais = $1";
    $result = pg_query_params($conn, $query, [$id]);
} else {
    $query = "SELECT * FROM pais ORDER BY nombre ASC";
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