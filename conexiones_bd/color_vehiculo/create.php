<?php
include '../conexion.php';

$descripcion = $_POST['descripcion'];

$sql = "INSERT INTO color_vehiculo (descripcion) VALUES ('$descripcion')";

if ($conexion->query($sql) === TRUE) {
    echo "Nuevo color de vehículo creado exitosamente";
} else {
    echo "Error: " . $sql . "<br>" . $conexion->error;
}

$conexion->close();
?>