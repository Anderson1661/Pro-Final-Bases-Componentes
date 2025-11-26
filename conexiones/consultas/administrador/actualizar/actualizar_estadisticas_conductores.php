<?php
/**
 * Script para ejecutar el procedimiento almacenado ActualizarResumenMensual
 */

include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "No se pudo ejecutar el procedimiento");

try {
    // Preparar y ejecutar el procedimiento almacenado
    $sql = "CALL ActualizarEstadisticasConductores()";
    
    if (mysqli_query($link, $sql)) {
        $res["success"] = "1";
        $res["mensaje"] = "Procedimiento almacenado ejecutado correctamente";
        
    } else {
        $res["success"] = "0";
        $res["mensaje"] = "Error al ejecutar el procedimiento: " . mysqli_error($link);
    }

} catch (Exception $e) {
    $res["success"] = "0";
    $res["mensaje"] = "Excepción: " . $e->getMessage();
}

echo json_encode($res);
mysqli_close($link);
?>