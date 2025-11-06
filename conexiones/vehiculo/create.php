<?php
include('../config/conexion.php');
$link = Conectar();

$placa = isset($_REQUEST['placa']) ? $_REQUEST['placa'] : '';
$linea_vehiculo = isset($_REQUEST['linea_vehiculo']) ? $_REQUEST['linea_vehiculo'] : '';
$modelo = isset($_REQUEST['modelo']) ? $_REQUEST['modelo'] : '';
$id_color = isset($_REQUEST['id_color']) ? $_REQUEST['id_color'] : '';
$id_marca = isset($_REQUEST['id_marca']) ? $_REQUEST['id_marca'] : '';
$id_tipo_servicio = isset($_REQUEST['id_tipo_servicio']) ? $_REQUEST['id_tipo_servicio'] : '';
$id_estado_vehiculo = isset($_REQUEST['id_estado_vehiculo']) ? $_REQUEST['id_estado_vehiculo'] : '';

if (empty($placa) || empty($linea_vehiculo) || empty($modelo) || empty($id_color) || 
    empty($id_marca) || empty($id_tipo_servicio) || empty($id_estado_vehiculo)) {
    echo json_encode(array("success" => "0", "mensaje" => "Todos los campos son requeridos"));
} else {
    $placa = mysqli_real_escape_string($link, $placa);
    $linea_vehiculo = mysqli_real_escape_string($link, $linea_vehiculo);
    $modelo = mysqli_real_escape_string($link, $modelo);
    $id_color = mysqli_real_escape_string($link, $id_color);
    $id_marca = mysqli_real_escape_string($link, $id_marca);
    $id_tipo_servicio = mysqli_real_escape_string($link, $id_tipo_servicio);
    $id_estado_vehiculo = mysqli_real_escape_string($link, $id_estado_vehiculo);
    
    $sql = "INSERT INTO vehiculo (placa, linea_vehiculo, modelo, id_color, id_marca, id_tipo_servicio, id_estado_vehiculo) 
            VALUES ('$placa', '$linea_vehiculo', '$modelo', '$id_color', '$id_marca', '$id_tipo_servicio', '$id_estado_vehiculo')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Vehículo registrado correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

