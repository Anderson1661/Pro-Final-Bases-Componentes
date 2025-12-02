<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_tipo_servicio = isset($input['id_tipo_servicio']) ? trim($input['id_tipo_servicio']) : '';

if (empty($id_tipo_servicio)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID del Tipo de Servicio es requerido"));
} else {
    $id_tipo_servicio = mysqli_real_escape_string($link, $id_tipo_servicio);
    
    $sql = "SELECT id_tipo_servicio, descripcion 
            FROM tipo_servicio 
            WHERE id_tipo_servicio = '$id_tipo_servicio'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Tipo de Servicio encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el Tipo de Servicio"));
    }
}

mysqli_close($link);
?>