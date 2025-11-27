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
$id_cliente = getRequestData('id_cliente');
$telefono = getRequestData('telefono');

if (empty($id_cliente) || empty($telefono)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID cliente y teléfono son requeridos"));
} else {
    $id_cliente = mysqli_real_escape_string($link, $id_cliente);
    $telefono = mysqli_real_escape_string($link, $telefono);
    
    $sql = "INSERT INTO telefono_cliente (id_cliente, telefono) VALUES ('$id_cliente', '$telefono')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Teléfono de cliente registrado correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>