<?php
include '../conexion.php';

$placa = $_POST['placa'];
$id_linea = $_POST['id_linea'];
$id_marca = $_POST['id_marca'];
$modelo = $_POST['modelo'];
$id_color = $_POST['id_color'];
$id_tipo_servicio = $_POST['id_tipo_servicio'];
$id_estado_vehiculo = $_POST['id_estado_vehiculo'];

$sql = "INSERT INTO vehiculo (placa, id_linea, id_marca, modelo, id_color, id_tipo_servicio, id_estado_vehiculo) VALUES ('$placa', '$id_linea', '$id_marca', '$modelo', '$id_color', '$id_tipo_servicio', '$id_estado_vehiculo')";

if ($conexion->query($sql) === TRUE) {
    echo "Nuevo vehículo creado exitosamente";
} else {
    echo "Error: " . $sql . "<br>" . $conexion->error;
}

$conexion->close();
?>