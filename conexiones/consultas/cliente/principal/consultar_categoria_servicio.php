<?php
include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array();
$res['datos'] = array();

$sql = "SELECT descripcion, valor_km FROM categoria_servicio ORDER BY descripcion";
$res1 = mysqli_query($link, $sql);

if ($res1) {
    if (mysqli_num_rows($res1) > 0) {
        while ($row = mysqli_fetch_assoc($res1)) {
            $item = array(
                "descripcion" => $row['descripcion'],
                "valor_km" => (float)$row['valor_km']
            );
            $res['datos'][] = $item;
        }
        $res["success"] = "1";
    } else {
        $res["success"] = "1";
        $res["mensaje"] = "No hay registros disponibles.";
    }
} else {
    $res["success"] = "0";
    $res["mensaje"] = "Error en la consulta: " . mysqli_error($link);
}

echo json_encode($res, JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);

mysqli_close($link);
?>
