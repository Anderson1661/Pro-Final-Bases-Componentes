<?php
require_once '../conexion.php';

$query = "SELECT * FROM vw_cantidad_servicios_por_mes";
$result = pg_query($conn, $query);

if ($result) {
    $data = pg_fetch_all($result, PGSQL_ASSOC);
    send_response(200, $data ? $data : []);
} else {
    send_response(500, ['error' => 'Error en la consulta de la vista: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>