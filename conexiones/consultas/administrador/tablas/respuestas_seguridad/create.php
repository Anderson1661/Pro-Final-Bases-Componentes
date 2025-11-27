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
$id_pregunta = getRequestData('id_pregunta');
$id_usuario = getRequestData('id_usuario');
$respuesta_pregunta = getRequestData('respuesta_pregunta');

if (empty($id_pregunta) || empty($id_usuario) || empty($respuesta_pregunta)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID pregunta, ID usuario y respuesta son requeridos"));
} else {
    $id_pregunta = mysqli_real_escape_string($link, $id_pregunta);
    $id_usuario = mysqli_real_escape_string($link, $id_usuario);
    $respuesta_pregunta = mysqli_real_escape_string($link, $respuesta_pregunta);
    
    $sql = "INSERT INTO respuestas_seguridad (id_pregunta, id_usuario, respuesta_pregunta) VALUES ('$id_pregunta', '$id_usuario', '$respuesta_pregunta')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Respuesta de seguridad registrada correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

