<?php
require_once '../conexion.php';

$id_administrador = isset($_GET['id_administrador']) ? (int)$_GET['id_administrador'] : null;

if ($id_administrador) {
    $query = "SELECT * FROM telefono_administrador WHERE id_administrador = $1";
    $result = pg_query_params($conn, $query, [$id_administrador]);
} else {
    $query = "SELECT * FROM telefono_administrador";
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