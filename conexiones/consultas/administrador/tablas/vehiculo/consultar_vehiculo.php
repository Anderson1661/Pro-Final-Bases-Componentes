<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$placa = isset($input['placa']) ? trim($input['placa']) : '';

if (empty($placa)) {
    echo json_encode(array("success" => "0", "mensaje" => "La Placa del Vehículo es requerida"));
} else {
    $placa = mysqli_real_escape_string($link, $placa);
    
    $sql = "SELECT placa, linea_vehiculo, modelo, id_color, id_marca, id_tipo_servicio, id_estado_vehiculo 
            FROM vehiculo 
            WHERE placa = '$placa'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Vehículo encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el Vehículo"));
    }
}

mysqli_close($link);
?>