<?php
include('../../../config/conexion.php');
$link = Conectar();

$res = array();
$res['datos'] = array();

$sql = "SELECT id_usuario, correo FROM usuario ORDER BY correo";

$res1 = mysqli_query($link, $sql);

if ($res1) {
    if (mysqli_num_rows($res1) > 0) {
        while ($row = mysqli_fetch_array($res1)) {
            $item = array();
            $item['id_usuario'] = $row['id_usuario'];
            $item['correo'] = $row['correo'];
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

header('Content-Type: application/json; charset=utf-8');
echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>

