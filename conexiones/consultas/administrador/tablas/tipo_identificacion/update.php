<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_tipo_identificacion = isset($input['id_tipo_identificacion']) ? trim($input['id_tipo_identificacion']) : '';
$descripcion = isset($input['descripcion']) ? trim($input['descripcion']) : '';

if (empty($id_tipo_identificacion)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID de Tipo de Identificación requerido"));
} else if (empty($descripcion)) {
    echo json_encode(array("success" => "0", "mensaje" => "La descripción es requerida"));
} else {
    // Escapar para prevenir inyección SQL
    $id_tipo_identificacion = mysqli_real_escape_string($link, $id_tipo_identificacion);
    $descripcion = mysqli_real_escape_string($link, $descripcion);
    
    // Verificar si ya existe otro Tipo de Identificación con la misma descripción (excluyendo el actual)
    $check_descripcion = "SELECT id_tipo_identificacion FROM tipo_identificacion 
                            WHERE descripcion = '$descripcion' AND id_tipo_identificacion != '$id_tipo_identificacion'";
    $check_res_desc = mysqli_query($link, $check_descripcion);
    
    if (mysqli_num_rows($check_res_desc) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "Ya existe otro Tipo de Identificación con esa descripción"));
        exit;
    }
    
    // Realizar la actualización
    $sql = "UPDATE tipo_identificacion SET 
            descripcion='$descripcion'
            WHERE id_tipo_identificacion='$id_tipo_identificacion'";
    
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        if (mysqli_affected_rows($link) > 0) {
            echo json_encode(array("success" => "1", "mensaje" => "Tipo de Identificación actualizado correctamente"));
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "No se realizaron cambios o el ID no existe"));
        }
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
    }
}
mysqli_close($link);
?>