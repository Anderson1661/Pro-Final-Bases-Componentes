<?php
include '../conexion.php';

$id_linea_actual = $_POST['id_linea_actual'];
$id_marca = $_POST['id_marca'];
$id_linea_nueva = $_POST['id_linea_nueva'];

$sql = "UPDATE linea_vehiculo SET id_linea = '$id_linea_nueva' WHERE id_linea = '$id_linea_actual' AND id_marca = '$id_marca'";

if ($conexion->query($sql) === TRUE) {
    echo "Línea de vehículo actualizada exitosamente";
} else {
    echo "Error: " . $sql . "<br>" . $conexion->error;
}

$conexion->close();
?>