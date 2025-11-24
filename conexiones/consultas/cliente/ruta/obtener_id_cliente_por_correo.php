<?php
/**
 * Script para obtener el ID de cliente dado su correo.
 * 
 * Recibe el correo electrónico y devuelve el ID del cliente.
 * Se utiliza para vincular operaciones (como crear ruta) al cliente correcto.
 */

// 1. MANEJO DE ERRORES Y CONEXIÓN
try {
    include('../../../config/conexion.php');
    $link = Conectar();
} catch (Exception $e) {
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode(["success" => "0", "mensaje" => "Error de conexión a la base de datos."]);
    exit;
}

header('Content-Type: application/json; charset=utf-8');

// Obtener y sanear el correo usando $_POST
$correo = $_POST['correo'] ?? ''; 

if (empty($correo)) {
    echo json_encode(["success" => "0", "mensaje" => "Falta el parámetro 'correo'."]);
    exit;
}

// 2. PREPARAR Y EJECUTAR LA CONSULTA
$sql = "SELECT id_cliente FROM cliente WHERE correo = ?";

$stmt = mysqli_prepare($link, $sql);

if ($stmt === false) {
    echo json_encode(["success" => "0", "mensaje" => "Error al preparar la consulta SQL."]);
    exit;
}

// Vincular parámetro (s: string)
mysqli_stmt_bind_param($stmt, "s", $correo);

// Ejecutar la consulta
if (mysqli_stmt_execute($stmt)) {
    $result = mysqli_stmt_get_result($stmt);
    
    if ($row = mysqli_fetch_assoc($result)) {
        // Cliente encontrado, devolver el ID
        // Es importante que el valor sea un string JSON válido, aunque PHP lo trate como número
        echo json_encode(["success" => "1", "id_cliente" => $row['id_cliente']]);
    } else {
        // Correo no encontrado en la tabla 'cliente'
        echo json_encode(["success" => "0", "mensaje" => "Cliente no encontrado."]);
    }
} else {
    // Error al ejecutar
    echo json_encode(["success" => "0", "mensaje" => "Error al ejecutar la consulta de cliente: " . mysqli_stmt_error($stmt)]);
}

mysqli_stmt_close($stmt);
mysqli_close($link);

?>