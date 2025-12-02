<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_cliente = isset($input['id_cliente']) ? trim($input['id_cliente']) : '';
$identificacion = isset($input['identificacion']) ? trim($input['identificacion']) : '';
$id_tipo_identificacion = isset($input['id_tipo_identificacion']) ? trim($input['id_tipo_identificacion']) : '';
$nombre = isset($input['nombre']) ? trim($input['nombre']) : '';
$direccion = isset($input['direccion']) ? trim($input['direccion']) : '';
$correo = isset($input['correo']) ? trim($input['correo']) : '';
$id_genero = isset($input['id_genero']) ? trim($input['id_genero']) : '';
$id_pais_nacionalidad = isset($input['id_pais_nacionalidad']) ? trim($input['id_pais_nacionalidad']) : '';
$codigo_postal = isset($input['codigo_postal']) ? trim($input['codigo_postal']) : '';

if (empty($id_cliente)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
} else if (empty($identificacion) || empty($id_tipo_identificacion) || empty($nombre) || 
           empty($direccion) || empty($correo) || empty($id_genero) || 
           empty($id_pais_nacionalidad) || empty($codigo_postal)) {
    echo json_encode(array("success" => "0", "mensaje" => "Todos los campos son requeridos"));
} else {
    $id_cliente = mysqli_real_escape_string($link, $id_cliente);
    $identificacion = mysqli_real_escape_string($link, $identificacion);
    $id_tipo_identificacion = mysqli_real_escape_string($link, $id_tipo_identificacion);
    $nombre = mysqli_real_escape_string($link, $nombre);
    $direccion = mysqli_real_escape_string($link, $direccion);
    $correo = mysqli_real_escape_string($link, $correo);
    $id_genero = mysqli_real_escape_string($link, $id_genero);
    $id_pais_nacionalidad = mysqli_real_escape_string($link, $id_pais_nacionalidad);
    $codigo_postal = mysqli_real_escape_string($link, $codigo_postal);
    
    // Verificar si ya existe otro cliente con la misma identificación
    $check_identificacion = "SELECT id_cliente FROM cliente 
                            WHERE identificacion = '$identificacion' AND id_cliente != '$id_cliente'";
    $check_res_id = mysqli_query($link, $check_identificacion);
    
    if (mysqli_num_rows($check_res_id) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "Ya existe otro cliente con esa identificación"));
        exit;
    }
    
    // Verificar si ya existe otro cliente con el mismo correo
    $check_correo = "SELECT id_cliente FROM cliente 
                    WHERE correo = '$correo' AND id_cliente != '$id_cliente'";
    $check_res_correo = mysqli_query($link, $check_correo);
    
    if (mysqli_num_rows($check_res_correo) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "Ya existe otro cliente con ese correo"));
        exit;
    }
    
    // Verificar que las FK existan
    $check_tipo_id = "SELECT id_tipo_identificacion FROM tipo_identificacion WHERE id_tipo_identificacion = '$id_tipo_identificacion'";
    $check_genero = "SELECT id_genero FROM genero WHERE id_genero = '$id_genero'";
    $check_pais = "SELECT id_pais FROM pais WHERE id_pais = '$id_pais_nacionalidad'";
    $check_codigo_postal = "SELECT id_codigo_postal FROM codigo_postal WHERE id_codigo_postal = '$codigo_postal'";
    
    if (mysqli_num_rows(mysqli_query($link, $check_tipo_id)) == 0) {
        echo json_encode(array("success" => "0", "mensaje" => "El tipo de identificación no existe"));
        exit;
    }
    
    if (mysqli_num_rows(mysqli_query($link, $check_genero)) == 0) {
        echo json_encode(array("success" => "0", "mensaje" => "El género no existe"));
        exit;
    }
    
    if (mysqli_num_rows(mysqli_query($link, $check_pais)) == 0) {
        echo json_encode(array("success" => "0", "mensaje" => "El país no existe"));
        exit;
    }
    
    if (mysqli_num_rows(mysqli_query($link, $check_codigo_postal)) == 0) {
        echo json_encode(array("success" => "0", "mensaje" => "El código postal no existe"));
        exit;
    }
    
    $sql = "UPDATE cliente SET 
            identificacion='$identificacion', 
            id_tipo_identificacion='$id_tipo_identificacion', 
            nombre='$nombre', 
            direccion='$direccion', 
            correo='$correo', 
            id_genero='$id_genero', 
            id_pais_nacionalidad='$id_pais_nacionalidad', 
            codigo_postal='$codigo_postal' 
            WHERE id_cliente='$id_cliente'";
    
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        if (mysqli_affected_rows($link) > 0) {
            echo json_encode(array("success" => "1", "mensaje" => "Cliente actualizado correctamente"));
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "No se realizaron cambios"));
        }
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
    }
}
mysqli_close($link);
?>