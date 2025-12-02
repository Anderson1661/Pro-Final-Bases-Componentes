<?php
include('../../../../config/conexion.php'); // Asegúrate de ajustar la ruta de 'conexion.php' si es necesario
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_pregunta = isset($input['id_pregunta']) ? trim($input['id_pregunta']) : '';

if (empty($id_pregunta)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID de la pregunta es requerido"));
} else {
    $id_pregunta = mysqli_real_escape_string($link, $id_pregunta);
    
    $sql = "SELECT id_pregunta, descripcion
            FROM preguntas_seguridad
            WHERE id_pregunta = '$id_pregunta'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Pregunta de seguridad encontrada"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró la pregunta de seguridad"));
    }
}

mysqli_close($link);
?>