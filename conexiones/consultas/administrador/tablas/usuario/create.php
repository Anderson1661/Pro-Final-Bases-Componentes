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
$id_tipo_usuario = getRequestData('id_tipo_usuario');
$correo = getRequestData('correo');
$contrasenia = getRequestData('contrasenia');

if (empty($id_tipo_usuario) || empty($correo) || empty($contrasenia)) {
    echo json_encode(array("success" => "0", "mensaje" => "Todos los campos son requeridos"));
} else {
    $id_tipo_usuario = mysqli_real_escape_string($link, $id_tipo_usuario);
    $correo = mysqli_real_escape_string($link, $correo);
    $contrasenia = mysqli_real_escape_string($link, $contrasenia);
    
    // Verificar si el correo ya existe
    $sql_verificar = "SELECT id_usuario FROM usuario WHERE correo = '$correo'";
    $res_verificar = mysqli_query($link, $sql_verificar);
    
    if (mysqli_num_rows($res_verificar) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "El correo electrónico ya está registrado"));
    } else {
        $sql = "INSERT INTO usuario (id_tipo_usuario, correo, contrasenia) VALUES ('$id_tipo_usuario', '$correo', '$contrasenia')";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            echo json_encode(array("success" => "1", "mensaje" => "Usuario registrado correctamente"));
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
        }
    }
}

mysqli_close($link);
?>