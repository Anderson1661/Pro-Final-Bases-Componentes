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

// USAR LA FUNCIÓN EN LUGAR DE $_REQUEST
$descripcion = getRequestData('descripcion');
$valor_km = getRequestData('valor_km');

if (empty($descripcion) || empty($valor_km)) {
    echo json_encode(array("success" => "0", "mensaje" => "La descripción y el valor por km son requeridos"));
} else {
    $descripcion = mysqli_real_escape_string($link, $descripcion);
    $valor_km = mysqli_real_escape_string($link, $valor_km);
    
    $sql = "INSERT INTO categoria_servicio (descripcion, valor_km) VALUES ('$descripcion', '$valor_km')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Categoría de servicio registrada correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>