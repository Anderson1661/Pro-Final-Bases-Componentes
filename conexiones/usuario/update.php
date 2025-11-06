<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_usuario = isset($_REQUEST['id_usuario']) ? $_REQUEST['id_usuario'] : '';
    $id_tipo_usuario = isset($_REQUEST['id_tipo_usuario']) ? $_REQUEST['id_tipo_usuario'] : '';
    $correo = isset($_REQUEST['correo']) ? $_REQUEST['correo'] : '';
    $contrasenia = isset($_REQUEST['contrasenia']) ? $_REQUEST['contrasenia'] : '';
    
    if (empty($id_usuario)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
    } else if (empty($id_tipo_usuario) || empty($correo)) {
        echo json_encode(array("success" => "0", "mensaje" => "Tipo de usuario y correo son requeridos"));
    } else {
        $id_usuario = mysqli_real_escape_string($link, $id_usuario);
        $id_tipo_usuario = mysqli_real_escape_string($link, $id_tipo_usuario);
        $correo = mysqli_real_escape_string($link, $correo);
        
        // Si se proporciona contraseña, actualizarla también
        if (!empty($contrasenia)) {
            $contrasenia = mysqli_real_escape_string($link, $contrasenia);
            $sql = "UPDATE usuario SET id_tipo_usuario='$id_tipo_usuario', correo='$correo', contrasenia='$contrasenia' WHERE id_usuario='$id_usuario'";
        } else {
            $sql = "UPDATE usuario SET id_tipo_usuario='$id_tipo_usuario', correo='$correo' WHERE id_usuario='$id_usuario'";
        }
        
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Usuario actualizado correctamente"));
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

