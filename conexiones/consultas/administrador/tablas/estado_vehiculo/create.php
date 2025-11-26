<?php
include('../config/conexion.php');
$link = Conectar();

$descripcion = isset($_REQUEST['descripcion']) ? $_REQUEST['descripcion'] : '';

if (empty($descripcion)) {
    echo json_encode(array("success" => "0", "mensaje" => "La descripción es requerida"));
} else {
    $descripcion = mysqli_real_escape_string($link, $descripcion);
    
    $sql = "INSERT INTO estado_vehiculo (descripcion) VALUES ('$descripcion')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Estado de vehículo registrado correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

