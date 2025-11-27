<?php
include('../../../../config/conexion.php');
$link = Conectar();

$id_pregunta = isset($_REQUEST['id_pregunta']) ? $_REQUEST['id_pregunta'] : '';
$id_usuario = isset($_REQUEST['id_usuario']) ? $_REQUEST['id_usuario'] : '';
$respuesta_pregunta = isset($_REQUEST['respuesta_pregunta']) ? $_REQUEST['respuesta_pregunta'] : '';

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

