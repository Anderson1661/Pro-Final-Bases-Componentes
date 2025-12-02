<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_tipo_identificacion = isset($input['id_tipo_identificacion']) ? trim($input['id_tipo_identificacion']) : '';

if (empty($id_tipo_identificacion)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID del Tipo de Identificación es requerido"));
} else {
    $id_tipo_identificacion = mysqli_real_escape_string($link, $id_tipo_identificacion);
    
    $sql = "SELECT id_tipo_identificacion, descripcion 
            FROM tipo_identificacion 
            WHERE id_tipo_identificacion = '$id_tipo_identificacion'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Tipo de Identificación encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el Tipo de Identificación"));
    }
}

mysqli_close($link);
?>