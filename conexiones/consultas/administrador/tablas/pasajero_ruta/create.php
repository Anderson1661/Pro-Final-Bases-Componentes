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
$id_ruta = getRequestData('id_ruta');
$nombre_pasajero = getRequestData('nombre_pasajero');

if (empty($id_ruta) || empty($nombre_pasajero)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID ruta y nombre pasajero son requeridos"));
} else {
    $id_ruta = mysqli_real_escape_string($link, $id_ruta);
    $nombre_pasajero = mysqli_real_escape_string($link, $nombre_pasajero);
    
    $sql = "INSERT INTO pasajero_ruta (id_ruta, nombre_pasajero) VALUES ('$id_ruta', '$nombre_pasajero')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Pasajero de ruta registrado correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

