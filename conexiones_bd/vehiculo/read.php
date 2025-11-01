<?php
require_once '../conexion.php';

$placa = isset($_GET['placa']) ? $_GET['placa'] : null;

if ($placa) {
    $query = "SELECT * FROM vehiculo WHERE placa = $1";
    $result = pg_query_params($conn, $query, [$placa]);
} else {
    $query = "SELECT * FROM vehiculo ORDER BY placa ASC";
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