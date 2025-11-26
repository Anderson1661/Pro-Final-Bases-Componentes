<?php
include('../../../../config/conexion.php');
$link = Conectar();

$res = array();
$res['datos'] = array();

$sql = "SELECT * FROM ruta ORDER BY id_ruta";

$res1 = mysqli_query($link, $sql);

if ($res1) {
    if (mysqli_num_rows($res1) > 0) {
        while ($row = mysqli_fetch_array($res1)) {
            $item = array();
            $item['id_ruta'] = $row['id_ruta'];
            $item['direccion_origen'] = $row['direccion_origen'];
            $item['direccion_destino'] = $row['direccion_destino'];
            $item['id_codigo_postal_origen'] = $row['id_codigo_postal_origen'];
            $item['id_codigo_postal_destino'] = $row['id_codigo_postal_destino'];
            $item['distancia_km'] = $row['distancia_km'];
            $item['fecha_hora_reserva'] = $row['fecha_hora_reserva'];
            $item['fecha_hora_origen'] = $row['fecha_hora_origen'];
            $item['fecha_hora_destino'] = $row['fecha_hora_destino'];
            $item['id_conductor'] = $row['id_conductor'];
            $item['id_tipo_servicio'] = $row['id_tipo_servicio'];
            $item['id_cliente'] = $row['id_cliente'];
            $item['id_estado_servicio'] = $row['id_estado_servicio'];
            $item['id_categoria_servicio'] = $row['id_categoria_servicio'];
            $item['id_metodo_pago'] = $row['id_metodo_pago'];
            $item['total'] = $row['total'];
            $item['pago_conductor'] = $row['pago_conductor'];
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

