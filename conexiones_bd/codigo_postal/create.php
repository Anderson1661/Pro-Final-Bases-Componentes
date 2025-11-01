<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_codigo_postal']) || !isset($data['id_pais']) || !isset($data['departamento']) || !isset($data['ciudad'])) {
    send_response(400, ['error' => 'Todos los campos son requeridos: id_codigo_postal, id_pais, departamento, ciudad.']);
    exit;
}

$id_codigo_postal = $data['id_codigo_postal'];
$id_pais = (int)$data['id_pais'];
$departamento = $data['departamento'];
$ciudad = $data['ciudad'];

$query = "INSERT INTO codigo_postal (id_codigo_postal, id_pais, departamento, ciudad) VALUES ($1, $2, $3, $4)";
$params = [$id_codigo_postal, $id_pais, $departamento, $ciudad];

$result = pg_query_params($conn, $query, $params);

if ($result) {
    send_response(201, ['success' => 'Código postal creado exitosamente.']);
} else {
    send_response(500, ['error' => 'Error al crear el código postal: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>