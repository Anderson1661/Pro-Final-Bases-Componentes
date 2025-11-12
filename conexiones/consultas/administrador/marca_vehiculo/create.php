<?php
include('../config/conexion.php');
$link = Conectar();

$nombre_marca = isset($_REQUEST['nombre_marca']) ? $_REQUEST['nombre_marca'] : '';

if (empty($nombre_marca)) {
    echo json_encode(array("success" => "0", "mensaje" => "El nombre de la marca es requerido"));
} else {
    $nombre_marca = mysqli_real_escape_string($link, $nombre_marca);
    
    $sql = "INSERT INTO marca_vehiculo (nombre_marca) VALUES ('$nombre_marca')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Marca de vehículo registrada correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

