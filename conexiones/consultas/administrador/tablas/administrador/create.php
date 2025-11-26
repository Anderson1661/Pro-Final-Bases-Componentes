<?php
include('../config/conexion.php');
$link = Conectar();

$id_administrador = isset($_REQUEST['id_administrador']) ? $_REQUEST['id_administrador'] : '';
$identificacion = isset($_REQUEST['identificacion']) ? $_REQUEST['identificacion'] : '';
$id_tipo_identificacion = isset($_REQUEST['id_tipo_identificacion']) ? $_REQUEST['id_tipo_identificacion'] : '';
$nombre = isset($_REQUEST['nombre']) ? $_REQUEST['nombre'] : '';
$direccion = isset($_REQUEST['direccion']) ? $_REQUEST['direccion'] : '';
$correo = isset($_REQUEST['correo']) ? $_REQUEST['correo'] : '';
$id_genero = isset($_REQUEST['id_genero']) ? $_REQUEST['id_genero'] : '';
$codigo_postal = isset($_REQUEST['codigo_postal']) ? $_REQUEST['codigo_postal'] : '';

if (empty($id_administrador) || empty($identificacion) || empty($id_tipo_identificacion) || 
    empty($nombre) || empty($direccion) || empty($correo) || empty($id_genero) || empty($codigo_postal)) {
    echo json_encode(array("success" => "0", "mensaje" => "Todos los campos son requeridos"));
} else {
    $id_administrador = mysqli_real_escape_string($link, $id_administrador);
    $identificacion = mysqli_real_escape_string($link, $identificacion);
    $id_tipo_identificacion = mysqli_real_escape_string($link, $id_tipo_identificacion);
    $nombre = mysqli_real_escape_string($link, $nombre);
    $direccion = mysqli_real_escape_string($link, $direccion);
    $correo = mysqli_real_escape_string($link, $correo);
    $id_genero = mysqli_real_escape_string($link, $id_genero);
    $codigo_postal = mysqli_real_escape_string($link, $codigo_postal);
    
    $sql = "INSERT INTO administrador (id_administrador, identificacion, id_tipo_identificacion, nombre, direccion, correo, id_genero, codigo_postal) 
            VALUES ('$id_administrador', '$identificacion', '$id_tipo_identificacion', '$nombre', '$direccion', '$correo', '$id_genero', '$codigo_postal')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Administrador registrado correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

