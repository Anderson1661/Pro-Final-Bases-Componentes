<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_usuario = isset($input['id_usuario']) ? trim($input['id_usuario']) : '';

if (empty($id_usuario)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID del Usuario es requerido"));
} else {
    $id_usuario = mysqli_real_escape_string($link, $id_usuario);
    
    $sql = "SELECT id_usuario, id_tipo_usuario, correo, contrasenia 
            FROM usuario 
            WHERE id_usuario = '$id_usuario'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        
        // La contraseña original es necesaria para verificar si se ingresó una nueva.
        // Se envía en el JSON, pero Kotlin no debe mostrarla en el campo de texto.
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Usuario encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el Usuario"));
    }
}

mysqli_close($link);
?>