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
$id_linea = getRequestData('id_linea');
$id_marca = getRequestData('id_marca');

if (empty($id_linea) || empty($id_marca)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID línea e ID marca son requeridos"));
} else {
    $id_linea = mysqli_real_escape_string($link, $id_linea);
    $id_marca = mysqli_real_escape_string($link, $id_marca);
    
    $sql = "INSERT INTO linea_vehiculo (id_linea, id_marca) VALUES ('$id_linea', '$id_marca')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Línea de vehículo registrada correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

