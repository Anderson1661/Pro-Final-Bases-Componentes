<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_conductor = isset($input['id_conductor']) ? trim($input['id_conductor']) : '';
$id_estado_conductor = isset($input['id_estado_conductor']) ? trim($input['id_estado_conductor']) : '';
$placa_vehiculo = isset($input['placa_vehiculo']) ? trim($input['placa_vehiculo']) : '';
$identificacion = isset($input['identificacion']) ? trim($input['identificacion']) : '';
$id_tipo_identificacion = isset($input['id_tipo_identificacion']) ? trim($input['id_tipo_identificacion']) : '';
$nombre = isset($input['nombre']) ? trim($input['nombre']) : '';
$direccion = isset($input['direccion']) ? trim($input['direccion']) : '';
$correo = isset($input['correo']) ? trim($input['correo']) : '';
$id_genero = isset($input['id_genero']) ? trim($input['id_genero']) : '';
$codigo_postal = isset($input['codigo_postal']) ? trim($input['codigo_postal']) : '';
$id_pais_nacionalidad = isset($input['id_pais_nacionalidad']) ? trim($input['id_pais_nacionalidad']) : '';
$url_foto = isset($input['url_foto']) ? trim($input['url_foto']) : '';

if (empty($id_conductor)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
} else if (empty($id_estado_conductor) || empty($placa_vehiculo) || empty($identificacion) || 
           empty($id_tipo_identificacion) || empty($nombre) || empty($direccion) || 
           empty($correo) || empty($id_genero) || empty($codigo_postal) || 
           empty($id_pais_nacionalidad) || empty($url_foto)) {
    echo json_encode(array("success" => "0", "mensaje" => "Todos los campos son requeridos"));
} else {
    $id_conductor = mysqli_real_escape_string($link, $id_conductor);
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
    
    // Validaciones de longitud
    if (strlen($placa_vehiculo) > 20) {
        echo json_encode(array("success" => "0", "mensaje" => "La placa no debe exceder 20 caracteres"));
        exit;
    }
    
    if (strlen($identificacion) > 20) {
        echo json_encode(array("success" => "0", "mensaje" => "La identificación no debe exceder 20 caracteres"));
        exit;
    }
    
    if (strlen($nombre) > 100) {
        echo json_encode(array("success" => "0", "mensaje" => "El nombre no debe exceder 100 caracteres"));
        exit;
    }
    
    if (strlen($direccion) > 100) {
        echo json_encode(array("success" => "0", "mensaje" => "La dirección no debe exceder 100 caracteres"));
        exit;
    }
    
    if (strlen($correo) > 100) {
        echo json_encode(array("success" => "0", "mensaje" => "El correo no debe exceder 100 caracteres"));
        exit;
    }
    
    if (strlen($codigo_postal) > 10) {
        echo json_encode(array("success" => "0", "mensaje" => "El código postal no debe exceder 10 caracteres"));
        exit;
    }
    
    if (strlen($url_foto) > 255) {
        echo json_encode(array("success" => "0", "mensaje" => "La URL de la foto no debe exceder 255 caracteres"));
        exit;
    }
    
    // Verificar si ya existe otro conductor con la misma identificación
    $check_identificacion = "SELECT id_conductor FROM conductor 
                            WHERE identificacion = '$identificacion' AND id_conductor != '$id_conductor'";
    $check_res_id = mysqli_query($link, $check_identificacion);
    
    if (mysqli_num_rows($check_res_id) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "Ya existe otro conductor con esa identificación"));
        exit;
    }
    
    // Verificar si ya existe otro conductor con el mismo correo
    $check_correo = "SELECT id_conductor FROM conductor 
                    WHERE correo = '$correo' AND id_conductor != '$id_conductor'";
    $check_res_correo = mysqli_query($link, $check_correo);
    
    if (mysqli_num_rows($check_res_correo) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "Ya existe otro conductor con ese correo"));
        exit;
    }
    
    // Verificar si ya existe otro conductor con la misma placa
    $check_placa = "SELECT id_conductor FROM conductor 
                   WHERE placa_vehiculo = '$placa_vehiculo' AND id_conductor != '$id_conductor'";
    $check_res_placa = mysqli_query($link, $check_placa);
    
    if (mysqli_num_rows($check_res_placa) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "Ya existe otro conductor con esa placa de vehículo"));
        exit;
    }
    
    // Verificar que las FK existan
    $check_estado = "SELECT id_estado_conductor FROM estado_conductor WHERE id_estado_conductor = '$id_estado_conductor'";
    $check_vehiculo = "SELECT placa FROM vehiculo WHERE placa = '$placa_vehiculo'";
    $check_tipo_id = "SELECT id_tipo_identificacion FROM tipo_identificacion WHERE id_tipo_identificacion = '$id_tipo_identificacion'";
    $check_genero = "SELECT id_genero FROM genero WHERE id_genero = '$id_genero'";
    $check_codigo_postal = "SELECT id_codigo_postal FROM codigo_postal WHERE id_codigo_postal = '$codigo_postal'";
    $check_pais = "SELECT id_pais FROM pais WHERE id_pais = '$id_pais_nacionalidad'";
    
    if (mysqli_num_rows(mysqli_query($link, $check_estado)) == 0) {
        echo json_encode(array("success" => "0", "mensaje" => "El estado del conductor no existe"));
        exit;
    }
    
    if (mysqli_num_rows(mysqli_query($link, $check_vehiculo)) == 0) {
        echo json_encode(array("success" => "0", "mensaje" => "El vehículo con esa placa no existe"));
        exit;
    }
    
    if (mysqli_num_rows(mysqli_query($link, $check_tipo_id)) == 0) {
        echo json_encode(array("success" => "0", "mensaje" => "El tipo de identificación no existe"));
        exit;
    }
    
    if (mysqli_num_rows(mysqli_query($link, $check_genero)) == 0) {
        echo json_encode(array("success" => "0", "mensaje" => "El género no existe"));
        exit;
    }
    
    if (mysqli_num_rows(mysqli_query($link, $check_codigo_postal)) == 0) {
        echo json_encode(array("success" => "0", "mensaje" => "El código postal no existe"));
        exit;
    }
    
    if (mysqli_num_rows(mysqli_query($link, $check_pais)) == 0) {
        echo json_encode(array("success" => "0", "mensaje" => "El país no existe"));
        exit;
    }
    
    $sql = "UPDATE conductor SET 
            id_estado_conductor='$id_estado_conductor', 
            placa_vehiculo='$placa_vehiculo', 
            identificacion='$identificacion', 
            id_tipo_identificacion='$id_tipo_identificacion', 
            nombre='$nombre', 
            direccion='$direccion', 
            correo='$correo', 
            id_genero='$id_genero', 
            codigo_postal='$codigo_postal', 
            id_pais_nacionalidad='$id_pais_nacionalidad', 
            url_foto='$url_foto' 
            WHERE id_conductor='$id_conductor'";
    
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        if (mysqli_affected_rows($link) > 0) {
            echo json_encode(array("success" => "1", "mensaje" => "Conductor actualizado correctamente"));
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "No se realizaron cambios"));
        }
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
    }
}
mysqli_close($link);
?>