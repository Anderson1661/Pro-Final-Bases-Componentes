<?php
include('../../../../config/conexion.php');
$link = Conectar();

// FUNCIÓN PARA LEER TANTO JSON COMO FORM-DATA
function getRequestData($key) {
    // Primero intenta leer de JSON
    $input = json_decode(file_get_contents('php://input'), true);
    if (isset($input[$key])) {
        return $input[$key];
    }
    
    // Si no viene en JSON, busca en $_REQUEST (form-data)
    return isset($_REQUEST[$key]) ? $_REQUEST[$key] : '';
}
$nombre_marca = getRequestData('nombre_marca');

if (empty($nombre_marca)) {
    echo json_encode(array("success" => "0", "mensaje" => "El nombre de la marca es requerido"));
} else {
    $nombre_marca = mysqli_real_escape_string($link, $nombre_marca);
    
    $sql = "INSERT INTO marca_vehiculo (nombre_marca) VALUES ('$nombre_marca')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Marca de vehículo registrada correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

