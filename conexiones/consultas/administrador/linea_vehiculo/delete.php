<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_linea = isset($_REQUEST['id_linea']) ? $_REQUEST['id_linea'] : '';
    $id_marca = isset($_REQUEST['id_marca']) ? $_REQUEST['id_marca'] : '';
    
    if (empty($id_linea) || empty($id_marca)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID línea e ID marca son requeridos"));
    } else {
        $id_linea = mysqli_real_escape_string($link, $id_linea);
        $id_marca = mysqli_real_escape_string($link, $id_marca);
        
        $sql = "DELETE FROM linea_vehiculo WHERE id_linea='$id_linea' AND id_marca='$id_marca'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Línea de vehículo eliminada correctamente"));
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

