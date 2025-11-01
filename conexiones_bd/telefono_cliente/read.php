<?php
require_once '../conexion.php';

$id_cliente = isset($_GET['id_cliente']) ? (int)$_GET['id_cliente'] : null;

if ($id_cliente) {
    $query = "SELECT * FROM telefono_cliente WHERE id_cliente = $1";
    $result = pg_query_params($conn, $query, [$id_cliente]);
} else {
    $query = "SELECT * FROM telefono_cliente";
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