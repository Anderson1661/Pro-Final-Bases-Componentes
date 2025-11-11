
<?php
include('../../../config/conexion.php'); // Ajusta la ruta según tu estructura
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$direccion_origen = $_POST['direccion_origen'] ?? '';
$direccion_destino = $_POST['direccion_destino'] ?? '';
$id_codigo_postal_origen = $_POST['id_codigo_postal_origen'] ?? '';
$id_codigo_postal_destino = $_POST['id_codigo_postal_destino'] ?? '';
$distancia_km = $_POST['distancia_km'] ?? '';
$fecha_hora_reserva = $_POST['fecha_hora_reserva'] ?? '';
$fecha_hora_origen = $_POST['fecha_hora_origen'] ?? '';
$fecha_hora_destino = $_POST['fecha_hora_destino'] ?? '';
$id_conductor = $_POST['id_conductor'] ?? '';
$id_tipo_servicio = $_POST['id_tipo_servicio'] ?? '';
$id_cliente = $_POST['id_cliente'] ?? '';
$id_estado_servicio = $_POST['id_estado_servicio'] ?? '';
$id_categoria_servicio = $_POST['id_categoria_servicio'] ?? '';
$id_metodo_pago = $_POST['id_metodo_pago'] ?? '';

if (
    empty($direccion_origen) || empty($direccion_destino) || empty($id_codigo_postal_origen) ||
    empty($id_codigo_postal_destino) || empty($distancia_km) || empty($fecha_hora_reserva) ||
    empty($id_tipo_servicio) || empty($id_cliente) || empty($id_categoria_servicio) || empty($id_metodo_pago)
) {
    echo json_encode(["success" => "0", "mensaje" => "Todos los campos son requeridos"]);
    exit;
}

$sql = "INSERT INTO ruta (
    direccion_origen, direccion_destino, id_codigo_postal_origen, id_codigo_postal_destino,
    distancia_km, fecha_hora_reserva, fecha_hora_origen, fecha_hora_destino, 
    id_conductor, id_tipo_servicio, id_cliente, id_estado_servicio, id_categoria_servicio, id_metodo_pago
) VALUES (
    '$direccion_origen', '$direccion_destino', '$id_codigo_postal_origen', '$id_codigo_postal_destino',
    '$distancia_km', '$fecha_hora_reserva', '$fecha_hora_origen', '$fecha_hora_destino',
    '$id_conductor', '$id_tipo_servicio', '$id_cliente', '$id_estado_servicio', '$id_categoria_servicio', '$id_metodo_pago'
)";

if (mysqli_query($link, $sql)) {
    echo json_encode(["success" => "1", "mensaje" => "Ruta registrada correctamente"]);
} else {
    echo json_encode(["success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)]);
}

mysqli_close($link);
?>
