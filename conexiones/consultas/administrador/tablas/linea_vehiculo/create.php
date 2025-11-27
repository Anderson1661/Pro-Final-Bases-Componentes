<?php
include('../../../../config/conexion.php');
$link = Conectar();

$id_linea = isset($_REQUEST['id_linea']) ? $_REQUEST['id_linea'] : '';
$id_marca = isset($_REQUEST['id_marca']) ? $_REQUEST['id_marca'] : '';

if (empty($id_linea) || empty($id_marca)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID línea e ID marca son requeridos"));
} else {
    $id_linea = mysqli_real_escape_string($link, $id_linea);
    $id_marca = mysqli_real_escape_string($link, $id_marca);
    
    $sql = "INSERT INTO linea_vehiculo (id_linea, id_marca) VALUES ('$id_linea', '$id_marca')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Línea de vehículo registrada correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

