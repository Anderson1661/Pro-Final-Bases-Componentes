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
$id_codigo_postal = getRequestData('id_codigo_postal');
$id_pais = getRequestData('id_pais');
$departamento = getRequestData('departamento');
$ciudad = getRequestData('ciudad');

if (empty($id_codigo_postal) || empty($id_pais) || empty($departamento) || empty($ciudad)) {
    echo json_encode(array("success" => "0", "mensaje" => "Todos los campos son requeridos"));
} else {
    $id_codigo_postal = mysqli_real_escape_string($link, $id_codigo_postal);
    $id_pais = mysqli_real_escape_string($link, $id_pais);
    $departamento = mysqli_real_escape_string($link, $departamento);
    $ciudad = mysqli_real_escape_string($link, $ciudad);
    
    $sql = "INSERT INTO codigo_postal (id_codigo_postal, id_pais, departamento, ciudad) VALUES ('$id_codigo_postal', '$id_pais', '$departamento', '$ciudad')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Código postal registrado correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

