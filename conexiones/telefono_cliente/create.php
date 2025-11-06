<?php
include('../config/conexion.php');
$link = Conectar();

$id_cliente = isset($_REQUEST['id_cliente']) ? $_REQUEST['id_cliente'] : '';
$telefono = isset($_REQUEST['telefono']) ? $_REQUEST['telefono'] : '';

if (empty($id_cliente) || empty($telefono)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID cliente y teléfono son requeridos"));
} else {
    $id_cliente = mysqli_real_escape_string($link, $id_cliente);
    $telefono = mysqli_real_escape_string($link, $telefono);
    
    $sql = "INSERT INTO telefono_cliente (id_cliente, telefono) VALUES ('$id_cliente', '$telefono')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Teléfono de cliente registrado correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

