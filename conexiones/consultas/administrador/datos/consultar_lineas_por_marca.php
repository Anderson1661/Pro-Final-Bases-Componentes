<?php
include('../../../config/conexion.php');
$link = Conectar();

$res = array();
$res['datos'] = array();

$id_marca = isset($_REQUEST['id_marca']) ? $_REQUEST['id_marca'] : '';

if (empty($id_marca)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID marca es requerido"));
    mysqli_close($link);
    exit;
}

$sql = "SELECT id_linea FROM linea_vehiculo WHERE id_marca = '$id_marca' ORDER BY id_linea";

$res1 = mysqli_query($link, $sql);

if ($res1) {
    if (mysqli_num_rows($res1) > 0) {
        while ($row = mysqli_fetch_array($res1)) {
            $item = array();
            $item['id_linea'] = $row['id_linea'];
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

