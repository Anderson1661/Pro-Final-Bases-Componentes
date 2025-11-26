<?php
include('../../../../config/conexion.php');
$link = Conectar();

$res = array();
$res['datos'] = array();

$sql = "SELECT * FROM vehiculo ORDER BY placa";

$res1 = mysqli_query($link, $sql);

if ($res1) {
    if (mysqli_num_rows($res1) > 0) {
        while ($row = mysqli_fetch_array($res1)) {
            $item = array();
            $item['placa'] = $row['placa'];
            $item['linea_vehiculo'] = $row['linea_vehiculo'];
            $item['modelo'] = $row['modelo'];
            $item['id_color'] = $row['id_color'];
            $item['id_marca'] = $row['id_marca'];
            $item['id_tipo_servicio'] = $row['id_tipo_servicio'];
            $item['id_estado_vehiculo'] = $row['id_estado_vehiculo'];
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

