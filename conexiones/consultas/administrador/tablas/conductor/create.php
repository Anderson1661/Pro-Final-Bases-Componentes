<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer el input JSON
$input = json_decode(file_get_contents('php://input'), true);

// Verificar si se recibieron datos JSON
if (json_last_error() !== JSON_ERROR_NONE) {
    echo json_encode(array("success" => "0", "mensaje" => "Error en el formato JSON: " . json_last_error_msg()));
    exit;
}

// Obtener datos del JSON
$id_estado_conductor = isset($input['id_estado_conductor']) ? $input['id_estado_conductor'] : '';
$placa_vehiculo = isset($input['placa_vehiculo']) ? $input['placa_vehiculo'] : '';
$identificacion = isset($input['identificacion']) ? $input['identificacion'] : '';
$id_tipo_identificacion = isset($input['id_tipo_identificacion']) ? $input['id_tipo_identificacion'] : '';
$nombre = isset($input['nombre']) ? $input['nombre'] : '';
$direccion = isset($input['direccion']) ? $input['direccion'] : '';
$correo = isset($input['correo']) ? $input['correo'] : '';
$id_genero = isset($input['id_genero']) ? $input['id_genero'] : '';
$codigo_postal = isset($input['codigo_postal']) ? $input['codigo_postal'] : '';
$id_pais_nacionalidad = isset($input['id_pais_nacionalidad']) ? $input['id_pais_nacionalidad'] : '';
$url_foto = isset($input['url_foto']) ? $input['url_foto'] : '';

// Validar campos requeridos
if (empty($id_estado_conductor) || empty($placa_vehiculo) || empty($identificacion) || empty($id_tipo_identificacion) || 
    empty($nombre) || empty($direccion) || empty($correo) || empty($id_genero) || 
    empty($codigo_postal) || empty($id_pais_nacionalidad) || empty($url_foto)) {
    echo json_encode(array("success" => "0", "mensaje" => "Todos los campos son requeridos"));
} else {
    // Limpiar y escapar datos
    $id_estado_conductor = mysqli_real_escape_string($link, $id_estado_conductor);
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
    
    // Preparar y ejecutar la consulta
    $sql = "INSERT INTO conductor (id_estado_conductor, placa_vehiculo, identificacion, id_tipo_identificacion, nombre, direccion, correo, id_genero, codigo_postal, id_pais_nacionalidad, url_foto) 
            VALUES ('$id_estado_conductor', '$placa_vehiculo', '$identificacion', '$id_tipo_identificacion', '$nombre', '$direccion', '$correo', '$id_genero', '$codigo_postal', '$id_pais_nacionalidad', '$url_foto')";
    
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Conductor registrado correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>