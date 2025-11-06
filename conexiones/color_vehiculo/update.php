<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_color = isset($_REQUEST['id_color']) ? $_REQUEST['id_color'] : '';
    $descripcion = isset($_REQUEST['descripcion']) ? $_REQUEST['descripcion'] : '';
    
    if (empty($id_color)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
    } else if (empty($descripcion)) {
        echo json_encode(array("success" => "0", "mensaje" => "La descripción es requerida"));
    } else {
        $id_color = mysqli_real_escape_string($link, $id_color);
        $descripcion = mysqli_real_escape_string($link, $descripcion);
        
        $sql = "UPDATE color_vehiculo SET descripcion='$descripcion' WHERE id_color='$id_color'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Color de vehículo actualizado correctamente"));
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

