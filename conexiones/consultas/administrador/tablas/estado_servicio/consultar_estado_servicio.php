<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_estado_servicio = isset($input['id_estado_servicio']) ? trim($input['id_estado_servicio']) : '';

if (empty($id_estado_servicio)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID del estado de servicio es requerido"));
} else {
    $id_estado_servicio = mysqli_real_escape_string($link, $id_estado_servicio);
    
    // Consulta para obtener el estado de servicio
    $sql = "SELECT id_estado_servicio, descripcion 
            FROM estado_servicio 
            WHERE id_estado_servicio = '$id_estado_servicio'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Estado de servicio encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el estado de servicio"));
    }
}

mysqli_close($link);
?>