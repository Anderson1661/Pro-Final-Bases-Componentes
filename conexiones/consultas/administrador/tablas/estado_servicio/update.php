<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_estado_servicio = isset($input['id_estado_servicio']) ? trim($input['id_estado_servicio']) : '';
$descripcion = isset($input['descripcion']) ? trim($input['descripcion']) : '';

if (empty($id_estado_servicio)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID de estado de servicio requerido"));
} else if (empty($descripcion)) {
    echo json_encode(array("success" => "0", "mensaje" => "La descripción es requerida"));
} else {
    $id_estado_servicio = mysqli_real_escape_string($link, $id_estado_servicio);
    $descripcion = mysqli_real_escape_string($link, $descripcion);
    
    // Verificar si ya existe otro estado con la misma descripción (excluyendo el actual)
    $check_descripcion = "SELECT id_estado_servicio FROM estado_servicio 
                          WHERE descripcion = '$descripcion' AND id_estado_servicio != '$id_estado_servicio'";
    $check_res_desc = mysqli_query($link, $check_descripcion);
    
    if (mysqli_num_rows($check_res_desc) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "Ya existe otro estado con esa descripción"));
        exit;
    }
    
    $sql = "UPDATE estado_servicio SET 
            descripcion='$descripcion'
            WHERE id_estado_servicio='$id_estado_servicio'";
    
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        if (mysqli_affected_rows($link) > 0) {
            echo json_encode(array("success" => "1", "mensaje" => "Estado de servicio actualizado correctamente"));
        } else {
            // Si la descripción es la misma, no hay filas afectadas, pero es un éxito funcional.
            echo json_encode(array("success" => "1", "mensaje" => "No se realizaron cambios"));
        }
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
    }
}
mysqli_close($link);
?>