<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_linea = isset($_REQUEST['id_linea']) ? $_REQUEST['id_linea'] : '';
    $id_marca = isset($_REQUEST['id_marca']) ? $_REQUEST['id_marca'] : '';
    $id_linea_nuevo = isset($_REQUEST['id_linea_nuevo']) ? $_REQUEST['id_linea_nuevo'] : '';
    $id_marca_nuevo = isset($_REQUEST['id_marca_nuevo']) ? $_REQUEST['id_marca_nuevo'] : '';
    
    if (empty($id_linea) || empty($id_marca)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID línea e ID marca son requeridos"));
    } else if (empty($id_linea_nuevo) || empty($id_marca_nuevo)) {
        echo json_encode(array("success" => "0", "mensaje" => "Los nuevos valores son requeridos"));
    } else {
        $id_linea = mysqli_real_escape_string($link, $id_linea);
        $id_marca = mysqli_real_escape_string($link, $id_marca);
        $id_linea_nuevo = mysqli_real_escape_string($link, $id_linea_nuevo);
        $id_marca_nuevo = mysqli_real_escape_string($link, $id_marca_nuevo);
        
        $sql = "DELETE FROM linea_vehiculo WHERE id_linea='$id_linea' AND id_marca='$id_marca'";
        $res1 = mysqli_query($link, $sql);
        
        if ($res1) {
            $sql2 = "INSERT INTO linea_vehiculo (id_linea, id_marca) VALUES ('$id_linea_nuevo', '$id_marca_nuevo')";
            $res2 = mysqli_query($link, $sql2);
            
            if ($res2) {
                echo json_encode(array("success" => "1", "mensaje" => "Línea de vehículo actualizada correctamente"));
            } else {
                echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
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

