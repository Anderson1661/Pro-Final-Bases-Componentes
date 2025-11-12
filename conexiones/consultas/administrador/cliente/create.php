<?php
include('../config/conexion.php');
$link = Conectar();

$identificacion = isset($_REQUEST['identificacion']) ? $_REQUEST['identificacion'] : '';
$id_tipo_identificacion = isset($_REQUEST['id_tipo_identificacion']) ? $_REQUEST['id_tipo_identificacion'] : '';
$nombre = isset($_REQUEST['nombre']) ? $_REQUEST['nombre'] : '';
$direccion = isset($_REQUEST['direccion']) ? $_REQUEST['direccion'] : '';
$correo = isset($_REQUEST['correo']) ? $_REQUEST['correo'] : '';
$id_genero = isset($_REQUEST['id_genero']) ? $_REQUEST['id_genero'] : '';
$id_pais_nacionalidad = isset($_REQUEST['id_pais_nacionalidad']) ? $_REQUEST['id_pais_nacionalidad'] : '';
$codigo_postal = isset($_REQUEST['codigo_postal']) ? $_REQUEST['codigo_postal'] : '';

if (empty($identificacion) || empty($id_tipo_identificacion) || empty($nombre) || 
    empty($direccion) || empty($correo) || empty($id_genero) || 
    empty($id_pais_nacionalidad) || empty($codigo_postal)) {
    echo json_encode(array("success" => "0", "mensaje" => "Todos los campos son requeridos"));
} else {
    $identificacion = mysqli_real_escape_string($link, $identificacion);
    $id_tipo_identificacion = mysqli_real_escape_string($link, $id_tipo_identificacion);
    $nombre = mysqli_real_escape_string($link, $nombre);
    $direccion = mysqli_real_escape_string($link, $direccion);
    $correo = mysqli_real_escape_string($link, $correo);
    $id_genero = mysqli_real_escape_string($link, $id_genero);
    $id_pais_nacionalidad = mysqli_real_escape_string($link, $id_pais_nacionalidad);
    $codigo_postal = mysqli_real_escape_string($link, $codigo_postal);
    
    $sql = "INSERT INTO cliente (identificacion, id_tipo_identificacion, nombre, direccion, correo, id_genero, id_pais_nacionalidad, codigo_postal) 
            VALUES ('$identificacion', '$id_tipo_identificacion', '$nombre', '$direccion', '$correo', '$id_genero', '$id_pais_nacionalidad', '$codigo_postal')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Cliente registrado correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

