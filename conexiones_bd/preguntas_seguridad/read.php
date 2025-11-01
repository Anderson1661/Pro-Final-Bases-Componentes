<?php
require_once '../conexion.php';

$id = isset($_GET['id_pregunta']) ? (int)$_GET['id_pregunta'] : null;

if ($id) {
    $query = "SELECT * FROM preguntas_seguridad WHERE id_pregunta = $1";
    $result = pg_query_params($conn, $query, [$id]);
} else {
    $query = "SELECT * FROM preguntas_seguridad ORDER BY descripcion ASC";
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