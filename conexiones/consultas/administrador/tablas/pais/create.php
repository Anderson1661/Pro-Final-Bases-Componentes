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

$nombre = getRequestData('nombre');

// EL RESTO DEL CÓDIGO PERMANECE IGUAL...
if (empty($nombre)) {
    echo json_encode(array("success" => "0", "mensaje" => "El nombre es requerido"));
} else {
    $nombre = mysqli_real_escape_string($link, $nombre);
    
    $sql = "INSERT INTO pais (nombre) VALUES ('$nombre')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "País registrado correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

