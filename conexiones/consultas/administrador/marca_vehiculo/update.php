<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_marca = isset($_REQUEST['id_marca']) ? $_REQUEST['id_marca'] : '';
    $nombre_marca = isset($_REQUEST['nombre_marca']) ? $_REQUEST['nombre_marca'] : '';
    
    if (empty($id_marca)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
    } else if (empty($nombre_marca)) {
        echo json_encode(array("success" => "0", "mensaje" => "El nombre de la marca es requerido"));
    } else {
        $id_marca = mysqli_real_escape_string($link, $id_marca);
        $nombre_marca = mysqli_real_escape_string($link, $nombre_marca);
        
        $sql = "UPDATE marca_vehiculo SET nombre_marca='$nombre_marca' WHERE id_marca='$id_marca'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Marca de vehículo actualizada correctamente"));
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

