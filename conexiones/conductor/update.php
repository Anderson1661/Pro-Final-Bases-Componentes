<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_conductor = isset($_REQUEST['id_conductor']) ? $_REQUEST['id_conductor'] : '';
    $placa_vehiculo = isset($_REQUEST['placa_vehiculo']) ? $_REQUEST['placa_vehiculo'] : '';
    $identificacion = isset($_REQUEST['identificacion']) ? $_REQUEST['identificacion'] : '';
    $id_tipo_identificacion = isset($_REQUEST['id_tipo_identificacion']) ? $_REQUEST['id_tipo_identificacion'] : '';
    $nombre = isset($_REQUEST['nombre']) ? $_REQUEST['nombre'] : '';
    $direccion = isset($_REQUEST['direccion']) ? $_REQUEST['direccion'] : '';
    $correo = isset($_REQUEST['correo']) ? $_REQUEST['correo'] : '';
    $id_genero = isset($_REQUEST['id_genero']) ? $_REQUEST['id_genero'] : '';
    $codigo_postal = isset($_REQUEST['codigo_postal']) ? $_REQUEST['codigo_postal'] : '';
    $id_pais_nacionalidad = isset($_REQUEST['id_pais_nacionalidad']) ? $_REQUEST['id_pais_nacionalidad'] : '';
    $url_foto = isset($_REQUEST['url_foto']) ? $_REQUEST['url_foto'] : '';
    
    if (empty($id_conductor)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
    } else if (empty($placa_vehiculo) || empty($identificacion) || empty($id_tipo_identificacion) || 
               empty($nombre) || empty($direccion) || empty($correo) || empty($id_genero) || 
               empty($codigo_postal) || empty($id_pais_nacionalidad) || empty($url_foto)) {
        echo json_encode(array("success" => "0", "mensaje" => "Todos los campos son requeridos"));
    } else {
        $id_conductor = mysqli_real_escape_string($link, $id_conductor);
        $placa_vehiculo = mysqli_real_escape_string($link, $placa_vehiculo);
        $identificacion = mysqli_real_escape_string($link, $identificacion);
        $id_tipo_identificacion = mysqli_real_escape_string($link, $id_tipo_identificacion);
        $nombre = mysqli_real_escape_string($link, $nombre);
        $direccion = mysqli_real_escape_string($link, $direccion);
        $correo = mysqli_real_escape_string($link, $correo);
        $id_genero = mysqli_real_escape_string($link, $id_genero);
        $codigo_postal = mysqli_real_escape_string($link, $codigo_postal);
        $id_pais_nacionalidad = mysqli_real_escape_string($link, $id_pais_nacionalidad);
        $url_foto = mysqli_real_escape_string($link, $url_foto);
        
        $sql = "UPDATE conductor SET placa_vehiculo='$placa_vehiculo', identificacion='$identificacion', 
                id_tipo_identificacion='$id_tipo_identificacion', nombre='$nombre', direccion='$direccion', 
                correo='$correo', id_genero='$id_genero', codigo_postal='$codigo_postal', 
                id_pais_nacionalidad='$id_pais_nacionalidad', url_foto='$url_foto' 
                WHERE id_conductor='$id_conductor'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Conductor actualizado correctamente"));
            } else {
                echo json_encode(array("success" => "0", "mensaje" => "No se encontró el registro"));
            }
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
        }
    }
    mysqli_close($link);
} else {
    echo json_encode(array("success" => "0", "mensaje" => "Método no permitido"));
}
?>

