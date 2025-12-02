<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_tipo_servicio = isset($input['id_tipo_servicio']) ? trim($input['id_tipo_servicio']) : '';
$descripcion = isset($input['descripcion']) ? trim($input['descripcion']) : '';

if (empty($id_tipo_servicio)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID de Tipo de Servicio requerido"));
} else if (empty($descripcion)) {
    echo json_encode(array("success" => "0", "mensaje" => "La descripción es requerida"));
} else {
    // Escapar para prevenir inyección SQL
    $id_tipo_servicio = mysqli_real_escape_string($link, $id_tipo_servicio);
    $descripcion = mysqli_real_escape_string($link, $descripcion);
    
    // Verificar si ya existe otro Tipo de Servicio con la misma descripción (excluyendo el actual)
    $check_descripcion = "SELECT id_tipo_servicio FROM tipo_servicio 
                            WHERE descripcion = '$descripcion' AND id_tipo_servicio != '$id_tipo_servicio'";
    $check_res_desc = mysqli_query($link, $check_descripcion);
    
    if (mysqli_num_rows($check_res_desc) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "Ya existe otro Tipo de Servicio con esa descripción"));
        exit;
    }
    
    // Realizar la actualización
    $sql = "UPDATE tipo_servicio SET 
            descripcion='$descripcion'
            WHERE id_tipo_servicio='$id_tipo_servicio'";
    
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        if (mysqli_affected_rows($link) > 0) {
            echo json_encode(array("success" => "1", "mensaje" => "Tipo de Servicio actualizado correctamente"));
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "No se realizaron cambios o el ID no existe"));
        }
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
    }
}
mysqli_close($link);
?>