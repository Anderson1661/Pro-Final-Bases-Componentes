<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../../../../config/conexion.php'); // Asegúrate de ajustar la ruta de 'conexion.php' si es necesario
    $link = Conectar();
    
    // Obtener datos del cuerpo JSON
    $input = json_decode(file_get_contents('php://input'), true);
    
    $id_pregunta = isset($input['id_pregunta']) ? $input['id_pregunta'] : '';
    $descripcion = isset($input['descripcion']) ? $input['descripcion'] : ''; // Nuevo valor
    
    if (empty($id_pregunta) || empty($descripcion)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID de pregunta y descripción son requeridos"));
    } else {
        $id_pregunta = mysqli_real_escape_string($link, $id_pregunta);
        $descripcion = mysqli_real_escape_string($link, $descripcion);
        
        // 1. Verificar unicidad de la nueva descripción (excepto para el propio registro)
        $sql_check = "SELECT id_pregunta FROM preguntas_seguridad WHERE descripcion = '$descripcion' AND id_pregunta != '$id_pregunta'";
        $res_check = mysqli_query($link, $sql_check);

        if (mysqli_num_rows($res_check) > 0) {
            echo json_encode(array("success" => "0", "mensaje" => "La descripción de la pregunta ya existe."));
            mysqli_close($link);
            exit;
        }

        // 2. Realizar el UPDATE
        $sql = "UPDATE preguntas_seguridad SET descripcion='$descripcion' WHERE id_pregunta='$id_pregunta'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Pregunta de seguridad actualizada correctamente"));
            } else {
                echo json_encode(array("success" => "0", "mensaje" => "No se realizó ningún cambio o no se encontró el ID."));
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