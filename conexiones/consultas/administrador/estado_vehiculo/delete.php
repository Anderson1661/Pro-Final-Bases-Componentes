<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_estado_vehiculo = isset($_REQUEST['id_estado_vehiculo']) ? $_REQUEST['id_estado_vehiculo'] : '';
    
    if (empty($id_estado_vehiculo)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
    } else {
        $id_estado_vehiculo = mysqli_real_escape_string($link, $id_estado_vehiculo);
        
        $sql = "DELETE FROM estado_vehiculo WHERE id_estado_vehiculo='$id_estado_vehiculo'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Estado de vehículo eliminado correctamente"));
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

