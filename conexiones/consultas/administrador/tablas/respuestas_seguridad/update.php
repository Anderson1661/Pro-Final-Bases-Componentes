<?php
// consultas/administrador/tablas/respuestas_seguridad/update.php

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Ajusta la ruta a tu archivo de conexión. Asumo 5 niveles atrás para llegar a config.
    include('../../../../config/conexion.php'); 
    $link = Conectar();
    
    // Obtener datos del cuerpo JSON
    $input = json_decode(file_get_contents('php://input'), true);
    
    // Clave primaria compuesta
    $id_pregunta = isset($input['id_pregunta']) ? $input['id_pregunta'] : '';
    $id_usuario = isset($input['id_usuario']) ? $input['id_usuario'] : '';
    
    // Campo editable
    $respuesta_pregunta = isset($input['respuesta_pregunta']) ? $input['respuesta_pregunta'] : ''; 
    
    if (empty($id_pregunta) || empty($id_usuario)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID de pregunta e ID de usuario son requeridos"));
    } else if (empty($respuesta_pregunta)) {
        echo json_encode(array("success" => "0", "mensaje" => "La respuesta es requerida"));
    } else {
        $id_pregunta = mysqli_real_escape_string($link, $id_pregunta);
        $id_usuario = mysqli_real_escape_string($link, $id_usuario);
        $respuesta_pregunta = mysqli_real_escape_string($link, $respuesta_pregunta);
        
        // El UPDATE debe usar ambos campos de la clave primaria compuesta
        $sql = "UPDATE respuestas_seguridad SET respuesta_pregunta='$respuesta_pregunta' 
                WHERE id_pregunta='$id_pregunta' AND id_usuario='$id_usuario'";
        
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Respuesta de seguridad actualizada correctamente"));
            } else {
                echo json_encode(array("success" => "0", "mensaje" => "No se realizó ningún cambio o no se encontró el registro."));
            }
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
        }
    }
    mysqli_close($link);
} else {
    echo json_encode(array("success" => "0", "mensaje" => "Método no permitido. Use POST"));
}
?>