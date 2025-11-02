<?php
include '../conexion.php';

$id_linea = $_POST['id_linea'];
$id_marca = $_POST['id_marca'];

$sql = "DELETE FROM linea_vehiculo WHERE id_linea = '$id_linea' AND id_marca = '$id_marca'";

if ($conexion->query($sql) === TRUE) {
    echo "Línea de vehículo eliminada exitosamente";
} else {
    echo "Error: " . $sql . "<br>" . $conexion->error;
}

$conexion->close();
?>