<?php
include('../../../config/conexion.php');
$link = Conectar();

$res = array();
$res['datos'] = array();

$sql = "
    SELECT 
        v.placa,
        m.nombre_marca AS marca,
        l.id_linea AS linea
    FROM vehiculo v
    INNER JOIN marca_vehiculo m ON v.id_marca = m.id_marca
    INNER JOIN linea_vehiculo l ON v.linea_vehiculo = l.id_linea 
        AND v.id_marca = l.id_marca
    ORDER BY v.placa
";

$res1 = mysqli_query($link, $sql);

if ($res1) {
    if (mysqli_num_rows($res1) > 0) {
        while ($row = mysqli_fetch_array($res1)) {

            $item = array();
            $item['placa'] = $row['placa'];
            $item['marca'] = $row['marca']; 
            $item['linea'] = $row['linea'];

            array_push($res['datos'], $item);
        }
        $res["success"] = "1";
    } else {
        $res["success"] = "1";
        $res["mensaje"] = "No hay vehículos registrados";
    }
} else {
    $res["success"] = "0";
    $res["mensaje"] = "Error al consultar: " . mysqli_error($link);
}

header('Content-Type: application/json; charset=utf-8');
echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>
