<?php
/**
 * Script para consultar los países disponibles.
 * 
 * Devuelve una lista de países registrados en el sistema.
 * Se utiliza como primer nivel en los selectores de ubicación.
 */

include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array();
$res['datos'] = array();

$sql = "SELECT id_pais, nombre FROM pais";
$res1 = mysqli_query($link, $sql);

if ($res1) {
    if (mysqli_num_rows($res1) > 0) {
        while ($row = mysqli_fetch_assoc($res1)) {
            $item = array(
                "id_pais" => $row['id_pais'],
                "nombre"  => $row['nombre']
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
