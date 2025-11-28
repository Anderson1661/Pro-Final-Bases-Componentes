<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer el input JSON
$input = json_decode(file_get_contents('php://input'), true);

// Verificar si se recibieron datos JSON
if (json_last_error() !== JSON_ERROR_NONE) {
    echo json_encode(array("success" => "0", "mensaje" => "Error en el formato JSON: " . json_last_error_msg()));
    exit;
}

// Obtener datos del JSON
$direccion_origen = isset($input['direccion_origen']) ? $input['direccion_origen'] : '';
$direccion_destino = isset($input['direccion_destino']) ? $input['direccion_destino'] : '';
$id_codigo_postal_origen = isset($input['id_codigo_postal_origen']) ? $input['id_codigo_postal_origen'] : '';
$id_codigo_postal_destino = isset($input['id_codigo_postal_destino']) ? $input['id_codigo_postal_destino'] : '';
$distancia_km = isset($input['distancia_km']) ? $input['distancia_km'] : '';
$fecha_hora_reserva = isset($input['fecha_hora_reserva']) ? $input['fecha_hora_reserva'] : '';
$fecha_hora_origen = isset($input['fecha_hora_origen']) ? $input['fecha_hora_origen'] : '';
$fecha_hora_destino = isset($input['fecha_hora_destino']) ? $input['fecha_hora_destino'] : '';
$id_conductor = isset($input['id_conductor']) ? $input['id_conductor'] : '';
$id_tipo_servicio = isset($input['id_tipo_servicio']) ? $input['id_tipo_servicio'] : '';
$id_cliente = isset($input['id_cliente']) ? $input['id_cliente'] : '';
$id_estado_servicio = isset($input['id_estado_servicio']) ? $input['id_estado_servicio'] : '';
$id_categoria_servicio = isset($input['id_categoria_servicio']) ? $input['id_categoria_servicio'] : '';
$id_metodo_pago = isset($input['id_metodo_pago']) ? $input['id_metodo_pago'] : '';

if (empty($direccion_origen) || empty($direccion_destino) || empty($id_codigo_postal_origen) || 
    empty($id_codigo_postal_destino) || empty($distancia_km) || empty($fecha_hora_reserva) || 
    empty($id_tipo_servicio) || empty($id_cliente) || empty($id_estado_servicio) || 
    empty($id_categoria_servicio) || empty($id_metodo_pago)) {
    echo json_encode(array("success" => "0", "mensaje" => "Todos los campos son requeridos"));
} else {
    // Limpiar y escapar datos
    $direccion_origen = mysqli_real_escape_string($link, $direccion_origen);
    $direccion_destino = mysqli_real_escape_string($link, $direccion_destino);
    $id_codigo_postal_origen = mysqli_real_escape_string($link, $id_codigo_postal_origen);
    $id_codigo_postal_destino = mysqli_real_escape_string($link, $id_codigo_postal_destino);
    $distancia_km = mysqli_real_escape_string($link, $distancia_km);
    $fecha_hora_reserva = mysqli_real_escape_string($link, $fecha_hora_reserva);
    
    // Manejar campos opcionales
    $fecha_hora_origen_sql = !empty($fecha_hora_origen) ? "'" . mysqli_real_escape_string($link, $fecha_hora_origen) . "'" : "NULL";
    $fecha_hora_destino_sql = !empty($fecha_hora_destino) ? "'" . mysqli_real_escape_string($link, $fecha_hora_destino) . "'" : "NULL";
    $id_conductor_sql = !empty($id_conductor) ? "'" . mysqli_real_escape_string($link, $id_conductor) . "'" : "NULL";

    $id_tipo_servicio = mysqli_real_escape_string($link, $id_tipo_servicio);
    $id_cliente = mysqli_real_escape_string($link, $id_cliente);
    $id_estado_servicio = mysqli_real_escape_string($link, $id_estado_servicio);
    $id_categoria_servicio = mysqli_real_escape_string($link, $id_categoria_servicio);
    $id_metodo_pago = mysqli_real_escape_string($link, $id_metodo_pago);
    
    // total y pago_conductor son calculados por triggers, no se incluyen en INSERT
    $sql = "INSERT INTO ruta (direccion_origen, direccion_destino, id_codigo_postal_origen, id_codigo_postal_destino, 
            distancia_km, fecha_hora_reserva, fecha_hora_origen, fecha_hora_destino, id_conductor, id_tipo_servicio, 
            id_cliente, id_estado_servicio, id_categoria_servicio, id_metodo_pago) 
            VALUES ('$direccion_origen', '$direccion_destino', '$id_codigo_postal_origen', '$id_codigo_postal_destino', 
            '$distancia_km', '$fecha_hora_reserva', $fecha_hora_origen_sql, $fecha_hora_destino_sql, $id_conductor_sql, 
            '$id_tipo_servicio', '$id_cliente', '$id_estado_servicio', '$id_categoria_servicio', '$id_metodo_pago')";
    
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Servicio registrado correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>