<?php
include '../conexion.php';

$placa = $_POST['placa'];
$id_linea = $_POST['id_linea'];
$id_marca = $_POST['id_marca'];
$modelo = $_POST['modelo'];
$id_color = $_POST['id_color'];
$id_tipo_servicio = $_POST['id_tipo_servicio'];
$id_estado_vehiculo = $_POST['id_estado_vehiculo'];

$sql = "UPDATE vehiculo SET id_linea = '$id_linea', id_marca = '$id_marca', modelo = '$modelo', id_color = '$id_color', id_tipo_servicio = '$id_tipo_servicio', id_estado_vehiculo = '$id_estado_vehiculo' WHERE placa = '$placa'";

if ($conexion->query($sql) === TRUE) {
    echo "Vehículo actualizado exitosamente";
} else {
    echo "Error: " . $sql . "<br>" . $conexion->error;
}

$conexion->close();
?>