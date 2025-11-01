<?php
require_once '../conexion.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id_ruta'])) {
    send_response(400, ['error' => 'El ID de la ruta es requerido.']);
    exit;
}

$id = (int)$data['id_ruta'];
$updates = [];
$params = [];
$param_count = 1;

$allowed_fields = ['direccion_origen', 'direccion_destino', 'id_codigo_postal_origen', 'id_codigo_postal_destino', 'distancia_km', 'fecha_hora_reserva', 'fecha_hora_origen', 'fecha_hora_destino', 'id_conductor', 'id_tipo_servicio', 'id_cliente', 'id_estado_servicio', 'placa_vehiculo', 'id_categoria_servicio', 'id_metodo_pago', 'total'];

foreach ($allowed_fields as $field) {
    if (isset($data[$field])) {
        $updates[] = "{$field} = $" . $param_count++;
        $params[] = $data[$field];
    }
}

if (empty($updates)) {
    send_response(400, ['error' => 'No hay campos para actualizar.']);
    exit;
}

$params[] = $id;
$query = "UPDATE ruta SET " . implode(', ', $updates) . " WHERE id_ruta = $" . $param_count;

$result = pg_query_params($conn, $query, $params);

if ($result) {
    if (pg_affected_rows($result) > 0) {
        send_response(200, ['success' => 'Ruta actualizada.']);
    } else {
        send_response(404, ['message' => 'No se encontró la ruta o no se realizaron cambios.']);
    }
} else {
    send_response(500, ['error' => 'Error al actualizar: ' . pg_last_error($conn)]);
}

pg_close($conn);
?>