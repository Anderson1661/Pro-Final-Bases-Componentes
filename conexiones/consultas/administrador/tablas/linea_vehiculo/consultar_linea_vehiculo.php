<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_linea = isset($input['id_linea']) ? trim($input['id_linea']) : '';
$id_marca = isset($input['id_marca']) ? trim($input['id_marca']) : '';

if (empty($id_linea) || empty($id_marca)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID de línea y el ID de marca son requeridos"));
} else {
    $id_linea = mysqli_real_escape_string($link, $id_linea);
    $id_marca = mysqli_real_escape_string($link, $id_marca);
    
    // Consulta para obtener la línea de vehículo
    $sql = "SELECT id_linea, id_marca 
            FROM linea_vehiculo 
            WHERE id_linea = '$id_linea' AND id_marca = '$id_marca'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        // Los datos recuperados son los datos originales para la edición
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Línea de vehículo encontrada"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró la línea de vehículo"));
    }
}

mysqli_close($link);
?>