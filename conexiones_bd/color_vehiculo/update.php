<?php
include '../conexion.php';

$id_color = $_POST['id_color'];
$descripcion = $_POST['descripcion'];

$sql = "UPDATE color_vehiculo SET descripcion = '$descripcion' WHERE id_color = '$id_color'";

if ($conexion->query($sql) === TRUE) {
    echo "Color de vehículo actualizado exitosamente";
} else {
    echo "Error: " . $sql . "<br>" . $conexion->error;
}

$conexion->close();
?>