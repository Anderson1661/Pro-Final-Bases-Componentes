<?php
/**
 * Script para consultar el catálogo de preguntas de seguridad.
 * 
 * Devuelve todas las preguntas de seguridad disponibles.
 * Se utiliza para que el conductor pueda seleccionar o cambiar sus preguntas.
 */

include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Error al obtener preguntas");

$sql = "SELECT id_pregunta, descripcion FROM preguntas_seguridad";
$result = mysqli_query($link, $sql);

if ($result) {
    if (mysqli_num_rows($result) > 0) {
        $preguntas = array();
        while ($row = mysqli_fetch_assoc($result)) {
            $preguntas[] = $row;
        }
        $res["success"] = "1";
        $res["mensaje"] = "Preguntas cargadas con éxito";
        $res["preguntas"] = $preguntas;
    } else {
        $res["success"] = "0";
        $res["mensaje"] = "No se encontraron preguntas de seguridad.";
    }
} else {
    $res["mensaje"] = "Error en la consulta SQL: " . mysqli_error($link);
}

echo json_encode($res);
mysqli_close($link);
?>