<?php
include '../conexion.php';

$id_linea = $_POST['id_linea'];
$id_marca = $_POST['id_marca'];

$sql = "INSERT INTO linea_vehiculo (id_linea, id_marca) VALUES ('$id_linea', '$id_marca')";

if ($conexion->query($sql) === TRUE) {
    echo "Nueva línea de vehículo creada exitosamente";
} else {
    echo "Error: " . $sql . "<br>" . $conexion->error;
}

$conexion->close();
?>