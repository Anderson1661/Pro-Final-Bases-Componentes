<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_marca = isset($input['id_marca']) ? trim($input['id_marca']) : '';

if (empty($id_marca)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID de la marca de vehículo es requerido"));
} else {
    $id_marca = mysqli_real_escape_string($link, $id_marca);
    
    // Consulta para obtener la marca de vehículo
    $sql = "SELECT id_marca, nombre_marca 
            FROM marca_vehiculo 
            WHERE id_marca = '$id_marca'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Marca de vehículo encontrada"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró la marca de vehículo"));
    }
}

mysqli_close($link);
?>