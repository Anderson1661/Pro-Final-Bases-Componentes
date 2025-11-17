<?php
include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

if (isset($_POST['id_ruta'])) {
    $id_ruta = trim($_POST['id_ruta']);

    // ID 4 para "Cancelado"
    $ID_ESTADO_CANCELADO = 1; 

    // Soft Delete: Actualizar el estado del servicio a Cancelado
    $sql = "UPDATE ruta SET id_estado_servicio = ? WHERE id_ruta = ?";
    
    $stmt = mysqli_prepare($link, $sql);
    // 'ii' indica que ambos parámetros son enteros (id_estado_servicio, id_ruta)
    mysqli_stmt_bind_param($stmt, "ii", $ID_ESTADO_CANCELADO, $id_ruta);
    
    if (mysqli_stmt_execute($stmt)) {
        if (mysqli_stmt_affected_rows($stmt) > 0) {
            $res["success"] = "1";
            $res["mensaje"] = "Servicio cancelado exitosamente.";
        } else {
            $res["success"] = "0";
            $res["mensaje"] = "No se encontró el servicio o ya está en estado finalizado.";
        }
    } else {
        $res["mensaje"] = "Error al cancelar el servicio: " . mysqli_stmt_error($stmt);
    }
    mysqli_stmt_close($stmt);
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>