<?php
include('../config/conexion.php');
$link = Conectar();

$id_conductor = isset($_REQUEST['id_conductor']) ? $_REQUEST['id_conductor'] : '';
$telefono = isset($_REQUEST['telefono']) ? $_REQUEST['telefono'] : '';

if (empty($id_conductor) || empty($telefono)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID conductor y teléfono son requeridos"));
} else {
    $id_conductor = mysqli_real_escape_string($link, $id_conductor);
    $telefono = mysqli_real_escape_string($link, $telefono);
    
    $sql = "INSERT INTO telefono_conductor (id_conductor, telefono) VALUES ('$id_conductor', '$telefono')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Teléfono de conductor registrado correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

