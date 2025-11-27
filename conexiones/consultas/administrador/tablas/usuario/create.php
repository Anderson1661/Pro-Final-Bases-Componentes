<?php
include('../../../../config/conexion.php');
$link = Conectar();

$id_tipo_usuario = isset($_REQUEST['id_tipo_usuario']) ? $_REQUEST['id_tipo_usuario'] : '';
$correo = isset($_REQUEST['correo']) ? $_REQUEST['correo'] : '';
$contrasenia = isset($_REQUEST['contrasenia']) ? $_REQUEST['contrasenia'] : '';

if (empty($id_tipo_usuario) || empty($correo) || empty($contrasenia)) {
    echo json_encode(array("success" => "0", "mensaje" => "Todos los campos son requeridos"));
} else {
    $id_tipo_usuario = mysqli_real_escape_string($link, $id_tipo_usuario);
    $correo = mysqli_real_escape_string($link, $correo);
    $contrasenia = mysqli_real_escape_string($link, $contrasenia);
    
    $sql = "INSERT INTO usuario (id_tipo_usuario, correo, contrasenia) VALUES ('$id_tipo_usuario', '$correo', '$contrasenia')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Usuario registrado correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

