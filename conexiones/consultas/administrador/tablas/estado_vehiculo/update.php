<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_estado_vehiculo = isset($input['id_estado_vehiculo']) ? trim($input['id_estado_vehiculo']) : '';
$descripcion = isset($input['descripcion']) ? trim($input['descripcion']) : '';

// La descripción de estado_vehiculo es de 100 caracteres según tu estructura.
if (empty($id_estado_vehiculo)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID de estado de vehículo requerido"));
} else if (empty($descripcion)) {
    echo json_encode(array("success" => "0", "mensaje" => "La descripción es requerida"));
} else {
    $id_estado_vehiculo = mysqli_real_escape_string($link, $id_estado_vehiculo);
    $descripcion = mysqli_real_escape_string($link, $descripcion);
    
    // Verificar si ya existe otro estado con la misma descripción (excluyendo el actual)
    $check_descripcion = "SELECT id_estado_vehiculo FROM estado_vehiculo 
                          WHERE descripcion = '$descripcion' AND id_estado_vehiculo != '$id_estado_vehiculo'";
    $check_res_desc = mysqli_query($link, $check_descripcion);
    
    if (mysqli_num_rows($check_res_desc) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "Ya existe otro estado con esa descripción"));
        exit;
    }
    
    $sql = "UPDATE estado_vehiculo SET 
            descripcion='$descripcion'
            WHERE id_estado_vehiculo='$id_estado_vehiculo'";
    
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        if (mysqli_affected_rows($link) > 0) {
            echo json_encode(array("success" => "1", "mensaje" => "Estado de vehículo actualizado correctamente"));
        } else {
            // Si la descripción es la misma, no hay filas afectadas.
            echo json_encode(array("success" => "1", "mensaje" => "No se realizaron cambios"));
        }
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
    }
}
mysqli_close($link);
?>