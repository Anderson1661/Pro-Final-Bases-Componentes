<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_tipo_identificacion = isset($_REQUEST['id_tipo_identificacion']) ? $_REQUEST['id_tipo_identificacion'] : '';
    $descripcion = isset($_REQUEST['descripcion']) ? $_REQUEST['descripcion'] : '';
    
    if (empty($id_tipo_identificacion)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
    } else if (empty($descripcion)) {
        echo json_encode(array("success" => "0", "mensaje" => "La descripción es requerida"));
    } else {
        $id_tipo_identificacion = mysqli_real_escape_string($link, $id_tipo_identificacion);
        $descripcion = mysqli_real_escape_string($link, $descripcion);
        
        $sql = "UPDATE tipo_identificacion SET descripcion='$descripcion' WHERE id_tipo_identificacion='$id_tipo_identificacion'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Tipo de identificación actualizado correctamente"));
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

