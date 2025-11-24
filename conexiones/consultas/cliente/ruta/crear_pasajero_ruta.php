<?php
/**
 * Script para registrar un pasajero en una ruta.
 * 
 * Recibe el ID de la ruta y el nombre del pasajero.
 * Se llama múltiples veces si hay varios pasajeros para un mismo servicio.
 */

include('../../../config/conexion.php'); // Ajusta la ruta a tu archivo de conexión
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

// Recoger y sanear los datos de entrada
$id_ruta = $_POST['id_ruta'] ?? '';
$nombre_pasajero = $_POST['nombre_pasajero'] ?? '';

// Validar campos requeridos
if (empty($id_ruta) || $id_ruta <= 0 || empty($nombre_pasajero)) {
    error_log("Error de validación en pasajero_ruta: ID de ruta o nombre de pasajero faltante.");
    echo json_encode(["success" => "0", "mensaje" => "ID de ruta y nombre de pasajero son requeridos."]);
    exit;
}

// Preparar la consulta SQL
$sql = "INSERT INTO pasajero_ruta (id_ruta, nombre_pasajero) VALUES (?, ?)";

$stmt = mysqli_prepare($link, $sql);

if ($stmt === false) {
    error_log("Error al preparar la consulta de pasajero_ruta: " . mysqli_error($link));
    echo json_encode(["success" => "0", "mensaje" => "Error interno del servidor al preparar la consulta de pasajero."]);
    exit;
}

// Vincular parámetros
// i: id_ruta (INT), s: nombre_pasajero (VARCHAR)
mysqli_stmt_bind_param(
    $stmt,
    "is",
    $id_ruta,
    $nombre_pasajero
);

// Ejecutar la consulta
if (mysqli_stmt_execute($stmt)) {
    echo json_encode(["success" => "1", "mensaje" => "Pasajero registrado correctamente."]);
} else {
    error_log("Error al ejecutar la consulta de pasajero_ruta: " . mysqli_stmt_error($stmt));
    echo json_encode(["success" => "0", "mensaje" => "Error al registrar pasajero: " . mysqli_stmt_error($stmt)]);
}

mysqli_stmt_close($stmt);
mysqli_close($link);
?>