<?php
include '../conexion.php';

$sql = "SELECT id_linea, id_marca FROM linea_vehiculo";
$result = $conexion->query($sql);

if ($result->num_rows > 0) {
    $lineas = array();
    while($row = $result->fetch_assoc()) {
        $lineas[] = $row;
    }
    echo json_encode($lineas);
} else {
    echo "0 results";
}

$conexion->close();
?>