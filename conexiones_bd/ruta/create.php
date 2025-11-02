<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

$required_fields = ['direccion_origen', 'direccion_destino', 'id_codigo_postal_origen', 'id_codigo_postal_destino', 'distancia_km', 'fecha_hora_reserva', 'fecha_hora_origen', 'fecha_hora_destino', 'id_conductor', 'id_tipo_servicio', 'id_cliente', 'id_estado_servicio', 'id_categoria_servicio', 'id_metodo_pago'];

foreach ($required_fields as $field) {
    if (!isset($data[$field])) {
        send_response(400, ['error' => "El campo {$field} es requerido."]);
        exit;
    }
}

$query = "INSERT INTO ruta (direccion_origen, direccion_destino, id_codigo_postal_origen, id_codigo_postal_destino, distancia_km, fecha_hora_reserva, fecha_hora_origen, fecha_hora_destino, id_conductor, id_tipo_servicio, id_cliente, id_estado_servicio, id_categoria_servicio, id_metodo_pago) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14)";
$params = array_values($data);

$result = pg_query_params($conn, $query, $params);

if ($result) {
    send_response(201, ['success' => 'Ruta creada exitosamente.']);
} else {
    send_response(500, ['error' => 'Error al crear la ruta: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>