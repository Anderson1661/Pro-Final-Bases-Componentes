<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_pregunta = isset($_REQUEST['id_pregunta']) ? $_REQUEST['id_pregunta'] : '';
    $id_usuario = isset($_REQUEST['id_usuario']) ? $_REQUEST['id_usuario'] : '';
    
    if (empty($id_pregunta) || empty($id_usuario)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID pregunta e ID usuario son requeridos"));
    } else {
        $id_pregunta = mysqli_real_escape_string($link, $id_pregunta);
        $id_usuario = mysqli_real_escape_string($link, $id_usuario);
        
        $sql = "DELETE FROM respuestas_seguridad WHERE id_pregunta='$id_pregunta' AND id_usuario='$id_usuario'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Respuesta de seguridad eliminada correctamente"));
            } else {
                echo json_encode(array("success" => "0", "mensaje" => "No se encontró el registro"));
            }
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "Error al eliminar: " . mysqli_error($link)));
        }
    }
    mysqli_close($link);
} else {
    echo json_encode(array("success" => "0", "mensaje" => "Método no permitido"));
}
?>

