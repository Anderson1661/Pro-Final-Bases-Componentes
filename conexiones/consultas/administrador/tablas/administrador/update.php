<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_administrador = isset($input['id_administrador']) ? trim($input['id_administrador']) : '';
$identificacion = isset($input['identificacion']) ? trim($input['identificacion']) : '';
$id_tipo_identificacion = isset($input['id_tipo_identificacion']) ? trim($input['id_tipo_identificacion']) : '';
$nombre = isset($input['nombre']) ? trim($input['nombre']) : '';
$direccion = isset($input['direccion']) ? trim($input['direccion']) : '';
$correo = isset($input['correo']) ? trim($input['correo']) : '';
$id_genero = isset($input['id_genero']) ? trim($input['id_genero']) : '';
$codigo_postal = isset($input['codigo_postal']) ? trim($input['codigo_postal']) : '';

if (empty($id_administrador)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
} else if (empty($identificacion) || empty($id_tipo_identificacion) || empty($nombre) || 
           empty($direccion) || empty($correo) || empty($id_genero) || empty($codigo_postal)) {
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
    
    // Verificar si ya existe otro administrador con la misma identificación
    $check_identificacion = "SELECT id_administrador FROM administrador 
                            WHERE identificacion = '$identificacion' AND id_administrador != '$id_administrador'";
    $check_res_id = mysqli_query($link, $check_identificacion);
    
    if (mysqli_num_rows($check_res_id) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "Ya existe otro administrador con esa identificación"));
        exit;
    }
    
    // Verificar si ya existe otro administrador con el mismo correo
    $check_correo = "SELECT id_administrador FROM administrador 
                    WHERE correo = '$correo' AND id_administrador != '$id_administrador'";
    $check_res_correo = mysqli_query($link, $check_correo);
    
    if (mysqli_num_rows($check_res_correo) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "Ya existe otro administrador con ese correo"));
        exit;
    }
    
    // Verificar que las FK existan
    $check_tipo_id = "SELECT id_tipo_identificacion FROM tipo_identificacion WHERE id_tipo_identificacion = '$id_tipo_identificacion'";
    $check_genero = "SELECT id_genero FROM genero WHERE id_genero = '$id_genero'";
    $check_codigo_postal = "SELECT id_codigo_postal FROM codigo_postal WHERE id_codigo_postal = '$codigo_postal'";
    
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
    
    $sql = "UPDATE administrador SET 
            identificacion='$identificacion', 
            id_tipo_identificacion='$id_tipo_identificacion', 
            nombre='$nombre', 
            direccion='$direccion', 
            correo='$correo', 
            id_genero='$id_genero', 
            codigo_postal='$codigo_postal' 
            WHERE id_administrador='$id_administrador'";
    
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        if (mysqli_affected_rows($link) > 0) {
            echo json_encode(array("success" => "1", "mensaje" => "Administrador actualizado correctamente"));
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "No se realizaron cambios"));
        }
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
    }
}
mysqli_close($link);
?>