<?php
include('../../../../config/conexion.php');
$link = Conectar();

$descripcion = isset($_REQUEST['descripcion']) ? $_REQUEST['descripcion'] : '';
$valor_km = isset($_REQUEST['valor_km']) ? $_REQUEST['valor_km'] : '';

if (empty($descripcion) || empty($valor_km)) {
    echo json_encode(array("success" => "0", "mensaje" => "La descripción y el valor por km son requeridos"));
} else {
    $descripcion = mysqli_real_escape_string($link, $descripcion);
    $valor_km = mysqli_real_escape_string($link, $valor_km);
    
    $sql = "INSERT INTO categoria_servicio (descripcion, valor_km) VALUES ('$descripcion', '$valor_km')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Categoría de servicio registrada correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

