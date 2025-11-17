<?php
ini_set('display_errors', 1);
error_reporting(E_ALL);
// 1. MANEJO DE ERRORES Y CONEXIÓN
try {
    include('../../../config/conexion.php'); // La ruta es correcta
    $link = Conectar();
} catch (Exception $e) {
    // Si falla la conexión, mostramos el error y terminamos la ejecución
    error_log("Fallo de conexión: " . $e->getMessage());
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode(["success" => "0", "mensaje" => "Error de conexión a la base de datos: " . $e->getMessage()]);
    exit;
}

header('Content-Type: application/json; charset=utf-8');

// === CAMBIO TEMPORAL: Usamos $_REQUEST para aceptar GET/URL ===
$direccion_origen = $_REQUEST['direccion_origen'] ?? '';
$direccion_destino = $_REQUEST['direccion_destino'] ?? '';
$id_codigo_postal_origen = $_REQUEST['id_codigo_postal_origen'] ?? '';
$id_codigo_postal_destino = $_REQUEST['id_codigo_postal_destino'] ?? '';
$distancia_km = $_REQUEST['distancia_km'] ?? '';
$id_tipo_servicio = $_REQUEST['id_tipo_servicio'] ?? '';
$id_cliente = $_REQUEST['id_cliente'] ?? '';
$id_categoria_servicio = $_REQUEST['id_categoria_servicio'] ?? '';
$id_metodo_pago = $_REQUEST['id_metodo_pago'] ?? '';
$fecha_hora_reserva = $_REQUEST['fecha_hora_reserva'] ?? ''; 
// ===============================================================

// Campos que pueden ser NULL
$fecha_hora_origen = !empty($_REQUEST['fecha_hora_origen']) ? $_REQUEST['fecha_hora_origen'] : NULL;
$fecha_hora_destino = !empty($_REQUEST['fecha_hora_destino']) ? $_REQUEST['fecha_hora_destino'] : NULL;
$id_conductor = !empty($_REQUEST['id_conductor']) ? $_REQUEST['id_conductor'] : NULL;

// Valores fijos
$id_estado_servicio = 4;

// Validar campos requeridos
if (
    empty($direccion_origen) || empty($direccion_destino) || empty($id_codigo_postal_origen) ||
    empty($id_codigo_postal_destino) || empty($distancia_km) || empty($fecha_hora_reserva) ||
    empty($id_tipo_servicio) || ($id_tipo_servicio <= 0) ||
    empty($id_cliente) || ($id_cliente <= 0) ||
    empty($id_categoria_servicio) || ($id_categoria_servicio <= 0) ||
    empty($id_metodo_pago) || ($id_metodo_pago <= 0)
) {
    error_log("Error de validación: Faltan campos requeridos o IDs inválidos.");
    echo json_encode(["success" => "0", "mensaje" => "Todos los campos requeridos deben ser proporcionados."]);
    exit;
}

// Preparar la consulta SQL
$sql = "INSERT INTO ruta (
    direccion_origen, direccion_destino, id_codigo_postal_origen, id_codigo_postal_destino,
    distancia_km, fecha_hora_reserva, fecha_hora_origen, fecha_hora_destino, 
    id_conductor, id_tipo_servicio, id_cliente, id_estado_servicio, id_categoria_servicio, id_metodo_pago
) VALUES (
    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
)";

$stmt = mysqli_prepare($link, $sql);

if ($stmt === false) {
    error_log("Error al preparar la consulta: " . mysqli_error($link));
    echo json_encode(["success" => "0", "mensaje" => "Error interno del servidor al preparar la consulta."]);
    exit;
}

// Vincular parámetros
mysqli_stmt_bind_param(
    $stmt,
    "ssssdsssiisiii",
    $direccion_origen,
    $direccion_destino,
    $id_codigo_postal_origen,
    $id_codigo_postal_destino,
    $distancia_km,
    $fecha_hora_reserva,
    $fecha_hora_origen,
    $fecha_hora_destino,
    $id_conductor,
    $id_tipo_servicio,
    $id_cliente,
    $id_estado_servicio,
    $id_categoria_servicio,
    $id_metodo_pago
);

// Ejecutar la consulta
if (mysqli_stmt_execute($stmt)) {
    $id_ruta = mysqli_insert_id($link);
    echo json_encode(["success" => "1", "mensaje" => "Ruta registrada correctamente", "id_ruta" => $id_ruta]);
} else {
    error_log("Error al ejecutar la consulta: " . mysqli_stmt_error($stmt));
    echo json_encode(["success" => "0", "mensaje" => "Error al registrar: " . mysqli_stmt_error($stmt)]);
}

mysqli_stmt_close($stmt);
mysqli_close($link);
?>