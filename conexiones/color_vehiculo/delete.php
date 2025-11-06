<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_color = isset($_REQUEST['id_color']) ? $_REQUEST['id_color'] : '';
    
    if (empty($id_color)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
    } else {
        $id_color = mysqli_real_escape_string($link, $id_color);
        
        $sql = "DELETE FROM color_vehiculo WHERE id_color='$id_color'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Color de vehículo eliminado correctamente"));
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

