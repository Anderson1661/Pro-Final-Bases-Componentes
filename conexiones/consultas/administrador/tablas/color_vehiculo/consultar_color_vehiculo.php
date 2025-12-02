<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_color = isset($input['id_color']) ? trim($input['id_color']) : '';

if (empty($id_color)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID del color es requerido"));
} else {
    $id_color = mysqli_real_escape_string($link, $id_color);
    
    $sql = "SELECT id_color, descripcion 
            FROM color_vehiculo 
            WHERE id_color = '$id_color'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Color encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el color"));
    }
}

mysqli_close($link);
?>