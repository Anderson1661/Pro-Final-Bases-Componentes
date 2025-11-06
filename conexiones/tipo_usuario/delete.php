<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_tipo_usuario = isset($_REQUEST['id_tipo_usuario']) ? $_REQUEST['id_tipo_usuario'] : '';
    
    if (empty($id_tipo_usuario)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
    } else {
        $id_tipo_usuario = mysqli_real_escape_string($link, $id_tipo_usuario);
        
        $sql = "DELETE FROM tipo_usuario WHERE id_tipo_usuario='$id_tipo_usuario'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Tipo de usuario eliminado correctamente"));
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

