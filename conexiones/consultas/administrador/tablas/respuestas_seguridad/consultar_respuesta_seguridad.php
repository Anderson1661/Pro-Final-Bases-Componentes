<?php
// consultas/administrador/tablas/respuestas_seguridad/consultar_respuesta_seguridad.php

include('../../../../config/conexion.php'); // Ajusta la ruta
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_pregunta = isset($input['id_pregunta']) ? trim($input['id_pregunta']) : '';
$id_usuario = isset($input['id_usuario']) ? trim($input['id_usuario']) : '';

if (empty($id_pregunta) || empty($id_usuario)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID de pregunta e ID de usuario son requeridos"));
} else {
    $id_pregunta = mysqli_real_escape_string($link, $id_pregunta);
    $id_usuario = mysqli_real_escape_string($link, $id_usuario);
    
    $sql = "SELECT id_pregunta, id_usuario, respuesta_pregunta
            FROM respuestas_seguridad
            WHERE id_pregunta = '$id_pregunta' AND id_usuario = '$id_usuario'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Respuesta de seguridad encontrada"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró la respuesta de seguridad para la clave compuesta"));
    }
}

mysqli_close($link);
?>