<?php
include('../config/conexion.php');
$link = Conectar();

$id_administrador = isset($_REQUEST['id_administrador']) ? $_REQUEST['id_administrador'] : '';
$telefono = isset($_REQUEST['telefono']) ? $_REQUEST['telefono'] : '';

if (empty($id_administrador) || empty($telefono)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID administrador y teléfono son requeridos"));
} else {
    $id_administrador = mysqli_real_escape_string($link, $id_administrador);
    $telefono = mysqli_real_escape_string($link, $telefono);
    
    $sql = "INSERT INTO telefono_administrador (id_administrador, telefono) VALUES ('$id_administrador', '$telefono')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Teléfono de administrador registrado correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

