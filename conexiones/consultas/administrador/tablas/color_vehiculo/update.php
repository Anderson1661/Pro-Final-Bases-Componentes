<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_color = isset($input['id_color']) ? trim($input['id_color']) : '';
$descripcion = isset($input['descripcion']) ? trim($input['descripcion']) : '';

if (empty($id_color)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
} else if (empty($descripcion)) {
    echo json_encode(array("success" => "0", "mensaje" => "La descripción es requerida"));
} else {
    $id_color = mysqli_real_escape_string($link, $id_color);
    $descripcion = mysqli_real_escape_string($link, $descripcion);
    
    // Verificar longitud máxima
    if (strlen($descripcion) > 30) {
        echo json_encode(array("success" => "0", "mensaje" => "La descripción no debe exceder 30 caracteres"));
        exit;
    }
    
    // Verificar si ya existe otro color con la misma descripción
    $check_existente = "SELECT id_color FROM color_vehiculo 
                       WHERE descripcion = '$descripcion' 
                       AND id_color != '$id_color'";
    $check_res = mysqli_query($link, $check_existente);
    
    if (mysqli_num_rows($check_res) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "Ya existe un color con esa descripción"));
        exit;
    }
    
    $sql = "UPDATE color_vehiculo SET 
            descripcion='$descripcion' 
            WHERE id_color='$id_color'";
    
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        if (mysqli_affected_rows($link) > 0) {
            echo json_encode(array("success" => "1", "mensaje" => "Color de vehículo actualizado correctamente"));
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "No se realizaron cambios"));
        }
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
    }
}
mysqli_close($link);
?>