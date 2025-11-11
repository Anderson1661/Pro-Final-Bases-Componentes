<?php
include('../config/conexion.php');
$link = Conectar();

$res = array();
$res['datos'] = array();

$sql = "SELECT * FROM conductor ORDER BY id_conductor";

$res1 = mysqli_query($link, $sql);

if ($res1) {
    if (mysqli_num_rows($res1) > 0) {
        while ($row = mysqli_fetch_array($res1)) {
            $item = array();
            $item['id_conductor'] = $row['id_conductor'];
            $item['placa_vehiculo'] = $row['placa_vehiculo'];
            $item['identificacion'] = $row['identificacion'];
            $item['id_tipo_identificacion'] = $row['id_tipo_identificacion'];
            $item['nombre'] = $row['nombre'];
            $item['direccion'] = $row['direccion'];
            $item['correo'] = $row['correo'];
            $item['id_genero'] = $row['id_genero'];
            $item['codigo_postal'] = $row['codigo_postal'];
            $item['id_pais_nacionalidad'] = $row['id_pais_nacionalidad'];
            $item['url_foto'] = $row['url_foto'];
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

