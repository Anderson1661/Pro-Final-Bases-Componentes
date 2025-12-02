<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_tipo_usuario = isset($input['id_tipo_usuario']) ? trim($input['id_tipo_usuario']) : '';

if (empty($id_tipo_usuario)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID del Tipo de Usuario es requerido"));
} else {
    $id_tipo_usuario = mysqli_real_escape_string($link, $id_tipo_usuario);
    
    $sql = "SELECT id_tipo_usuario, descripcion 
            FROM tipo_usuario 
            WHERE id_tipo_usuario = '$id_tipo_usuario'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Tipo de Usuario encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el Tipo de Usuario"));
    }
}

mysqli_close($link);
?>