<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_pregunta = isset($_REQUEST['id_pregunta']) ? $_REQUEST['id_pregunta'] : '';
    $id_usuario = isset($_REQUEST['id_usuario']) ? $_REQUEST['id_usuario'] : '';
    $respuesta_pregunta = isset($_REQUEST['respuesta_pregunta']) ? $_REQUEST['respuesta_pregunta'] : '';
    
    if (empty($id_pregunta) || empty($id_usuario)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID pregunta e ID usuario son requeridos"));
    } else if (empty($respuesta_pregunta)) {
        echo json_encode(array("success" => "0", "mensaje" => "La respuesta es requerida"));
    } else {
        $id_pregunta = mysqli_real_escape_string($link, $id_pregunta);
        $id_usuario = mysqli_real_escape_string($link, $id_usuario);
        $respuesta_pregunta = mysqli_real_escape_string($link, $respuesta_pregunta);
        
        $sql = "UPDATE respuestas_seguridad SET respuesta_pregunta='$respuesta_pregunta' 
                WHERE id_pregunta='$id_pregunta' AND id_usuario='$id_usuario'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Respuesta de seguridad actualizada correctamente"));
            } else {
                echo json_encode(array("success" => "0", "mensaje" => "No se encontró el registro"));
            }
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
        }
    }
    mysqli_close($link);
} else {
    echo json_encode(array("success" => "0", "mensaje" => "Método no permitido"));
}
?>

