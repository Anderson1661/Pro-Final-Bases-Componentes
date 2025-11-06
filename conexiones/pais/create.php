<?php
include('../config/conexion.php');
$link = Conectar();

$nombre = isset($_REQUEST['nombre']) ? $_REQUEST['nombre'] : '';

if (empty($nombre)) {
    echo json_encode(array("success" => "0", "mensaje" => "El nombre es requerido"));
} else {
    $nombre = mysqli_real_escape_string($link, $nombre);
    
    $sql = "INSERT INTO pais (nombre) VALUES ('$nombre')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "País registrado correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

