<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_estado_vehiculo = isset($input['id_estado_vehiculo']) ? trim($input['id_estado_vehiculo']) : '';

if (empty($id_estado_vehiculo)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID del estado de vehículo es requerido"));
} else {
    $id_estado_vehiculo = mysqli_real_escape_string($link, $id_estado_vehiculo);
    
    // Consulta para obtener el estado de vehículo
    $sql = "SELECT id_estado_vehiculo, descripcion 
            FROM estado_vehiculo 
            WHERE id_estado_vehiculo = '$id_estado_vehiculo'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Estado de vehículo encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el estado de vehículo"));
    }
}

mysqli_close($link);
?>