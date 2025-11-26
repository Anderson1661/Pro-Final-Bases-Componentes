<?php
include('../config/conexion.php');
$link = Conectar();

$id_ruta = isset($_REQUEST['id_ruta']) ? $_REQUEST['id_ruta'] : '';
$nombre_pasajero = isset($_REQUEST['nombre_pasajero']) ? $_REQUEST['nombre_pasajero'] : '';

if (empty($id_ruta) || empty($nombre_pasajero)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID ruta y nombre pasajero son requeridos"));
} else {
    $id_ruta = mysqli_real_escape_string($link, $id_ruta);
    $nombre_pasajero = mysqli_real_escape_string($link, $nombre_pasajero);
    
    $sql = "INSERT INTO pasajero_ruta (id_ruta, nombre_pasajero) VALUES ('$id_ruta', '$nombre_pasajero')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Pasajero de ruta registrado correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

