<?php
/**
 * Script para consultar los tipos de servicio.
 * 
 * Devuelve una lista de tipos de servicio (ej. Transporte, Encomienda, etc.).
 * Se utiliza para que el usuario seleccione qué tipo de servicio desea solicitar.
 */

include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array();
$res['datos'] = array();

$sql = "SELECT id_tipo_servicio, descripcion FROM tipo_servicio";
$res1 = mysqli_query($link, $sql);

if ($res1) {
    if (mysqli_num_rows($res1) > 0) {
        while ($row = mysqli_fetch_assoc($res1)) {
            $item = array(
                "descripcion"  => $row['descripcion']
            );
            array_push($res['datos'], $item);
        }
        $res["success"] = "1";
    } else {
        $res["success"] = "1";
        $res["mensaje"] = "No hay registros";
    }
} else {
    $res["success"] = "0";
    $res["mensaje"] = "Error al consultar: " . mysqli_error($link);
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>
