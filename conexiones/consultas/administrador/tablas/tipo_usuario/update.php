<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_tipo_usuario = isset($input['id_tipo_usuario']) ? trim($input['id_tipo_usuario']) : '';
$descripcion = isset($input['descripcion']) ? trim($input['descripcion']) : '';

if (empty($id_tipo_usuario)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID de Tipo de Usuario requerido"));
} else if (empty($descripcion)) {
    echo json_encode(array("success" => "0", "mensaje" => "La descripción es requerida"));
} else {
    // Escapar para prevenir inyección SQL
    $id_tipo_usuario = mysqli_real_escape_string($link, $id_tipo_usuario);
    $descripcion = mysqli_real_escape_string($link, $descripcion);
    
    // Verificar si ya existe otro Tipo de Usuario con la misma descripción (excluyendo el actual)
    $check_descripcion = "SELECT id_tipo_usuario FROM tipo_usuario 
                            WHERE descripcion = '$descripcion' AND id_tipo_usuario != '$id_tipo_usuario'";
    $check_res_desc = mysqli_query($link, $check_descripcion);
    
    if (mysqli_num_rows($check_res_desc) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "Ya existe otro Tipo de Usuario con esa descripción"));
        exit;
    }
    
    // Realizar la actualización
    $sql = "UPDATE tipo_usuario SET 
            descripcion='$descripcion'
            WHERE id_tipo_usuario='$id_tipo_usuario'";
    
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        if (mysqli_affected_rows($link) > 0) {
            echo json_encode(array("success" => "1", "mensaje" => "Tipo de Usuario actualizado correctamente"));
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "No se realizaron cambios o el ID no existe"));
        }
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
    }
}
mysqli_close($link);
?>