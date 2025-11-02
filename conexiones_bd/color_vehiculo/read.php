<?php
include '../conexion.php';

$sql = "SELECT id_color, descripcion FROM color_vehiculo";
$result = $conexion->query($sql);

if ($result->num_rows > 0) {
    $colores = array();
    while($row = $result->fetch_assoc()) {
        $colores[] = $row;
    }
    echo json_encode($colores);
} else {
    echo "0 results";
}

$conexion->close();
?>