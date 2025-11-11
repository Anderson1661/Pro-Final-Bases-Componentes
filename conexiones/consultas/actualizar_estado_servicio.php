<?php
header('Content-Type: application/json; charset=utf-8');
include('../config/conexion.php');

$link = Conectar();

$id_servicio = $_POST['id_servicio'] ?? null;
$nuevo_estado = $_POST['nuevo_estado'] ?? '';

if (empty($id_servicio) || empty($nuevo_estado)) {
    echo json_encode(["success" => false, "message" => "Faltan datos requeridos (id_servicio, nuevo_estado)."]);
    exit;
}

// Using prepared statements to prevent SQL injection
$sql = "UPDATE servicio SET estado_servicio = ? WHERE id_servicio = ?";
$stmt = mysqli_prepare($link, $sql);

if ($stmt) {
    mysqli_stmt_bind_param($stmt, "si", $nuevo_estado, $id_servicio);
    
    if (mysqli_stmt_execute($stmt)) {
        if (mysqli_stmt_affected_rows($stmt) > 0) {
            echo json_encode(["success" => true, "message" => "Estado del servicio actualizado correctamente."]);
        } else {
            echo json_encode(["success" => false, "message" => "No se encontró el servicio o el estado ya era el mismo."]);
        }
    } else {
        echo json_encode(["success" => false, "message" => "Error al ejecutar la actualización."]);
    }

    mysqli_stmt_close($stmt);
} else {
    echo json_encode(["success" => false, "message" => "Error en la preparación de la consulta: " . mysqli_error($link)]);
}

mysqli_close($link);
?>
