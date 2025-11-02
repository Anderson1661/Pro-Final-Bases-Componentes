<?php
include '../conexion.php';

$id_color = $_POST['id_color'];

$sql = "DELETE FROM color_vehiculo WHERE id_color = '$id_color'";

if ($conexion->query($sql) === TRUE) {
    echo "Color de vehículo eliminado exitosamente";
} else {
    echo "Error: " . $sql . "<br>" . $conexion->error;
}

$conexion->close();
?>